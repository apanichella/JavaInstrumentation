package nl.tudelft.instrumentation.learning;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.*;
import nl.tudelft.instrumentation.general.BaseVisitor;

import com.github.javaparser.ast.body.*;

/**
 * This class is used to instrument the code by converting the expressions and
 * statements
 * into the MyVar objects.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class MembershipVisitor extends BaseVisitor {

    private String pathFile = "LearningTracker";

    private String class_name = "";

    /**
     * What to do when we have encountered a Class of Interface declaration.
     * In this case, we try to grab the name of class.
     * 
     * @param node the node that represents a class or interface declaration.
     * @param arg  additional arguments that were given to the JavaParser.
     * @return node containing extra code that we added.
     */
    @Override
    public Node visit(ClassOrInterfaceDeclaration node, Object arg) {
        this.class_name = node.getName().toString();
        BodyDeclaration bd1 = StaticJavaParser.parseBodyDeclaration("public Void call(){ " + class_name + " cp = new "
                + class_name
                + "(); for(String s : sequence){ try { cp.calculateOutput(s); } catch (Exception e) { LearningTracker.output(\"Invalid input: \" + e.getMessage()); } LearningTracker.processedInput(); } return null;}");
        BodyDeclaration bd2 = StaticJavaParser
                .parseBodyDeclaration(" public void setSequence(String[] trace){ sequence = trace; } ");
        BodyDeclaration fd = StaticJavaParser.parseBodyDeclaration("public String[] sequence;");
        node.getMembers().add(fd);
        node.getMembers().add(bd1);
        node.getMembers().add(bd2);
        node.addImplementedType("CallableTraceRunner<Void>");
        return (Node) super.visit(node, arg);
    }

    /**
     * Method to add import statements in the top of the source file.
     * 
     * @param node the node that defines the root of the source file.
     * @param arg  additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(CompilationUnit node, Object arg) {
        node.addImport("nl.tudelft.instrumentation.learning.*");
        node.addImport("nl.tudelft.instrumentation.runner.CallableTraceRunner");
        return (Node) super.visit(node, arg);
    }

    /**
     * Remove a while statement in the file. This is used to remove
     * the while statement in the problem file.
     * 
     * @param node the node that defines the while statement in the file.
     * @param arg  the arguments that were given to the JavaParser.
     * @return the parent node after removing the while statement.
     */
    @Override
    public Node visit(WhileStmt node, Object arg) {
        Node parent = node.getParentNode().get();
        parent.remove(node);
        return node;
    }

    /**
     * Method that specifies what should be done when we have encountered an
     * expression statement
     * in the AST.
     * 
     * @param node the node that represents the expression statement.
     * @param arg  additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(ExpressionStmt node, Object arg) {
        if (node.toString().contains("eca =")) {
            Statement staticStatement = StaticJavaParser.parseStatement(pathFile + ".run(eca.inputs, eca);");
            this.addCodeAfter(node, staticStatement, arg);
        }
        // Catch the output from the standard out.
        if (node.getExpression() instanceof MethodCallExpr) {
            MethodCallExpr mce = (MethodCallExpr)node.getExpression();
            if (node.toString().contains("System.out")) {
                node.setExpression(
                        new MethodCallExpr(
                                new NameExpr(pathFile),"output",mce.getArguments()
                        )
                );
            }
        }
        return node;
    }

}
