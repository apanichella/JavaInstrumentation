package nl.tudelft.instrumentation.fuzzing;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.*;

import nl.tudelft.instrumentation.general.BaseVisitor;

import com.github.javaparser.ast.body.*;

/**
 * This class is used to instrument the code by converting the expressions and statements
 * into the MyVar objects.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class DistanceVisitor extends BaseVisitor {

    int var_count = 1;

    /** Name of the source file to instrument */
    private String filename;

    private String pathFile = "DistanceTracker";

    private String class_name = "";

    public DistanceVisitor(String filename) {
        super();
    }

    /**
     * This method is used to convert each expression to a MyVar object.
     * @param node the expression.
     * @param args the additional arguments that were given to the JavaParser.
     * @return an expression containing our instrumented code.
     */
    public Expression addOwnExpressionCode(Expression node, Object args){
        while(node instanceof EnclosedExpr) node = ((EnclosedExpr)node).getInner();

        // Convert a binary expression to MyVar object
        if(node instanceof BinaryExpr){
            BinaryExpr bine = (BinaryExpr) node;
            String operator = bine.getOperator().asString();
            if(operator.equals("|") || operator.equals("||") || operator.equals("&") || operator.equals("&&") ||
                    operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=") ||
                    operator.equals("==")){
                Expression left = addOwnExpressionCode(bine.getLeft(), args);
                Expression right = addOwnExpressionCode(bine.getRight(), args);
                NodeList<Expression> node_list = new NodeList<Expression>();
                node_list.add(left);
                node_list.add(right);
                node_list.add(new StringLiteralExpr(bine.getOperator().asString()));
                return new MethodCallExpr(new NameExpr(pathFile),"binaryExpr",node_list);
            }
        } else if(node instanceof UnaryExpr){ // Convert an unary expression to a MyVar object.
            UnaryExpr ue = (UnaryExpr) node;
            Expression expr = addOwnExpressionCode(ue.getExpression(),args);
            if(ue.getOperator().asString().equals("+") || ue.getOperator().asString().equals("-") ||
                    ue.getOperator().asString().equals("!")){
                NodeList<Expression> node_list = new NodeList<Expression>();
                node_list.add(expr);
                node_list.add(new StringLiteralExpr(ue.getOperator().asString()));
                return new MethodCallExpr(new NameExpr(pathFile),"unaryExpr", node_list);
            }
        } else if(node instanceof MethodCallExpr){ // Convert a method call (equals) to a MyVar object.
            MethodCallExpr mete = (MethodCallExpr) node;
            Expression scope = addOwnExpressionCode(mete.getScope().get(), args);
            NodeList<Expression> arguments = new NodeList<Expression>();
            for(int i = 0; i < mete.getArguments().size(); ++i){
                arguments.add(addOwnExpressionCode(mete.getArguments().get(i), args));
            }
            if(mete.getName().asString().equals("equals")){
                arguments.add(scope);
                return new MethodCallExpr(new NameExpr(pathFile),"equals",arguments);
            } else {
                return new MethodCallExpr(scope, mete.getName().asString(), arguments);
            }
        }
        NodeList<Expression> node_list = new NodeList<Expression>();
        node_list.add(node.clone());
        return new MethodCallExpr(new NameExpr(pathFile),"MyVar",node_list);
    }

    /**
     * This method is used to convert the expressions/method calls into a MyVar object.
     * @param node the node that represents an expression.
     * @param stat the node but represented as a statement.
     * @param args the additional arguments that were given to the JavaParser.
     */
    public void addOwnConditionalCode(Expression node, Statement stat, Object args){
        while(node instanceof EnclosedExpr) node = ((EnclosedExpr)node).getInner();

        // Convert a binary expression to a MyVar object.
        if(node instanceof BinaryExpr){
            BinaryExpr bine = (BinaryExpr) node;
            addOwnConditionalCode(bine.getLeft(), stat, args);
            addOwnConditionalCode(bine.getRight(), stat, args);
        } else if(node instanceof UnaryExpr){ // convert a unary expression to a MyVar object.
            UnaryExpr ue = (UnaryExpr) node;
            addOwnConditionalCode(ue.getExpression(), stat, args);
        } else if(node instanceof MethodCallExpr){ // Convert a method call (equals) and its arguments to a MyVar object.
            MethodCallExpr mete = (MethodCallExpr) node;
            for(int i = 0; i < mete.getArguments().size(); ++i){
                addOwnConditionalCode(mete.getArguments().get(i), stat, args);
            }
        } else if(node instanceof ConditionalExpr){ // Convert a conditional expression to a myIf call (e.g. a < 4 ? 0 : 1)
            ConditionalExpr ce = (ConditionalExpr) node;
            Expression n_orig = ce.getCondition().clone();
            NodeList<Expression> node_list = new NodeList<Expression>();
            addOwnConditionalCode(ce.getThenExpr(), stat, args);
            addOwnConditionalCode(ce.getElseExpr(), stat, args);
            node_list.add(addOwnExpressionCode(ce.getCondition(),args));
            node_list.add(n_orig);
            node_list.add(new IntegerLiteralExpr(stat.getBegin().get().line));
            ExpressionStmt mycon = new ExpressionStmt(new MethodCallExpr(new NameExpr(pathFile),"myIf", node_list));
            this.addCode(stat, mycon, args);
        }
    }

    /**
     * Method that is used to convert an if-statement to a "myIf" method call.
     * Actually the call is added just right above the if-statement.
     * @param n the node that represents an if-statement.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    public ExpressionStmt createMyIf(IfStmt n, Object arg){
        Expression node = this.addOwnExpressionCode(n.getCondition(), arg);
        Expression n_orig = n.getCondition().clone();
        NodeList<Expression> node_list = new NodeList<Expression>();
        node_list.add(node);
        node_list.add(n_orig);
        node_list.add(new IntegerLiteralExpr(n.getBegin().get().line));
        MethodCallExpr myif = new MethodCallExpr(new NameExpr(pathFile),"myIf",node_list);
        return new ExpressionStmt(myif);
    }

    /**
     * Method that specifies what should be done when we have encountered an expression statement
     * in the AST.
     * @param node the node that represents the expression statement.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrument code.
     */
    @Override
    public Node visit(ExpressionStmt node, Object arg) {
        // This is to modify the lines in the main method.
        if (node.toString().contains("eca =")) {
            Statement staticStatement = StaticJavaParser.parseStatement(pathFile + ".run(eca.inputs, eca);");
            this.addCodeAfter(node, staticStatement, arg);
        }

       // Catch the output from the standard out.
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

        return node;
    }

    /**
     * What to do when we have encountered a Class of Interface declaration.
     * In this case, we try to grab the name of class.
     * @param node the node that represents a class or interface declaration.
     * @param arg additional arguments that were given to the JavaParser.
     * @return node containing extra code that we added.
     */
    @Override
    public Node visit(ClassOrInterfaceDeclaration node, Object arg){
        this.class_name = node.getName().toString();
        BodyDeclaration bd1 = StaticJavaParser.parseBodyDeclaration("public Void call(){ " + class_name + " cp = new " + class_name + "(); for(String s : sequence){ try { cp.calculateOutput(s); } catch (Exception e) { FuzzingLab.output(\"Invalid input: \" + e.getMessage()); } } return null;}");
        BodyDeclaration bd2 = StaticJavaParser.parseBodyDeclaration(" public void setSequence(String[] trace){ sequence = trace; } ");
        BodyDeclaration fd = StaticJavaParser.parseBodyDeclaration("public String[] sequence;");
        node.getMembers().add(fd);
        node.getMembers().add(bd1);
        node.getMembers().add(bd2);
        node.addImplementedType("CallableTraceRunner<Void>");
        return (Node) super.visit(node, arg);
    }

    /**
     * Method that specifies what should we have done when we have encountered
     * an if-statement in the AST.
     * @param node the node that represents the if-statement.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(IfStmt node, Object arg) {
        ExpressionStmt myIf = this.createMyIf(node.clone(), arg);
        this.addCode(node, myIf, arg);
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
        node.addImport("nl.tudelft.instrumentation.fuzzing.*");
        node.addImport("nl.tudelft.instrumentation.runner.CallableTraceRunner");
        return (Node) super.visit(node, arg);
    }

    /**
     * Remove a while statement in the file. This is used to remove
     * the while statement in the problem file.
     * @param node the node that defines the while statement in the file.
     * @param arg the arguments that were given to the JavaParser.
     * @return the parent node after removing the while statement.
     */
    @Override
    public Node visit(WhileStmt node, Object arg) {
        Node parent = node.getParentNode().get();
        parent.remove(node);
        return node;
    }
}
