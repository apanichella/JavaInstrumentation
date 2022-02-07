package nl.tudelft.instrumentation.fuzzing;
import nl.tudelft.instrumentation.runner.CallableTraceRunner;
import java.util.concurrent.*;

/**
 * This class is used to convert each Java primitive type (or an expression)
 * into a MyVar object. The MyVar objects makes it easier for the computation
 * of the branch distance when the myIf method is called.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class DistanceTracker {
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    static CallableTraceRunner<Void> problem;
    static String[] inputSymbols;
    // Longest a single testcase is allowed to run
    static final int timeoutMS = 1000;

    /**
     * Converts a boolean to a MyVar object
     * @param b the boolean that needs to be converted.
     * @return a MyVar objects that represents a boolean value.
     */
    public static MyVar MyVar(boolean b){
        return new MyVar(b);
    }

    /**
     * Converts an integer to a MyVar object
     * @param i the integer that needs to be converted.
     * @return a MyVar objects that represents a integer value.
     */
    public static MyVar MyVar(int i){
        return new MyVar(i);
    }

    /**
     * Converts a string to a MyVar object
     * @param s the string that needs to be converted.
     * @return a MyVar objects that represents a string value.
     */
    public static MyVar MyVar(String s){
        return new MyVar(s);
    }

    /**
     * Converts a binary expression in a MyVar Object. This makes it easier
     * to keep track of the operator and the values that were used in the
     * expression.
     * @param i the left-hand side variable/value.
     * @param j the right-hand side variable/value.
     * @param operator the operator of the binary expression.
     * @return a MyVar object that represents a binary expression.
     */
    public static MyVar binaryExpr( MyVar i, MyVar j, String operator ){
        return new MyVar(i,j,operator);
    }

    /**
     * Converts an un expression in a MyVar Object. This makes it easier
     * to keep track of the operator and the value that was used in the
     * expression.
     * @param i the variable/value.
     * @param operator the operator of the unary expression.
     * @return a MyVar object that represents an unary expression.
     */
    public static MyVar unaryExpr(MyVar i, String operator){
        return new MyVar(i,operator);
    }

    /**
     * Converts an equal expression in Java to a MyVar object. This essentially
     * changes it to a binary expression.
     * @param i the left-hand side variable/value
     * @param j the right-hand side variable/value
     * @return a MyVar object that represents a Java equal expression.
     */
    public static MyVar equals(MyVar i, MyVar j){
        return new MyVar(i,j, "==");
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
        System.out.println("Found a new branch");
        FuzzingLab.encounteredNewBranch(condition, value, line_nr);
    }

    /**
     * Used to catch output from the standard out.
     * @param out the string that has been outputted in the standard out.
     */
    public static void output(String out){
        FuzzingLab.output(out);
    }

    /**
     * Initialize and hand over control to FuzzingLab
     * @param eca The current problem instance
     * @param s the input symbols of the problem
     */
    public static void run(String[] s, CallableTraceRunner<Void> eca) {
        problem = eca;
        inputSymbols = s;
        FuzzingLab.run();
    }

    /**
`     * This method is used for running the fuzzed input. It first assigns the
     * fuzzed sequence that needs to be run and then user a handler to
     * start running the sequence through the problem.
     * @param sequence the fuzzed sequence that needs top be run.
     */
    public static void runNextFuzzedSequence(String[] sequence) {
        problem.setSequence(sequence);
        final Future handler = executor.submit(problem);
        executor.schedule(() -> {
            handler.cancel(true);
        }, timeoutMS, TimeUnit.MILLISECONDS);

        // Wait for it to be completed
        try {
            handler.get();
        } catch (CancellationException e) {
            System.out.println("TIMEOUT!");
            System.exit(-1);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
