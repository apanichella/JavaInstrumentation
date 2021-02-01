package nl.tudelft.instrumentation.symbolic;

import java.util.*;
import com.microsoft.z3.*;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used for the following:
 * - It constructs the corresponding Z3 expression for each variable.
 * - It stores the context object that is used to compute path contraint.
 * - It stores the model that will be used to represent the path contraint.
 * - It stores the inputs that were used in a symbolic execution.
 *
 * This class constructs the corresponding expressions for a symbolic execution. The path constraint
 * that is computed using this class will be given to the Z3 solver for it to find a solution.
 *
 * @author Sicco Verwer
 */
public class PathTracker {
    public static HashMap<String, String> cfg = new HashMap<String, String>() {{ put("model","true"); }};
    public static Context ctx = new Context(cfg);

    public static int z3counter = 1; // used to give an ID to each variable.
    public static BoolExpr z3model= ctx.mkTrue();
    public static BoolExpr z3branches = ctx.mkTrue();

    public static LinkedList<MyVar> inputs = new LinkedList<MyVar>();

    /**
     * Resets everything that is used for a symbolic execution.
     */
    public static void reset(){
        z3counter  = 1;
        z3model    = ctx.mkTrue();
        z3branches = ctx.mkTrue();
        inputs.clear();
    }

    /**
     * The following three methods convert the primitive types of java to the corresponding Z3 expression type.
     * It then creates a MyVar object using the expression. The objects that are created using these methods
     * are for temporary use only i.e. these will not be used in the path constraint but they are needed for
     * the operations that are done in a symbolic execution (e.g. comparing whether two values are equal).
     **/
    public static MyVar tempVar(boolean value){
        return new MyVar(ctx.mkBool(value));
    }
    public static MyVar tempVar(int value){
        return new MyVar(ctx.mkInt(value));
    }
    public static MyVar tempVar(String value){
        return new MyVar(ctx.mkString(value));
    }

    /**
     * Converts a boolean to a Z3 boolean expression.
     * @param value the value of the boolean variable.
     * @param name the name of the variable.
     * @return a MyVar object that is used to represent a Z3 boolean expression.
     */
    public static MyVar myVar(boolean value, String name){
        Expr z3var = ctx.mkConst(ctx.mkSymbol(name + "_" + z3counter++), ctx.getBoolSort());
        z3model = ctx.mkAnd(ctx.mkEq(z3var, ctx.mkBool(value)), z3model);
        return new MyVar(z3var, name);
    }

    /**
     * Converts an integer to a Z3 boolean expression.
     * @param value the value of the integer variable.
     * @param name the name of the variable.
     * @return a MyVar object that is used to represent a Z3 integer expression.
     */
    public static MyVar myVar(int value, String name){
        Expr z3var = ctx.mkConst(ctx.mkSymbol(name + "_" + z3counter++), ctx.getIntSort());
        z3model = ctx.mkAnd(ctx.mkEq(z3var, ctx.mkInt(value)), z3model);
        return new MyVar(z3var, name);
    }

    /**
     * Converts a string to a Z3 string expression.
     * @param value the value of the string variable.
     * @param name the name of the variable.
     * @return a MyVar object that is used to represent a Z3 string expression.
     */
    public static MyVar myVar(String value, String name){
        Expr z3var = ctx.mkConst(ctx.mkSymbol(name + "_" + z3counter++), ctx.getStringSort());
        z3model = ctx.mkAnd(ctx.mkEq(z3var, ctx.mkString(value)), z3model);
        return new MyVar(z3var, name);
    }

    /**
     * Converts an input to a Z3 string expression.
     * @param value the value of the input.
     * @param name the name of the input variable.
     * @return a MyVar object that is used to represent a Z3 string expression for the input.
     */
    public static MyVar myInputVar(String value, String name){
        Expr z3var = ctx.mkConst(ctx.mkSymbol(name + "_" + z3counter++), ctx.getStringSort());
        MyVar result =  new MyVar(z3var, name);
        inputs.add(result);
        return result;
    }

    /**
     * Creates a MyVar object from an existing MyVar object.
     * @param value the MyVar object.
     * @param name the name of the MyVar object.
     * @return a MyVar object.
     */
    public static MyVar myVar(MyVar value, String name){
        Expr z3var = ctx.mkConst(ctx.mkSymbol(name + "_" + z3counter++), value.z3var.getSort());
        z3model = ctx.mkAnd(ctx.mkEq(z3var, value.z3var), z3model);
        return new MyVar(z3var, name);
    }

    /**
     * Creates a copy of an array of MyVar objects.
     * @param value the array of MyVar objects
     * @return a copy of the array of MyVar objects.
     */
    public static MyVar[] myVar(MyVar[] value){
        MyVar[] vars = new MyVar[value.length];
        for(int i = 0; i < value.length; i++){
            vars[i] = value[i];
        }
        return vars;
    }

    /**
     * Creates an MyVar object based on the given binary expression. It constructs the corresponding
     * Z3 binary expression.
     * @param i the left-hand side variable
     * @param j the right-hand side variable
     * @param operator the operator of the binary expression.
     * @return a MyVar objects that represents a Z3 binary expression.
     */
    public static MyVar binaryExpr( MyVar i, MyVar j, String operator ){
        // Deal with boolean binary expressions.
        if(i.z3var instanceof BoolExpr){
            if(operator.equals("&&") || operator.equals("&")) return new MyVar(ctx.mkAnd((BoolExpr)i.z3var,(BoolExpr)j.z3var));
            if(operator.equals("||") || operator.equals("|")) return new MyVar(ctx.mkOr((BoolExpr)i.z3var,(BoolExpr)j.z3var));

            // Comparisons between variables.
            if(operator.equals("==")) return new MyVar(ctx.mkEq((BoolExpr)i.z3var,(BoolExpr)j.z3var));
            if(operator.equals("!=")) return new MyVar(ctx.mkNot(ctx.mkEq((BoolExpr)i.z3var,(BoolExpr)j.z3var)));
        }

        // Deal with integer binary expressions.
        if(i.z3var instanceof IntExpr){
            if(operator.equals("+")) return new MyVar(ctx.mkAdd((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("-")) return new MyVar(ctx.mkSub((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("/")) return new MyVar(ctx.mkDiv((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("%")) return new MyVar(ctx.mkMod((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("*")) return new MyVar(ctx.mkMul((IntExpr)i.z3var,(IntExpr)j.z3var));

            // Comparisons between variables.
            if(operator.equals("==")) return new MyVar(ctx.mkEq((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("!=")) return new MyVar(ctx.mkNot(ctx.mkEq((IntExpr)i.z3var,(IntExpr)j.z3var)));
            if(operator.equals(">")) return new MyVar(ctx.mkGt((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("<")) return new MyVar(ctx.mkLt((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals(">=")) return new MyVar(ctx.mkGe((IntExpr)i.z3var,(IntExpr)j.z3var));
            if(operator.equals("=<")) return new MyVar(ctx.mkLe((IntExpr)i.z3var,(IntExpr)j.z3var));
        }
        return new MyVar(ctx.mkFalse());
    }

    /**
     * Creates an MyVar object based on the given unary expression. It constructs the corresponding
     * Z3 unary expression.
     * @param i the variable
     * @param operator the operator of the unary expression.
     * @return a MyVar objects that represents a Z3 unary expression.
     */
    public static MyVar unaryExpr(MyVar i, String operator){
        // Deal with integer unary expressions.
        if(i.z3var instanceof IntExpr || i.z3var instanceof ArithExpr){
            if(operator.equals("+")) return new MyVar((IntExpr)i.z3var);
            if(operator.equals("-")) return new MyVar(ctx.mkUnaryMinus((IntExpr)i.z3var));
        }

        // Deal with boolean unary expressions.
        if(i.z3var instanceof BoolExpr){
            if(operator.equals("+")) return new MyVar((BoolExpr)i.z3var);
            if(operator.equals("-")) return new MyVar(ctx.mkNot((BoolExpr)i.z3var));
            if(operator.equals("!")) return new MyVar(ctx.mkNot((BoolExpr)i.z3var));
        }
        return new MyVar(ctx.mkFalse());
    }

    /**
     * Creates an MyVar object based on the given equals expression. It constructs the corresponding
     * Z3 equals expression.
     * @param i the left-hand side variable.
     * @param j  the right-hand side variable
     * @return a MyVar objects that represents a Z3 equals expression.
     */
    public static MyVar equals(MyVar i, MyVar j){
        return new MyVar(ctx.mkEq((SeqExpr)i.z3var, (SeqExpr)j.z3var));
    }

    /**
     * Creates an MyVar object based on the given array access expression. It constructs the corresponding
     * Z3 ITE (If-then-else) expression to represent array access expression.
     * @param name the name of the variable.
     * @param index the index of the array that were are trying to access.
     * @return a MyVar objects that represents a Z3 ITE expression (in our case an array access expression).
     */
    public static MyVar arrayInd(MyVar[] name, MyVar index){
        Expr ite_expr = name[0].z3var;
        for(int i = 1; i < name.length; i++){
            ite_expr = ctx.mkITE(ctx.mkEq(ctx.mkInt(i),(IntExpr)index.z3var), name[i].z3var, ite_expr);
        }
        return new MyVar(ite_expr);
    }

    /**
     * Creates a MyVar object that represents an increment/decrement expression. It construct the corresponding
     * Z3 increment/decrements expression (which is an "add" operation).
     * @param i the variable on which the increment/decrement is done.
     * @param operator the operator of the operation.
     * @param prefix specify whether variable name has a prefix or not.
     * @return a MyVar object that represents a Z3 increment/decrement expression.
     */
    public static MyVar increment(MyVar i, String operator, boolean prefix){
        if(prefix){
            if(operator.equals("++")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(1))), "=");
            if(operator.equals("--")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(-1))), "=");
            return i;
        } else {
            MyVar old_var = new MyVar(i.z3var); // if it has a prefix, create a reference.
            if(operator.equals("++")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(1))), "=");
            if(operator.equals("--")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(-1))), "=");
            return old_var;
        }
    }

    /**
     * Create a MyVar object that represents a conditional expression. It constructs the corresponding Z3
     * ITE (if-then-else) expression.
     * @param b the variable that represents the boolean condition.
     * @param t the variable that represents the then statements.
     * @param e the variable that represents the else statements.
     * @return a MyVar objec that represents a Z3 condition expression (ITE).
     */
    public static MyVar conditional(MyVar b, MyVar t, MyVar e){
        return new MyVar(ctx.mkITE((BoolExpr)b.z3var, t.z3var, e.z3var));
    }

    /**
     * Create a Z3 expression that represents a compound assignment expression. It constructs the
     * corresponding Z3 addition/subtraction expression based on the operator.
     * @param target the variable for which the value will be assigned to.
     * @param value the value that will be assigned to the variable.
     * @param operator the operator of the compound expression.
     */
    public static void myAssign(MyVar target, MyVar value, String operator){
        Expr new_value = value.z3var;
        if(operator == "-=") new_value = ctx.mkSub((IntExpr)target.z3var,(IntExpr)value.z3var);
        if(operator == "+=") new_value = ctx.mkAdd((IntExpr)target.z3var,(IntExpr)value.z3var);

        target.z3var = ctx.mkConst(ctx.mkSymbol(target.name + "_"+z3counter++), target.z3var.getSort());
        z3model = ctx.mkAnd(ctx.mkEq(target.z3var, new_value), z3model);
    }

    /**
     * Create a Z3 expression that represents a compound assignment expression on an element that is stored in an array.
     * It constructs the corresponding Z3 addition/subtraction expression based on the operator.
     * @param name the name of the variable (name of the array).
     * @param index the selected index of the array.
     * @param value the value that will be assigned to the variable.
     * @param operator the operator of the compound expression
     */
    public static void myAssign(MyVar[] name, MyVar index, MyVar value, String operator){
        for(int i = 0; i < name.length; i++){
            Expr old_expr = name[i].z3var;
            Expr new_value = value.z3var;
            if(operator == "-=") new_value = ctx.mkSub((IntExpr)old_expr,(IntExpr)value.z3var);
            if(operator == "+=") new_value = ctx.mkAdd((IntExpr)old_expr,(IntExpr)value.z3var);

            name[i].z3var = ctx.mkConst(ctx.mkSymbol(name[i].name + "_"+z3counter++), name[i].z3var.getSort());
            z3model = ctx.mkAnd(ctx.mkEq(name[i].z3var, ctx.mkITE(ctx.mkEq(ctx.mkInt(i),index.z3var), new_value, old_expr)), z3model);
        }
    }

    /**
     * Creates a Z3 expression that represents an assignment of an array to a new variable. It basically copies the values
     * of one array to the other.
     * @param name1 the name of the array to which values should be copied to.
     * @param name2 the name of the array from whihc values are copied from.
     * @param operator unused.
     */
    public static void myAssign(MyVar[] name1, MyVar[] name2, String operator){
        for(int i = 0; i < name1.length; i++){
            name1[i] = name2[i];
        }
    }

    /**
     * This method specifies what should be done when when we are calling a "myIf" method.
     * Whenever we have made a call to a "myIf", we assume that we have discovered a new branch
     * and we would like see whether we can visit this branch using Symbolic Execution. You should
     * implement the logic in the "encounteredNewBranch" method.
     * @param condition the condition of the branch
     * @param value the value of the condition
     * @param line_nr the line number where we have discovered the branch.
     */
    public static void myIf(MyVar condition, boolean value, int line_nr){

        SymbolicExecution.encounteredNewBranch(condition, value, line_nr);

        if(value){
            z3branches = ctx.mkAnd(ctx.mkEq(condition.z3var, ctx.mkTrue()), z3branches);
        } else {
            z3branches = ctx.mkAnd(ctx.mkEq(condition.z3var, ctx.mkFalse()), z3branches);
        }
    }
}
