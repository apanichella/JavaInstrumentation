package nl.tudelft.instrumentation.line;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.ModifierVisitor;

/**
 * This class implements a small line-coverage instrumentation tool. It relies on {@code JavaParser} for parsing Java
 * source code files and to visit the corresponding AST.
 *
 * @author Annibale Panichella
 */
public class LineCoverageVisitor extends ModifierVisitor<Object> {

    /** Name of the source file to instrument */
    private String filename;

    public LineCoverageVisitor(String filename) {
        this.filename = filename;
    }

    /** This method adds a new line in the source file under analysis to keep track of
     * covered line at execution time
     * @param node {@code Statement} to instrument
     * @param args additional arguments of JavaParser
     * @return {@code BlockStmt} that includes the {@code node} (statement in input) and the additional statement
     * for code coverage analysis
     */
    public Node addInstrumentationCode(Statement node, Object args){
        if (node.getParentNode().isPresent()){
            Node parent = node.getParentNode().get();

            if (parent instanceof BlockStmt) {
                // if {@code node} is within a BlockStmt (i.e., withing a block with
                // open-close curly brackets), we just add the new line for coverage tracking
                BlockStmt block = (BlockStmt) parent;

                int line = node.getBegin().get().line;
                int position = block.getStatements().indexOf(node);
                block.addStatement(position, makeCoverageTrackingCall(filename, line));
            } else {
                // if {@code node} is not within a BlockStmt (e.g., true branch of an if condition
                // with no curly brackets), we need to create a BlockStmt first
                BlockStmt block = new BlockStmt();
                block.addStatement(makeCoverageTrackingCall(filename, node.getBegin().get().line));
                block.addStatement(node);
                return block;
            }
        }
        return node;
    }

    @Override
    public Node visit(ThrowStmt node, Object arg){
        return this.addInstrumentationCode(node, arg);
    }

    @Override
    public Node visit(ReturnStmt node, Object arg) {
        return this.addInstrumentationCode(node, arg);
    }

    @Override
    public Node visit(ExpressionStmt node, Object arg) {
        return this.addInstrumentationCode(node, arg);
    }

    /**
     * We need to keep track of coverage for the overall {@code ForStmt} but also
     * for all statements in its body
     */
    @Override
    public Node visit(ForStmt node, Object arg) {
        this.addInstrumentationCode(node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * We need to keep track of coverage for the overall {@code IfStmt} but also
     * for all statements in its body: {@code thenStmt}, {@code elseStmt},
     * and {@code Expression}
     */
    @Override
    public Node visit(IfStmt node, Object arg) {
        this.addInstrumentationCode(node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * We need to keep track of coverage for the overall {@code SwitchStmt} but also
     * for all statements in its body: {@code Expression} and {@code SwitchEntry}.
     */
    @Override
    public Node visit(SwitchStmt node, Object arg) {
        this.addInstrumentationCode(node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * We need to keep track of coverage for the overall {@code WhileStmt} but also
     * for all statements in its body
     */
    @Override
    public Node visit(WhileStmt node, Object arg) {
        this.addInstrumentationCode(node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * We need to keep track of coverage for the overall {@code ForEachStmt} but also
     * for all composing statements: {@code VariableDeclarationExpr}, {@code Expression}, and
     * {@code Statement}
     */
    @Override
    public Node visit(ForEachStmt node, Object arg) {
        this.addInstrumentationCode(node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * We need to keep track of coverage for the overall {@code TryStmt} but also
     * for all composing statements: {@code NodeList<Expression>}, {@code BlockStmt}, and
     * {@code CatchClause}
     */
    @Override
    public Node visit(TryStmt node, Object arg) {
        this.addInstrumentationCode(node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * This methods add a new statement
     * @param filename name of the Java file we instrumented
     * @param line the line of the file we want to keep track of (for coverage)
     * @return a new {@code Statement} that corresponds to invoking the method
     * {@code LineCoverageTracker#updateCoverage}.
     */
    private Statement makeCoverageTrackingCall(String filename, int line) {
        // register new line to keep track of
        LineCoverageTracker.registerLine(filename, line);

        // create a method call to LineCoverageTracker
        NameExpr coverageTracker = new NameExpr("nl.tudelft.instrumentation.line.LineCoverageTracker");
        MethodCallExpr call = new MethodCallExpr(coverageTracker, "updateCoverage");
        call.addArgument(new StringLiteralExpr(filename));
        call.addArgument(new IntegerLiteralExpr(String.valueOf(line)));

        // return the newly created method call
        return new ExpressionStmt(call);
    }
}
