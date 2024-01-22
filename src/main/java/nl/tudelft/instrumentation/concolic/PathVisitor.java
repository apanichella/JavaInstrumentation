package nl.tudelft.instrumentation.concolic;

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
import com.github.javaparser.ast.type.*;

/**
 * This class is used to parse the RERS problem and instrument the problem file with our own object types
 * and method calls.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class PathVisitor extends BaseVisitor {

    /** Used to give each variable an ID */
    int var_count = 1;

    /** Name of the source file to instrument */
    private String filename;
    private String pathFile = "PathTracker";

    private String class_name = "";

    /**
     * Constructor
     * @param filename the name of the source file that we want to parse.
     */
    public PathVisitor(String filename) {
        super();
    }

    /**
     * Method that is used to convert each expression into the corresponding Z3 expression.
     * @param node the expression.
     * @param args additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    public Expression addOwnExpressionCode(Expression node, Object args) {
        while(node instanceof EnclosedExpr) node = ((EnclosedExpr)node).getInner();

        // Prepend "my_" to the name of the variable.
        if(node instanceof NameExpr){
            return new NameExpr("my_" + node.toString());
        } else if(node instanceof LiteralExpr){ // convert each variable (that is a literal) to a MyVar object.
            NodeList<Expression> node_list = new NodeList<Expression>();
            node_list.add(node.clone());
            return new MethodCallExpr(new NameExpr(pathFile),"tempVar", node_list);
        } else if(node instanceof BinaryExpr){ // convert binary expressions to the corresponding Z3 binary expressions.
            BinaryExpr bine = (BinaryExpr) node;
            Expression left = addOwnExpressionCode(bine.getLeft(), args);
            Expression right = addOwnExpressionCode(bine.getRight(), args);
            NodeList<Expression> node_list = new NodeList<Expression>();
            node_list.add(left);
            node_list.add(right);
            node_list.add(new StringLiteralExpr(bine.getOperator().asString()));
            return new MethodCallExpr(new NameExpr(pathFile),"binaryExpr",node_list);
        } else if(node instanceof MethodCallExpr){ // convert "equals" calls to the MyVar equals calls
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
        } else if(node instanceof ArrayAccessExpr){ // convert array access expression to the corresponding Z3 array access expression
            ArrayAccessExpr aae = (ArrayAccessExpr) node;
            Expression name = addOwnExpressionCode(aae.getName(), args);
            Expression index = addOwnExpressionCode(aae.getIndex(),args);
            NodeList<Expression> node_list = new NodeList<Expression>();
            node_list.add(name);
            node_list.add(index);
            return new MethodCallExpr(new NameExpr(pathFile),"arrayInd", node_list);
        } else if(node instanceof UnaryExpr){ // convert unary expression to the corresponding Z3 unary expression
            UnaryExpr ue = (UnaryExpr) node;
            Expression expr = addOwnExpressionCode(ue.getExpression(),args);
            if(ue.getOperator().asString().equals("++") || ue.getOperator().asString().equals("--")){
                NodeList<Expression> node_list = new NodeList<Expression>();
                node_list.add(expr);
                node_list.add(new StringLiteralExpr(ue.getOperator().asString()));
                node_list.add(new BooleanLiteralExpr(ue.isPrefix()));
                return new MethodCallExpr(new NameExpr(pathFile),"increment", node_list);
            }
            else if(ue.getOperator().asString().equals("+") || ue.getOperator().asString().equals("-")){
                NodeList<Expression> node_list = new NodeList<Expression>();
                node_list.add(expr);
                node_list.add(new StringLiteralExpr(ue.getOperator().asString()));
                return new MethodCallExpr(new NameExpr(pathFile),"unaryExpr", node_list);
            }
            else if(ue.getOperator().asString().equals("!")){
                NodeList<Expression> node_list = new NodeList<Expression>();
                node_list.add(expr);
                node_list.add(new StringLiteralExpr(ue.getOperator().asString()));
                return new MethodCallExpr(new NameExpr(pathFile),"unaryExpr", node_list);
            }
        } else if(node instanceof ConditionalExpr){ // convert conditional expression to the corresponding Z3 conditional expression
            ConditionalExpr ce = (ConditionalExpr) node;
            NodeList<Expression> node_list = new NodeList<Expression>();
            node_list.add(addOwnExpressionCode(ce.getCondition(),args));
            node_list.add(addOwnExpressionCode(ce.getThenExpr(),args));
            node_list.add(addOwnExpressionCode(ce.getElseExpr(),args));
            return new MethodCallExpr(new NameExpr(pathFile),"conditional", node_list);
        }
        return node.clone();
    }

    /**
     * Method that is used to convert an if-statement to a "myIf" method call.
     * Actually the call is added just right above the if-statement.
     * @param n the node that represents an if-statement.
     * @param args additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    public ExpressionStmt createMyIf(IfStmt n, Object args){
        Expression node = this.addOwnExpressionCode(n.getCondition(), args);
        Expression n_orig = n.getCondition().clone();
        NodeList<Expression> node_list = new NodeList<Expression>();
        node_list.add(node);
        node_list.add(n_orig);
        node_list.add(new IntegerLiteralExpr(n.getBegin().get().line));
        MethodCallExpr myif = new MethodCallExpr(new NameExpr(pathFile),"myIf",node_list);
        return new ExpressionStmt(myif);
    }

    /**
     * Method that is used to convert assigment operations to "myAssign" method calls.
     * @param node the node that represents an assignment expression.
     * @param args additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    public ExpressionStmt createMyAssign(AssignExpr node, Object args){
        NodeList<Expression> node_list = new NodeList<Expression>();
        if(node.getTarget() instanceof ArrayAccessExpr){
            ArrayAccessExpr aae = (ArrayAccessExpr)node.getTarget();
            node_list.add(this.addOwnExpressionCode(aae.getName(),args));
            node_list.add(this.addOwnExpressionCode(aae.getIndex(),args));
        } else {
            node_list.add(this.addOwnExpressionCode(node.getTarget(),args));
        }
        node_list.add(this.addOwnExpressionCode(node.getValue(),args));
        node_list.add(new StringLiteralExpr(node.getOperator().asString()));
        MethodCallExpr myassign = new MethodCallExpr(new NameExpr(pathFile),"myAssign", node_list);
        return new ExpressionStmt(myassign);
    }

    /**
     * Method that is used to create the MyVar variables for each field that is listed in the source file.
     * @param node the node that represents a declaration of a variable.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    public VariableDeclarator createMyField(VariableDeclarator node, Object arg){
        JavaParser jp = new JavaParser();
        // Prepend "my_" to the name of the variable.
        node.setName(new SimpleName("my_" + node.getName().toString()));
        // Check whether we are deadline with a variable that is used to store an array.
        if(node.getType().getArrayLevel() != 0){
            node.setType(new ClassOrInterfaceType("MyVar[]"));
            if(node.getInitializer().isPresent()){
                if(node.getInitializer().get() instanceof ArrayInitializerExpr){
                    ArrayInitializerExpr aie = (ArrayInitializerExpr)node.getInitializer().get();
                    int i = 0;
                    // Convert each element in the array into a MyVar object
                    for(Expression e : aie.getValues()){
                        NodeList<Expression> node_list = new NodeList<Expression>();
                        node_list.add(e.clone());
                        node_list.add(new StringLiteralExpr(node.getName().asString() + String.valueOf(i)));
                        e.replace(new MethodCallExpr(new NameExpr(pathFile), "myVar", node_list));
                        i++;
                    }
                } else {
                    NodeList<Expression> node_list = new NodeList<Expression>();
                    node_list.add(addOwnExpressionCode(node.getInitializer().get(), arg));
                    node_list.add(new StringLiteralExpr(node.getName().asString()));
                    node.setInitializer(new MethodCallExpr(new NameExpr(pathFile), "myVar", node_list));
                }
            }
        } else {
            // Convert booleans, integers and strings to MyVar objects.
            if(node.getType().asString().equals("boolean") || node.getType().asString().equals("int") || node.getType().asString().equals("String")){
                node.setType(new ClassOrInterfaceType("MyVar"));
                if(node.getInitializer().isPresent()){
                    NodeList<Expression> node_list = new NodeList<Expression>();
                    node_list.add(node.getInitializer().get());
                    node_list.add(new StringLiteralExpr(node.getName().asString()));
                    node.setInitializer(new MethodCallExpr(new NameExpr(pathFile), "myVar", node_list));
                }
            }
        }
        return node;
    }

    /**
     * Method that specifies what should be done when we have encountered a field declaration
     * in the AST.
     * @param node the node that represents the field declaration.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(FieldDeclaration node, Object arg){
        if(node.getParentNode().isPresent()){
            for(VariableDeclarator vard : node.getVariables()){
                // For each field that is listed in the source file, create a copy of the field and give it the
                // MyVar type so that we can use these fields in the concolic execution.
                FieldDeclaration fd = new FieldDeclaration(node.getModifiers(), createMyField(vard.clone(), arg));
                ClassOrInterfaceDeclaration decl = (ClassOrInterfaceDeclaration) node.getParentNode().get();
                decl.getMembers().add(fd);
            }
        }
        return node;
    }

    /**
     * Method that specifies what should be done when we have encountered a method declaration
     * in the AST.
     * @param node the node that represents the Method Declaration.
     * @param arg additional arguments that were given to the JavaParser.
     * @return a node that contains our instrumented code.
     */
    @Override
    public Node visit(MethodDeclaration node, Object arg){
        if(node.getName().toString().contains("calculate")){
            for(Parameter par : node.clone().getParameters()){
                // For each parameter of the method, create a copy of the parameter and give it the MyVar type
                // This makes sure that we can also use the parameters in the concolic execution.
                node.addParameter("MyVar", "my_" + par.getName().asString());
            }
        }
        return (Node) super.visit(node, arg);
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
        BodyDeclaration bd1 = StaticJavaParser.parseBodyDeclaration("public Void call(){ " + class_name + " cp = new " + class_name + "(); for(String s : sequence){ try { MyVar my_s = PathTracker.myInputVar(s, \"input\"); cp.calculateOutput(s); } catch (IllegalArgumentException | IllegalStateException e) { ConcolicExecutionLab.output(\"Invalid input: \" + e.getMessage()); } } return null;}");
        BodyDeclaration bd2 = StaticJavaParser.parseBodyDeclaration(" public void setSequence(String[] trace){ sequence = trace; } ");
        BodyDeclaration fd = StaticJavaParser.parseBodyDeclaration("public String[] sequence;");
        node.getMembers().add(fd);
        node.getMembers().add(bd1);
        node.getMembers().add(bd2);
        node.addImplementedType("CallableTraceRunner<Void>");
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
        // This is to modify the main method to follow a particular structure.
        if (node.toString().contains("eca =")) {
            Statement staticStatement = StaticJavaParser.parseStatement(pathFile + ".run(eca.inputs, eca);");
            this.addCodeAfter(node, staticStatement, arg);
        }

        // What should be done when it is an assign expression.
        if(node.getExpression() instanceof AssignExpr && !node.toString().contains("sequence")){
            // Convert it to the corresponding Z3 expression.
            ExpressionStmt myAssign = this.createMyAssign((AssignExpr)node.getExpression().clone(), arg);
            this.addCode(node, myAssign, arg);
        }

        // What should be done when it is a method call expresion.
        if(node.getExpression() instanceof MethodCallExpr){
            MethodCallExpr n = ((MethodCallExpr)node.getExpression()).clone();
            if(n.getName().asString().startsWith("calculate")){
                for(Expression expr : n.getArguments()){
                    // We basically add an extra argument to a method if its name starts with "calculate"
                    ((MethodCallExpr)node.getExpression()).addArgument(addOwnExpressionCode(expr, arg));
                }
            }
        }

        // Handle unary expression (eg ++) if it is the only expression on a line.
        if(node.getExpression() instanceof UnaryExpr) {
            ExpressionStmt s = new ExpressionStmt(addOwnExpressionCode(node.getExpression().clone(), arg));
            this.addCode(node, s, arg);
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
        node.addImport("nl.tudelft.instrumentation.concolic.*");
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
