package nl.tudelft.instrumentation.learning;
import nl.tudelft.instrumentation.runner.CallableTraceRunner;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
/**
 * This class is used to start and run the tests for the algorithm that you
 * will build to do automated code patching.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class LearningTracker {
    static String[] currentInput;
    static String[] inputSymbols;
    static String currentOutput = "";
    static int current_index = 0;
    static List<String> outputs = new ArrayList<>();
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    static CallableTraceRunner<Void> problem;

    // Longest a single testcase is allowed to run
    static final int timeoutMS = 1000;


    /**
     * Append the output of the program to a list containing all outputs.
     * @param out
     */
    public static void output(String out){
        LearningLab.output(out);
        currentOutput = out;
    }

    /**
     * Initialize some of the fields in this class.
     * @param o the list of operators.
     */
    public static void initialize(String[] o){
    }

    /**
     * Initialize and hand over control to PatchingLab
     * @param inputSymbols The input symbols of the problem
     * @param eca The current problem instance
     */
    public static void run(String[] inputSymbols, CallableTraceRunner<Void> eca) {
        problem = eca;
        LearningTracker.inputSymbols = inputSymbols;
        LearningLab.run();
    }

    /**
`     * This method is used for running the fuzzed input. It first assigns the
     * fuzzed sequence that needs to be run and then user a handler to
     * start running the sequence through the problem.
     * @param sequence the fuzzed sequence that needs top be run.
     */
    public static void runNextTrace(String[] sequence) {
        currentOutput = "";
        problem.setSequence(sequence);
        currentInput = sequence;
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

    public static void processedInput(){
        System.out.printf("after input %s: %d: output '%s'\n", currentInput[current_index], current_index, currentOutput);
        current_index++;
        currentOutput = "";
    }
}
