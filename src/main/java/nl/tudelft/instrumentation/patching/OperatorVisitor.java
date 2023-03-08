package nl.tudelft.instrumentation.patching;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.body.*;

import java.util.LinkedList;

/**
 * This class implements a small line-coverage instrumentation tool. It relies on {@code JavaParser} for parsing Java
 * source code files and to visit the corresponding AST.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class OperatorVisitor extends ModifierVisitor<Object> {
    /** Name of the source file to instrument */
    private String filename;

    private String pathFile = "OperatorTracker";

    private String class_name = "";

    int operator_nr = 0;

    LinkedList<String> operators = new LinkedList<String>();

    public OperatorVisitor(String filename) {
        this.filename = filename;
    }

    public Node addCode(Statement node, Statement new_statement, Object args){
        if (node.getParentNode().isPresent()){

            Node parent = node.getParentNode().get();

            if (parent instanceof BlockStmt) {
                // if {@code node} is within a BlockStmt (i.e., withing a block with
                // open-close curly brackets), we just add the new line for coverage tracking
                BlockStmt block = (BlockStmt) parent;
                int line = node.getBegin().get().line;
                int position = block.getStatements().indexOf(node);
                block.addStatement(position, new_statement);
            } else {
                // if {@code node} is not within a BlockStmt (e.g., true branch of an if condition
                // with no curly brackets), we need to create a BlockStmt first
                BlockStmt block = new BlockStmt();
                block.addStatement(new_statement);
                block.addStatement(node);
                return block;
            }
        }
        return node;
    }

    public Node addCodeAfter(Statement node, Statement new_statement, Object args){
        if (node.getParentNode().isPresent()){

            Node parent = node.getParentNode().get();

            if (parent instanceof BlockStmt) {
                // if {@code node} is within a BlockStmt (i.e., withing a block with
                // open-close curly brackets), we just add the new line for coverage tracking
                BlockStmt block = (BlockStmt) parent;
                int line = node.getBegin().get().line;
                int position = block.getStatements().indexOf(node) + 1;
                block.addStatement(position, new_statement);
            } else {
                // if {@code node} is not within a BlockStmt (e.g., true branch of an if condition
                // with no curly brackets), we need to create a BlockStmt first
                BlockStmt block = new BlockStmt();
                block.addStatement(node);
                block.addStatement(new_statement);
                return block;
            }
        }
        return node;
    }

    public void setOperatorList(Expression node, Statement stat, Object args){
        while(node instanceof EnclosedExpr) node = ((EnclosedExpr)node).getInner();

        if(node instanceof BinaryExpr){
            BinaryExpr bine = (BinaryExpr) node;
            setOperatorList(bine.getLeft(), stat, args);
            setOperatorList(bine.getRight(), stat, args);
            String operator = bine.getOperator().asString();
            if(operator.equals(">") || operator.equals("<") || operator.equals("<=") || operator.equals(">=") || operator.equals("==") || operator.equals("!=")){
                NodeList<Expression> node_list = new NodeList<Expression>();
                node_list.add(new StringLiteralExpr(operator));
                node_list.add(bine.getLeft().clone());
                node_list.add(bine.getRight().clone());
                node_list.add(new IntegerLiteralExpr(operator_nr++));
                MethodCallExpr myop = new MethodCallExpr(new NameExpr(pathFile),"myOperator",node_list);
                node.replace(myop);
                operators.add(operator);
            }
        } else if(node instanceof MethodCallExpr){
            MethodCallExpr mete = (MethodCallExpr) node;
            NodeList<Expression> arguments = new NodeList<Expression>();
            for(int i = 0; i < mete.getArguments().size(); ++i){
                setOperatorList(mete.getArguments().get(i), stat, args);
            }
        } else if(node instanceof ArrayAccessExpr){
            ArrayAccessExpr aae = (ArrayAccessExpr) node;
            setOperatorList(aae.getIndex(), stat, args);
        } else if(node instanceof UnaryExpr){
            UnaryExpr ue = (UnaryExpr) node;
            setOperatorList(ue.getExpression(), stat, args);
        } else if(node instanceof ConditionalExpr){
            ConditionalExpr ce = (ConditionalExpr) node;
            setOperatorList(ce.getCondition(), stat, args);
            setOperatorList(ce.getThenExpr(), stat, args);
            setOperatorList(ce.getElseExpr(), stat, args);
        }
    }

    /**
     * We need to keep track of coverage for the overall {@code IfStmt} but also
     * for all statements in its body: {@code thenStmt}, {@code elseStmt},
     * and {@code Expression}
     */
    @Override
    public Node visit(IfStmt node, Object arg) {
        this.setOperatorList(node.getCondition(), node, arg);
        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(ClassOrInterfaceDeclaration node, Object arg){
        this.class_name = node.getName().toString();
        BodyDeclaration bd1 = StaticJavaParser.parseBodyDeclaration("public Void call(){ " + class_name + " cp = new " + class_name + "(); for(String s : sequence){ try { cp.calculateOutput(s); } catch (Exception e) { PatchingLab.output(\"Invalid input: \" + e.getMessage()); } } return null;}");
        BodyDeclaration bd2 = StaticJavaParser.parseBodyDeclaration(" public void setSequence(String[] trace){ sequence = trace; } ");
        BodyDeclaration fd = StaticJavaParser.parseBodyDeclaration("public String[] sequence;");
        node.getMembers().add(fd);
        node.getMembers().add(bd1);
        node.getMembers().add(bd2);
        node.addImplementedType("CallableTraceRunner<Void>");
        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(ExpressionStmt node, Object arg) {
        if (node.getExpression() instanceof VariableDeclarationExpr) {
            //System.out.println(node.toString());
            if (node.toString().contains("eca =") && !operators.isEmpty()) {
                String operator_string = "{ \"" + operators.pop() + "\"";
                for(String op : operators){
                    operator_string = operator_string + ", \"" + op + "\"";
                }
                operator_string = operator_string + "}";
                Statement staticStatement = StaticJavaParser.parseStatement(pathFile + ".run(operators, eca);");
                this.addCodeAfter(node, staticStatement, arg);
                staticStatement = StaticJavaParser.parseStatement("String[] operators = "+ operator_string + ";");
                this.addCodeAfter(node, staticStatement, arg);
            }
        }

        if (node.getExpression() instanceof MethodCallExpr) {
            MethodCallExpr mce = (MethodCallExpr)node.getExpression();
            if (mce.toString().contains("System.out")) {
                node.setExpression(
                        new MethodCallExpr(
                                new NameExpr(pathFile),"output",mce.getArguments()
                        )
                );
            }
        }

        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(CatchClause node, Object arg){
        Parameter staticParam = StaticJavaParser.parseParameter("Exception e");
        node.setParameter(staticParam);
        return (Node) super.visit(node, arg);
    }

    /**
     * Method to add import statements in the top of the source file.
     * @param node the node that defines the root of the source file.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(CompilationUnit node, Object arg) {
        node.addImport("nl.tudelft.instrumentation.patching.*");
        node.addImport("nl.tudelft.instrumentation.runner.CallableTraceRunner");
        return (Node) super.visit(node, arg);
    }

    @Override
    public Node visit(WhileStmt node, Object arg) {
        Node parent = node.getParentNode().get();
        parent.remove(node);
        return node;
    }
}

