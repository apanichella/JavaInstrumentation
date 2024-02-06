package nl.tudelft.instrumentation.concolic;

import java.util.*;
import java.util.stream.Collectors;

import com.microsoft.z3.*;

/**
 * You should write your solution using this class.
 * 
 * Z3 API: https://z3prover.github.io/api/html/classcom_1_1microsoft_1_1z3_1_1_context.html
 */
public class ConcolicExecutionLab {

    static Random r = new Random();
    static Boolean isFinished = false;
    static List<String> currentTrace;
    static int traceLength = 10;

    static void initialize(String[] inputSymbols){
        // Initialise a random trace from the input symbols of the problem.
        currentTrace = generateRandomTrace(inputSymbols);
    }

    static MyVar createVar(String name, Expr value, Sort s){
        Context c = PathTracker.ctx;
        /**
         * Create var, assign value and add to path constraint.
         * We show how to do it for creating new symbols, please
         * add similar steps to the functions below in order to
         * obtain a path constraint.
         */
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s);
        PathTracker.addToModel(c.mkEq(z3var, value));
        return new MyVar(z3var, name);
    }

    static MyVar createInput(String name, Expr value, Sort s){
        // Create an input var, these should be free variables!
        Context c = PathTracker.ctx;

        Expr z3var = c.mkString(""); // change this line to the correct code for creating a z3var.
        
        // The following code is to add an additional constraint on the input variable.
        // The input variable must have a value that is equal to one of the input symbols.
        BoolExpr constraint = c.mkFalse();
        for (String input: PathTracker.inputSymbols) {
            constraint = c.mkOr(c.mkEq(z3var, c.mkString(input)), constraint);
        }

        PathTracker.addToModel(constraint);

        return new MyVar(PathTracker.ctx.mkString(""));
    }

    static MyVar createBoolExpr(BoolExpr var, String operator){
        // Handle the following unary operators: !
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createBoolExpr(BoolExpr left_var, BoolExpr right_var, String operator){
        // Handle the following binary operators: &, &&, |, ||
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createIntExpr(IntExpr var, String operator){
        // Handle the following unary operators for numerical operations: +, -
        if(operator.equals("+") || operator.equals("-"))
            return new MyVar(PathTracker.ctx.mkInt(0));
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createIntExpr(IntExpr left_var, IntExpr right_var, String operator){
        // Handle the following binary operators for numerical operations: +, -, /, *, %, ^, ==, <=, <, >= and >
        if(operator.equals("+") || operator.equals("-") || operator.equals("/") || operator.equals("*") || operator.equals("%") || operator.equals("^"))
            return new MyVar(PathTracker.ctx.mkInt(0));
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static MyVar createStringExpr(SeqExpr left_var, SeqExpr right_var, String operator){
        // We only support String.equals
        return new MyVar(PathTracker.ctx.mkFalse());
    }

    static void assign(MyVar var, String name, Expr value, Sort s){
        // All variable assignments, use single static assignment
    }

    static void encounteredNewBranch(MyVar condition, boolean value, int line_nr){
        // Call the solver
    }

    static void newSatisfiableInput(LinkedList<String> new_inputs) {
        // Hurray! found a new branch using these new inputs!
        // Remove the extra quotes from the inputs that were find by the solver.
        List<String> trimmed_new_inputs = new_inputs.stream()
                .map(s -> s.replaceAll("\"", ""))
                .collect(Collectors.toList());

    }

    /**
     * Method for fuzzing new inputs for a program.
     * @param inputSymbols the inputSymbols to fuzz from.
     * @return a fuzzed sequence
     */
    static List<String> fuzz(String[] inputSymbols){
        /*
         * Add here your code for fuzzing a new sequence for the RERS problem.
         * You can guide your fuzzer to fuzz "smart" input sequences to cover
         * more branches using concolic execution. Right now we just generate
         * a complete random sequence using the given input symbols. Please
         * change it to your own code.
         */
        return generateRandomTrace(inputSymbols);
    }

    /**
     * Generate a random trace from an array of symbols.
     * @param symbols the symbols from which a trace should be generated from.
     * @return a random trace that is generated from the given symbols.
     */
    static List<String> generateRandomTrace(String[] symbols) {
        ArrayList<String> trace = new ArrayList<>();
        for (int i = 0; i < traceLength; i++) {
            trace.add(symbols[r.nextInt(symbols.length)]);
        }
        return trace;
    }

    static void run() {
        initialize(PathTracker.inputSymbols);
        PathTracker.runNextFuzzedSequence(currentTrace.toArray(new String[0]));
        // Place here your code to guide your fuzzer with its search using Concolic Execution.
        while(!isFinished) {
            // Do things!
            try {
                System.out.println("Woohoo, looping!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void output(String out){
        System.out.println(out);
    }

}