package nl.tudelft.instrumentation.fuzzing;

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
import com.github.javaparser.ast.type.*;

/**
 * This class is used to instrument the code by converting the expressions and statements
 * into the MyVar objects.
 *
 * @author Sicco Verwer
 */
public class DistanceVisitor extends ModifierVisitor<Object> {

    int var_count = 1;

    /** Name of the source file to instrument */
    private String filename;
    
    private String pathFile = "nl.tudelft.instrumentation.fuzzing.DistanceTracker";

    private String class_name = "";

    public DistanceVisitor(String filename) {
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
                    operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals("<=") ||
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
        // This is to insert a line main method.
        if (node.getExpression() instanceof VariableDeclarationExpr) {
            if (node.toString().contains("String input = stdin")) {
                Statement staticStatement = StaticJavaParser.parseStatement("if(input.equals(\"R\")){ eca = new " + class_name + "(); continue; }");
                this.addCodeAfter(node, staticStatement, arg);
                staticStatement = StaticJavaParser.parseStatement("String input = " + pathFile + ".fuzz(eca.inputs);");
                node.replace(staticStatement);
            }
        }
        // What we should do when we encountered a assign expression.
        if(node.getExpression() instanceof AssignExpr){
            AssignExpr ae = (AssignExpr)node.getExpression();
            // Check if the assigment expression involves an array.
            if(ae.getTarget() instanceof ArrayAccessExpr){
                ArrayAccessExpr aae = (ArrayAccessExpr)ae.getTarget();
                this.addOwnConditionalCode(aae.getIndex(),node,arg);
            }
            this.addOwnConditionalCode(ae.getValue(),node,arg);
        }

        // Catch the out from in the standard out.
        if (node.getExpression() instanceof MethodCallExpr) {
            MethodCallExpr mce = (MethodCallExpr)node.getExpression();
            if (node.toString().contains("System.out")) {
                this.addCode(node, new ExpressionStmt(new MethodCallExpr(new NameExpr(pathFile),"output",mce.getArguments())), arg);
            }
        }

        return node;
    }

    /**
     * What to do when we have encountered a Class of Interface declaration.
     * In this case, we try to grab the name of class.
     * @param node the node that represents a class or interface declaration.
     * @param arg additional arugments that were given to the JavaParser.
     * @return node original node.
     */
    @Override
    public Node visit(ClassOrInterfaceDeclaration node, Object arg){
        this.class_name = node.getName().toString();
        return (Node) super.visit(node, arg);
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
        ExpressionStmt myif = this.createMyIf(node.clone(), arg);
        this.addCode(node, myif, arg);
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
        return (Node) super.visit(node, arg);
    }
}
