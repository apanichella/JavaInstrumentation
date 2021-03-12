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
 * This class is used to parse a RERS problem and instrument the code with our object
 * types and method calls.
 *
 * @author Sicco Verwer
 */
public class OperatorVisitor extends ModifierVisitor<Object> {
    /** Name of the source file to instrument */
    private String filename;

    private String pathFile = "nl.tudelft.instrumentation.patching.OperatorTracker";

    private String class_name = "";

    int operator_nr = 0;

    LinkedList<String> operators = new LinkedList<String>();

    public OperatorVisitor(String filename) {
        this.filename = filename;
    }

    /**
     * This method is used to insert a statement above a given statement.
     * @param node the statement for which we want to insert statement above.
     * @param new_statement the statement that we want to insert.
     * @param args the additional arguments that were given to the JavaParser.
     * @return a node containing the instrumented code.
     */
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

    /**
     * This method is used to insert a statement after a given statement. Used to insert additional statement
     * in the main method (right after "String input = stdin.readLine();"
     * @param node the node that represents the statement for which we want to add a statement after.
     * @param new_statement the new statement that needs to be inserted
     * @param args additional arguments that were given to the JavaParser
     * @return a node containing our instrumented code.
     */
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

    /**
     * This method converts set of given operators into a myOperator method call.
     * @param node the node that represents an expression.
     * @param stat the expression but represented as a statement.
     * @param args the additional arguments that were given to the JavaParser.
     */
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
     * Method that specifies what should we done when we have encountered
     * an if-statement in the AST.
     * @param node the node that represents the if-statement.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(IfStmt node, Object arg) {
        this.setOperatorList(node.getCondition(), node, arg);
        return (Node) super.visit(node, arg);
    }

    /**
     * Method that specifies what should be done when we have encountered a class or interface
     * declaration in the AST.
     * @param node the node that represents a class or interface declaration.
     * @param arg the additional arguments that were given to the JavaParser.
     * @return a node containing the instrumented code.
     */
    @Override
    public Node visit(ClassOrInterfaceDeclaration node, Object arg){
        this.class_name = node.getName().toString();
        return (Node) super.visit(node, arg);
    }

    /**
     * Method that specifies what should be done when we have encountered an expression statement
     * in the AST.
     * @param node the node that represents the expression statement.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(ExpressionStmt node, Object arg) {
        if (node.getExpression() instanceof VariableDeclarationExpr) {
            //System.out.println(node.toString());
            if (node.toString().contains("String input = stdin")) {
                Statement staticStatement = StaticJavaParser.parseStatement("if(input.equals(\"#\")){ eca = new " + class_name + "(); continue; }");
                this.addCodeAfter(node, staticStatement, arg);
                staticStatement = StaticJavaParser.parseStatement("String input = " + pathFile + ".fuzz(eca.inputs);");
                node.replace(staticStatement);
            }
            if (node.toString().contains("eca =") && !operators.isEmpty()) {
                String operator_string = "{ \"" + operators.pop() + "\"";
                for(String op : operators){
                    operator_string = operator_string + ", \"" + op + "\"";
                }
                operator_string = operator_string + "}";
                Statement staticStatement = StaticJavaParser.parseStatement("String[] operators = "+ operator_string + ";");
                this.addCode(node, staticStatement, arg);
                staticStatement = StaticJavaParser.parseStatement(pathFile + ".initialize(operators);");
                this.addCode(node, staticStatement, arg);
            }
        }
        if (node.getExpression() instanceof MethodCallExpr) {
            MethodCallExpr mce = (MethodCallExpr)node.getExpression();
            if (node.toString().contains("System.out")) {
                this.addCode(node, new ExpressionStmt(new MethodCallExpr(new NameExpr(pathFile),"output",mce.getArguments())), arg);
            }
        }
        return (Node) super.visit(node, arg);
    }

    /**
     * Specifies what should happen when we encounter a catch clause
     * in the AST.
     * @param node the node that represents a catch clause.
     * @param arg additional arguments given to the JavaParser.
     * @return node containing our instrumented code.
     */
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
        return (Node) super.visit(node, arg);
    }
}