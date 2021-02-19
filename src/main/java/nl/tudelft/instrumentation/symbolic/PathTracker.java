package nl.tudelft.instrumentation.symbolic;

import java.util.*;
import com.microsoft.z3.*;

public class PathTracker {
    public static HashMap<String, String> cfg = new HashMap<String, String>() {{ put("model","true"); }};
    public static Context ctx = new Context(cfg);

    public static int z3counter = 1; // used to give an ID to each variable.
    public static BoolExpr z3model= ctx.mkTrue();
    public static BoolExpr z3branches = ctx.mkTrue();

    public static LinkedList<MyVar> inputs = new LinkedList<MyVar>();

    public static void reset(){
        z3counter  = 1;
        z3model    = ctx.mkTrue();
        z3branches = ctx.mkTrue();
        inputs.clear();
    }

    /**
     * This method contains code that calls the Z3 solver and check whether it solve the path
     * constraint that is constructed for the new branch. The solver will try to inputs that can
     * satify the path contraint. We can use these inputs to reach this newly discovered branch.
     * @param new_branch the branch that we have discovered and want to visit.
     * @param printmodel boolean value that specifies whether the path constraint should be printed in the terminal or not.
     */
    public static void solve(BoolExpr new_branch, boolean printmodel){
        Solver s = PathTracker.ctx.mkSolver();

        s.add(PathTracker.z3model);
        s.add(PathTracker.z3branches);
        s.add(new_branch);

        if(printmodel){
            System.out.print("Model: ");
            System.out.println(PathTracker.z3model);
            System.out.print("Branches: ");
            System.out.println(PathTracker.z3branches);
            System.out.print("New branch: ");
            System.out.println(new_branch);
        }

        if(s.check() == Status.SATISFIABLE){
            //System.out.println("satisfiable");
            Model m = s.getModel();
            LinkedList<String> new_inputs = new LinkedList<String>();
            for(MyVar v : PathTracker.inputs){
                new_inputs.add(m.evaluate(v.z3var, true).toString());
            }
            SymbolicExecutionLab.newSatisfiableInput(new_inputs);
        } else {
            //System.out.println("unsatisfiable");
        }
    }

    // making temporary variables, i.e., within if-conditions
    public static MyVar tempVar(boolean value){
        return new MyVar(ctx.mkBool(value));
    }
    public static MyVar tempVar(int value){
        return new MyVar(ctx.mkInt(value));
    }
    public static MyVar tempVar(String value){
        return new MyVar(ctx.mkString(value));
    }

    // making new stored variables
    public static MyVar myVar(boolean value, String name){
        return SymbolicExecutionLab.createVar(name, ctx.mkBool(value), ctx.getBoolSort());
    }
    public static MyVar myVar(int value, String name){
        return SymbolicExecutionLab.createVar(name, ctx.mkInt(value), ctx.getIntSort());
    }
    public static MyVar myVar(String value, String name){
        return SymbolicExecutionLab.createVar(name, ctx.mkString(value), ctx.getStringSort());
    }
    public static MyVar myVar(MyVar value, String name){
        return SymbolicExecutionLab.createVar(name, value.z3var, value.z3var.getSort());
    }

    // making a new input variable
    public static MyVar myInputVar(String value, String name){
        return SymbolicExecutionLab.createInput(name, ctx.mkString(value), ctx.getStringSort());
    }

    // arrays are tricky, we deal with those
    // this assignment creates a reference and does not need new variables
    public static MyVar[] myVar(MyVar[] value){
        MyVar[] vars = new MyVar[value.length];
        for(int i = 0; i < value.length; i++){
            vars[i] = value[i];
        }
        return vars;
    }

    // main construction for creating the path constraint
    // handling arithmetic and boolean logic
    public static MyVar unaryExpr(MyVar i, String operator){
        if(i.z3var instanceof BoolExpr){
            return SymbolicExecutionLab.createBoolExpr((BoolExpr)i.z3var, operator);
        }
        if(i.z3var instanceof IntExpr || i.z3var instanceof ArithExpr){
            return SymbolicExecutionLab.createIntExpr((IntExpr)i.z3var, operator);
        }
        return new MyVar(ctx.mkFalse());
    }
    public static MyVar binaryExpr( MyVar i, MyVar j, String operator ){
        if(i.z3var instanceof BoolExpr){
            return SymbolicExecutionLab.createBoolExpr((BoolExpr)i.z3var, (BoolExpr)j.z3var, operator);
        }
        if(i.z3var instanceof IntExpr){
            return SymbolicExecutionLab.createIntExpr((IntExpr)i.z3var, (IntExpr)j.z3var, operator);
        }
        return new MyVar(ctx.mkFalse());
    }
    public static MyVar equals(MyVar i, MyVar j){
        return SymbolicExecutionLab.createStringExpr((SeqExpr)i.z3var, (SeqExpr)j.z3var, "==");
    }

    // we handle arrays, which needs an iterated if-then-else
    public static MyVar arrayInd(MyVar[] name, MyVar index){
        Expr ite_expr = name[0].z3var;
        for(int i = 1; i < name.length; i++){
            ite_expr = ctx.mkITE(ctx.mkEq(ctx.mkInt(i),(IntExpr)index.z3var), name[i].z3var, ite_expr);
        }
        return new MyVar(ite_expr);
    }

    // we handle increments, forwarded to assignments
    public static MyVar increment(MyVar i, String operator, boolean prefix){
        if(prefix){
            if(operator.equals("++")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(1))), "=");
            if(operator.equals("--")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(-1))), "=");
            return i;
        } else {
            MyVar old_var = new MyVar(i.z3var);
            if(operator.equals("++")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(1))), "=");
            if(operator.equals("--")) myAssign(i, new MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(-1))), "=");
            return old_var;
        }
    }

    // we handle conditionals, which is an if-then-else
    public static MyVar conditional(MyVar b, MyVar t, MyVar e){
        return new MyVar(ctx.mkITE((BoolExpr)b.z3var, t.z3var, e.z3var));
    }

    // assignment changes the z3var in a MyVar variable
    public static void myAssign(MyVar target, MyVar value, String operator){
        // first add or subtract if necessary
        Expr new_value = value.z3var;
        if(operator == "-=") new_value = ctx.mkSub((IntExpr)target.z3var,(IntExpr)value.z3var);
        if(operator == "+=") new_value = ctx.mkAdd((IntExpr)target.z3var,(IntExpr)value.z3var);

        SymbolicExecutionLab.assign(target, target.name, new_value, target.z3var.getSort());
    }

    // we handle arrays, again using if-then-else and call standard variable assignment for all indices
    public static void myAssign(MyVar[] name, MyVar index, MyVar value, String operator){
        for(int i = 0; i < name.length; i++){
            Expr old_expr = name[i].z3var;
            Expr new_value = value.z3var;
            if(operator == "-=") new_value = ctx.mkSub((IntExpr)old_expr,(IntExpr)value.z3var);
            if(operator == "+=") new_value = ctx.mkAdd((IntExpr)old_expr,(IntExpr)value.z3var);

            SymbolicExecutionLab.assign(name[i], name[i].name, ctx.mkITE(ctx.mkEq(ctx.mkInt(i),index.z3var), new_value, old_expr), name[i].z3var.getSort());
        }
    }

    // direct assign for array references
    public static void myAssign(MyVar[] name1, MyVar[] name2, String operator){
        for(int i = 0; i < name1.length; i++){
            name1[i] = name2[i];
        }
    }

    /**
     * Converts an if-statement into a custom myIf-statement. This method is used to
     * call the encounteredNewBranch method which contains the logic for computing
     * the branch distance when a new branch has been found.
     * @param condition the condition of the if-statement.
     * @param value the value of the condition.
     * @param line_nr the line number of the if-statement.
     */
    public static void myIf(MyVar condition, boolean value, int line_nr){
        SymbolicExecutionLab.encounteredNewBranch(condition, value, line_nr);
    }

    /**
     * Method for fuzzing a new input.
     * @param inputSymbols the input symbols from which the fuzzer should fuzz from.
     * @return an input.
     */
    public static String fuzz(String[] inputSymbols){
        return SymbolicExecutionLab.fuzz(inputSymbols);
    }

    /**
     * Used to catch output from the standard out.
     * @param out the string that has been outputted in the standard out.
     */
    public static void output(String out){
        SymbolicExecutionLab.output(out);
    }

}