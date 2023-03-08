package nl.tudelft.instrumentation.patching;
import nl.tudelft.instrumentation.runner.CallableTraceRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
/**
 * This class is used to start and run the tests for the algorithm that you
 * will build to do automated code patching.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class OperatorTracker {
    static String[] operators;
    static Vector<String[]> tests = new Vector< String[] >();
    static boolean testing = false;
    static String outputs = "";
    static int current_test = 0;
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    static CallableTraceRunner<Void> problem;

    // Longest a single testcase is allowed to run
    static final int timeoutMS = 1000;

    /**
     * This method is used to call the encounteredOperator method from the FuzzingLab class.
     * This methods handles operators for integers.
     * @param operator the operator of the operation.
     * @param left left-hand side of the expression.
     * @param right right-hand side of the expression.
     * @param operator_nr the id of the operator.
     * @return
     */
    public static boolean myOperator(String operator, int left, int right, int operator_nr){
        return PatchingLab.encounteredOperator(operator, left, right, operator_nr);
    }
    /**
     * This method is used to call the encounteredOperator method from the FuzzingLab class.
     * This methods handles operators for booleans.
     * @param operator the operator of the operation.
     * @param left left-hand side of the expression.
     * @param right right-hand side of the expression.
     * @param operator_nr the id of the operator.
     * @return
     */
    public static boolean myOperator(String operator, boolean left, boolean right, int operator_nr){
        return PatchingLab.encounteredOperator(operator, left, right, operator_nr);
    }

    /**
     * Append the output of the program to a list containing all outputs.
     * @param out
     */
    public static void output(String out){
        PatchingLab.output(out);
        outputs = outputs + out;
    }

    /**
     * Check the output of the program with the expected output from
     * the a test case and see if they match. Thus this method is used to
     * assess how well you algorithm is working.
     */
    public static boolean checkOutput(int current_test) {
        if (outputs.equals(tests.elementAt(current_test)[1])) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Initialize some of the fields in this class.
     * @param o the list of operators.
     */
    public static void initialize(String[] o){
        operators = o;
        readTests();
    }
    /**
     * Read the test cases from a file. In this case we are reading the test cases
     * from "tests.txt"
     */
    public static void readTests(){
        String testCaseFile = String.format("/rers2020_test_cases/%sTestcases.txt", problem.getClass().getSimpleName());
        try (Stream<String> stream = new BufferedReader(
                new InputStreamReader(OperatorTracker.class.getResourceAsStream(testCaseFile))).lines()
        )
        {
            stream.forEach(s -> {
                tests.add(s.split("->"));
            });
//            System.out.println("Read tests:");
//            for(String[] s : tests){
//                for(String st : s) System.out.print(st + " ");
//                System.out.println();
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initialize and hand over control to PatchingLab
     * @param operators The operators used in the current problem
     * @param eca The current problem instance
     */
    public static void run(String[] operators, CallableTraceRunner<Void> eca) {
        problem = eca;
        initialize(operators);
        PatchingLab.run();
    }

    /**
     * Runs the test with the given index,
     * returning true if it passes, false if it fails.
     * We also apply a timeout in case a test accidentally
     * enters an infinite loop
     * @param testIndex index of the test to run
     * @return whether the test passed or not
     */
    public static boolean runTest(int testIndex) {
        current_test = testIndex;

        // Pass the test input to the problem
        String[] test = tests.get(testIndex);
        String[] testInput = test[0].split(",");
        problem.setSequence(testInput);

        // Reset the output tracking
        outputs = "";

        // Schedule the trace to be ran, and cancel after timeout
        final Future handler = executor.submit(problem);
        executor.schedule(() -> {
            handler.cancel(true);
        }, timeoutMS, TimeUnit.MILLISECONDS);

        // Wait for it to be completed
        boolean wasCancelled = false;
        try {
            handler.get();
        } catch (CancellationException e) {
            wasCancelled = true;
            System.out.println("TIMEOUT!");
            System.exit(-1);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Return the result
        if (wasCancelled) {
            return false;
        }
        return checkOutput(testIndex);
    }

    static List<Boolean> runAllTests() {
        return IntStream.range(0, tests.size())
                .mapToObj(OperatorTracker::runTest)
                .collect(Collectors.toList());
    }
}