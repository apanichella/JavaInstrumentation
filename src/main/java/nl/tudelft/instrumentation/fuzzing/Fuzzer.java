package nl.tudelft.instrumentation.fuzzing;
import java.util.*;

/**
 * A fuzzer that generates input. This fuzzer can be made smarter
 * by adapting its search strategy.
 */
public class Fuzzer {

    private String[] inputSymbols;
    private Queue<List<String>> traces;
    private List<String> currentTrace;
    private Random random;
    private int traceLength = 10;

    /**
     * Constructor
     * @param inputSymbols the input symbols for a given RERS problem.
     */
    public Fuzzer(String[] inputSymbols) {
        this.inputSymbols = inputSymbols;
        random = new Random();
        traces = new LinkedList<>();
        currentTrace = generateRandomTrace();

    }

    /**
     * Generate a single input from the list of possible inputs.
     * @return an input
     */
    public String fuzz() {
        String nextInput;
        // Check if the current trace is empty and if it is
        // then we grab a new trace from the list of traces.
        if (currentTrace.isEmpty() && !traces.isEmpty()) {
            currentTrace = traces.poll();
            nextInput = currentTrace.remove(0);
        }
        // If we are not done running on the current trace,
        // grab the next input from the current trace.
        else if (!currentTrace.isEmpty()) {
            nextInput = currentTrace.remove(0);
        }
        // If the current is empty and we don't have anymore
        // traces in the list of traces, then we generate a new random trace.
        else {
            currentTrace = generateRandomTrace();
            nextInput = currentTrace.remove(0);
        }
        return nextInput;
    }


    /**
     * Generate a random trace from the input symbols that were givent to
     * the fuzzer.
     * @return a trace generated from the input symbols.
     */
    private List<String> generateRandomTrace() {
        ArrayList<String> trace = new ArrayList<>();
        for (int i = 0; i < traceLength; i++) {
            trace.add(inputSymbols[random.nextInt(inputSymbols.length)]);
        }
        return trace;
    }


    /**
     * Mutate a given input trace with given probabilities.
     * @param inputTrace the input trace to mutate.
     * @return the mutated input trace.
     */
    public List<String> mutate(List<String> inputTrace){
        /**
         * Insert code here to do mutation on an input trace.
         */
        return inputTrace;

    }

}