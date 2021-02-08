package nl.tudelft.instrumentation.branch;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import nl.tudelft.instrumentation.line.LineCoverageTracker;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class implements a small line-coverage instrumentation tool. It relies on {@code JavaParser} for parsing Java
 * source code files and to visit the corresponding AST.
 *
 * @author Annibale Panichella
 */
public class BranchCoverageVisitor extends ModifierVisitor<Object> {

    /** Name of the source file to instrument */
    private String filename;

    public BranchCoverageVisitor(String filename) {
        this.filename = filename;
    }

    /**
     * For if statement, we add coverage tracking in both true ({@code thenStmt}) and false {@code elseStmt} branches
     */
    @Override
    public Node visit(IfStmt node, Object arg) {
        if (node.hasThenBlock()){
            BlockStmt block = (BlockStmt) node.getThenStmt();
            Statement track = makeCoverageTrackingCall(filename, node.getBegin().get().line, true);
            block.addStatement(0, track);
        } else {
            // creating block when it doesn't exist
            BlockStmt block = new BlockStmt();

            // old then statement
            Statement child = node.getThenStmt();

            // replace old "then statement" with new block
            node.replace(child, block);

            // 1. Add coverage tracking statement
            block.addStatement(makeCoverageTrackingCall(filename, node.getBegin().get().line, true));

            // 2. Add existing statement
            block.addStatement(child);
            child.setParentNode(block);
        }

        if (node.hasElseBranch()){
            Statement elseStmt = node.getElseStmt().get();
            if (elseStmt instanceof BlockStmt){
                BlockStmt block = (BlockStmt) node.getElseStmt().get();
                block.addStatement(0, makeCoverageTrackingCall(filename, node.getBegin().get().line, false));
            } else {
                // creating block when it doesn't exist
                BlockStmt block = new BlockStmt();

                // old else statement
                Statement child = node.getElseStmt().get();

                // replace old "else statement" with new block
                node.replace(child, block);

                // 1. Add coverage tracking statement
                block.addStatement(makeCoverageTrackingCall(filename, node.getBegin().get().line, false));

                // 2. Add existing statement
                block.addStatement(child);
                child.setParentNode(block);
            }
        } else {
            BlockStmt block = new BlockStmt();
            block.addStatement(makeCoverageTrackingCall(filename, node.getBegin().get().line, false));
            node.setElseStmt(block);
            block.setParentNode(node);
        }

        return (Node) super.visit(node, arg);
    }


    /**
     * For loops (for and while) We need to keep track of coverage for the entry block and
     * the exit case
     */
    public void instrumentLoopBody(NodeWithBody node){
        int line = -1;
        Node parent = null;
        if (node instanceof ForStmt) {
            line = ((ForStmt) node).getBegin().get().line;
            parent = ((ForStmt) node).getParentNode().get();
        }
        if (node instanceof WhileStmt) {
            line = ((WhileStmt) node).getBegin().get().line;
            parent = ((WhileStmt) node).getParentNode().get();
        }
        if (node instanceof ForEachStmt) {
            line = ((ForEachStmt) node).getBegin().get().line;
            parent = ((ForEachStmt) node).getParentNode().get();
        }

        // add coverage track within the loop body
        Statement stmt = node.getBody();
        if (stmt instanceof BlockStmt){
            BlockStmt block = (BlockStmt) stmt;
            block.addStatement(0, makeCoverageTrackingCall(filename, line, true));
        } else {
            BlockStmt block = new BlockStmt();
            block.addStatement(makeCoverageTrackingCall(filename, line, true));
            block.addStatement(node.getBody());

            block.setParentNode((Node) node);
            node.setBody(block);
        }

        // add coverage track just after the for body
        if (parent instanceof BlockStmt){
            BlockStmt block = (BlockStmt) parent;
            int position = block.getStatements().indexOf(node);
            block.addStatement(position+1, makeCoverageTrackingCall(filename, line, false));
        } else {
            // TODO: this branch should be infeasible
        }
    }

    @Override
    public Node visit(ForStmt node, Object arg) {
        this.instrumentLoopBody(node);
        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(WhileStmt node, Object arg) {
        this.instrumentLoopBody(node);
        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(ForEachStmt node, Object arg) {
        this.instrumentLoopBody(node);
        return (Node) super.visit(node, arg);
    }

    /**
     * We need to keep track of each case in the body of  {@code SwitchStmt}
     */
    @Override
    public Node visit(SwitchStmt node, Object arg) {
        for (SwitchEntry entry : node.getEntries()){
            NodeList<Statement> stmt = entry.getStatements();
            stmt.add(0,  makeCoverageTrackingCall(filename, entry.getBegin().get().line, true));
        }
        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(ConditionalExpr node, Object arg){
        ClassOrInterfaceDeclaration clazz = node.findAncestor(ClassOrInterfaceDeclaration.class).get();
        Type type = this.inferType(node);

        MethodCallExpr call = createMethodCall(node, true, clazz);
        node.replace(node.getThenExpr(), call);

        MethodCallExpr call2 = createMethodCall(node, false, clazz);
        node.replace(node.getElseExpr(), call2);

        return (Node) super.visit(node, arg);
    }

    protected MethodCallExpr createMethodCall(ConditionalExpr node, boolean branch, ClassOrInterfaceDeclaration clazz){
        Expression expr = null;
        if (branch)
            expr = node.getThenExpr();
        else
            expr = node.getElseExpr();

        int line = node.getBegin().get().line;
        String methodName = "condition_"+ line + "_" + String.valueOf(branch);

        NodeList<Modifier> modifier = new NodeList<>(Modifier.privateModifier());
        modifier.add(Modifier.staticModifier());
        MethodDeclaration method = new MethodDeclaration(modifier,
                this.inferType(node),
                methodName);
        BlockStmt block = new BlockStmt();

        ReturnStmt returnStmt = new ReturnStmt();
        returnStmt.setExpression(expr);
        block.addStatement(makeCoverageTrackingCall(this.filename, line, branch));
        block.addStatement(returnStmt);
        method.setBody(block);

        clazz.addMember(method);
        MethodCallExpr call = new MethodCallExpr(clazz.getNameAsExpression(), methodName);

        return call;
    }

    protected Type inferType(ConditionalExpr expr){
        Node parent = expr.getParentNode().get();
        if (parent instanceof VariableDeclarator){
            VariableDeclarator var = (VariableDeclarator) parent;
            return var.getType();
        } else if (parent instanceof  AssignExpr){
            AssignExpr assign = (AssignExpr) parent;
            NameExpr name = (NameExpr) assign.getTarget();

            // Option 1: the variable is one of the method attributes
            MethodDeclaration method = expr.findAncestor(MethodDeclaration.class).get();
            Optional<Parameter> parameter = method.getParameterByName(name.getName().getIdentifier());
            if (parameter.isPresent()){

                return new VoidType();
            }

            // Option 2: the variable is declared within the method body
            List<VariableDeclarator> list = method.findAll(VariableDeclarator.class);
            for (VariableDeclarator declaration : list){
                if (declaration.getName().getIdentifier().equals(name.getName().getIdentifier()))
                    return declaration.getType();
            }

            // Option 3: the used variable is one of the class attributes
            ClassOrInterfaceDeclaration clazz = expr.findAncestor(ClassOrInterfaceDeclaration.class).get();
            List<FieldDeclaration> fields = clazz.getFields();
            for (FieldDeclaration field : fields){
                for (VariableDeclarator var : field.getVariables()){
                    if (var.getName().getIdentifier().equals(name.getName().getIdentifier()))
                        return var.getType();
                }
            }
        }

        // TODO: This line should be unreachable
        return new VoidType();
    }

    /**
     * This methods add a new statement to keep track of branch coverage
     * @param filename name of the Java file we instrumented
     * @param line the line of the condition point we want to keep track of (for coverage)
     * @param branch true or false branch
     * @return a new {@code Statement} that corresponds to invoking the method
     * {@code BranchCoverageTracker#updateCoverage}.
     */
    private Statement makeCoverageTrackingCall(String filename, int line, boolean branch) {
        // register new line to keep track of
        BranchCoverageTracker.registerLine(filename, line, branch);

        // create a method call to LineCoverageTracker
        NameExpr coverageTracker = new NameExpr("nl.tudelft.instrumentation.branch.BranchCoverageTracker");
        MethodCallExpr call = new MethodCallExpr(coverageTracker, "updateCoverage");
        call.addArgument(new StringLiteralExpr(filename));
        call.addArgument(new IntegerLiteralExpr(String.valueOf(line)));
        call.addArgument(new IntegerLiteralExpr(String.valueOf(branch)));

        // return the newly created method call
        return new ExpressionStmt(call);
    }
}
