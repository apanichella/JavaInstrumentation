package nl.tudelft.instrumentation.fuzzing;

import java.util.*;
import java.util.Random;

/**
 * You should write your own solution using this class.
 */
public class FuzzingLab {
        static Random r = new Random();
        static List<String> currentTrace;
        static int traceLength = 10;
        static String currentTraceSymbol;


        /**
         * Write your solution that specifies what should happen when a new branch has been found.
         */
        static void encounteredNewBranch(MyVar condition, boolean value, int line_nr){
                // do something useful
                System.out.println(condition.toString());
        }

        /**
         * Method for fuzzing new inputs for a program.
         * @param inputSymbols the inputSymbols
         * @return a fuzzed input
         */
        static String fuzz(String[] inputSymbols){

                String nextInput = null;
                // If the current trace does not exist,
                // then generate a random one.
                if (currentTrace == null) {
                        currentTrace = generateRandomTrace(inputSymbols);
                        nextInput = currentTrace.remove(0);
                }
                // Check if the current trace is empty and if it is
                // then generate a new random trace.
                else if (currentTrace.isEmpty()) {
                        currentTrace = generateRandomTrace(inputSymbols);
                        nextInput = currentTrace.remove(0);
                }
                // If we are not done running on the current trace,
                // grab the next input from the current trace.
                else {
                        nextInput = currentTrace.remove(0);
                        currentTraceSymbol = nextInput;
                }

                return nextInput;
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
                trace.add("#"); // Reset symbol that marks that we have arrived at the end of a trace.
                return trace;
        }

        /**
         * Method that is used for catching the output from standard out.
         * You should write your own logic here.
         * @param out the string that has been outputted in the standard out.
         */
        static void output(String out){
                System.out.println(out);
        }
}
