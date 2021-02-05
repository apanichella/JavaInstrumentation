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
    private String currentTraceSymbol;

    /**
     * Constructor
     * @param inputSymbols the input symbols for a given RERS problem.
     */
    public Fuzzer(String[] inputSymbols) {
        this.inputSymbols = inputSymbols;
        random = new Random();
        traces = new LinkedList<>();
        currentTrace = generateRandomTrace(this.inputSymbols);
    }

    /**
     * Generate a single input from the list of possible inputs.
     * @return an input
     */
    public String fuzz() {
        String nextInput = null;
        // Check if the current trace is empty and if it is
        // then we grab a new trace from the list of traces.
        if (currentTrace.isEmpty() && !traces.isEmpty()) {
            currentTrace = traces.poll();
            nextInput = currentTrace.remove(0);
            currentTraceSymbol = nextInput;
        }
        // If we are not done running on the current trace,
        // grab the next input from the current trace.
        else if (!currentTrace.isEmpty()) {
            nextInput = currentTrace.remove(0);
            currentTraceSymbol = nextInput;
        }
        // If we have tried everything, generate a new random trace.
        else {
            currentTrace = generateRandomTrace(inputSymbols);
            nextInput = currentTrace.remove(0);
        }

        return nextInput;
    }


    /**
     * Generate a random trace from an array of symbols.
     * @param symbols the symbols from which a trace should be generated from.
     * @return a random trace that is generated from the given symbols.
     */
    public List<String> generateRandomTrace(String[] symbols) {
        ArrayList<String> trace = new ArrayList<>();
        for (int i = 0; i < traceLength; i++) {
            trace.add(symbols[random.nextInt(symbols.length)]);
        }
        trace.add("R"); // Reset symbol that marks that we have arrived at the end of a trace.
        return trace;
    }

    public List<String> getCurrentTrace() {
        return currentTrace;
    }
    public int getTraceLength() {
        return traceLength;
    }
    public String getCurrentTraceSymbol() {return currentTraceSymbol;}
    public void setTraceLength(int length) {traceLength = length;}
}