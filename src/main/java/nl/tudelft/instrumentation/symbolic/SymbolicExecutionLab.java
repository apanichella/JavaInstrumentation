package nl.tudelft.instrumentation.symbolic;

import java.util.*;
import com.microsoft.z3.*;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * You should write your solution using this class.
 */
public class SymbolicExecutionLab {

    static Random r = new Random();

    static MyVar createVar(String name, Expr value, Sort s){
        Context c = PathTracker.ctx;
        // create var, assign value, add to path constraint
        // we show how to do it for creating new symbols
        // please add similar steps to the functions below in order to obtain a path constraint
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s);
        PathTracker.z3model = c.mkAnd(c.mkEq(z3var, value), PathTracker.z3model);
        return new MyVar(z3var, name);
    }

    static MyVar createInput(String name, Expr value, Sort s){
        // create an input var, these should be free variables!
        return new MyVar(PathTracker.ctx.mkString(""));
    }

    static MyVar createBoolExpr(BoolExpr var, String operator){
        // any unary expression (!)
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createBoolExpr(BoolExpr left_var, BoolExpr right_var, String operator){
        // any binary expression (&, &&, |, ||)
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createIntExpr(IntExpr var, String operator){
        // any unary expression (+, -)
        if(operator == "+" || operator == "-")
            return new MyVar(PathTracker.ctx.mkInt(0));
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createIntExpr(IntExpr left_var, IntExpr right_var, String operator){
        // any binary expression (+, -, /, etc)
        if(operator == "+" || operator == "-" || operator == "/" || operator == "*" || operator == "%" || operator == "^")
            return new MyVar(PathTracker.ctx.mkInt(0));
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createStringExpr(SeqExpr left_var, SeqExpr right_var, String operator){
        // we only support String.equals
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static void assign(Expr var, String name, Expr value, Sort s){
        // all variable assignments, use single static assignment
    }

    static void encounteredNewBranch(MyVar condition, boolean value, int line_nr){
        // call the solver
    }

    static void newSatisfiableInput(LinkedList<String> new_inputs) {
        // hurray! found a new branch using these new inputs!
    }

    static String fuzz(String[] inputs){
        // do something useful
        if(r.nextDouble() < 0.01) return "R";
        return inputs[r.nextInt(inputs.length)];
    }

    static void output(String out){
        System.out.println(out);
    }

}