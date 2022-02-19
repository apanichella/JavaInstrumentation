package nl.tudelft.instrumentation.fuzzing;

import java.util.*;
import java.util.Random;


import org.apache.commons.lang3.StringUtils;

/**
 * You should write your own solution using this class.
 */
public class FuzzingLab {
        static Random r = new Random();
        static List<String> currentTrace;
        static int traceLength = 10;
        static boolean isFinished = false;

        // constants
        static int K = 1;

        static void initialize(String[] inputSymbols){
                // Initialise a random trace from the input symbols of the problem.
                currentTrace = generateRandomTrace(inputSymbols);
        }

        @SuppressWarnings("deprecation")
        static int getLevenshteinDistance(String s1, String s2) {
                return StringUtils.getLevenshteinDistance(s1, s2);
        }

        static double normalize(double distance) {
                return distance / (distance + 1.0);
        }

        static double branchDistance(MyVar condition) {
                MyVar left = condition.left;
                MyVar right = condition.right;


               if(condition.type == TypeEnum.BOOL){
                        // reverse the binary
                        if(condition.value){
                                return 0;
                        }
                        return 1;

               } else if (condition.type == TypeEnum.BINARY){
                       switch (condition.operator){
                                case "<":
                                        if(right.type == TypeEnum.INT && left.type == TypeEnum.INT)
                                                if (right.int_value < left.int_value){
                                                        return 0;
                                                }
                                                else {
                                                        return - right.int_value + left.int_value + K;
                                                }
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);
                                
                                case "<=":
                                        if(right.type == TypeEnum.INT && left.type == TypeEnum.INT)
                                                if (right.int_value <= left.int_value){
                                                        return 0;
                                                }
                                                else {
                                                        return - right.int_value + left.int_value;
                                                }
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);
                                
                                case ">":
                                        if(right.type == TypeEnum.INT && left.type == TypeEnum.INT)
                                                if (right.int_value > left.int_value){
                                                        return 0;
                                                }
                                                else {
                                                        return right.int_value - left.int_value + K;
                                                }
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);
                        
                                case "=>":
                                        if(right.type == TypeEnum.INT && left.type == TypeEnum.INT)
                                                if (right.int_value >= left.int_value){
                                                        return 0;
                                                }
                                                else {
                                                        return right.int_value - left.int_value;
                                                }
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);
                
                
                                case "&&":
                                        return normalize(branchDistance(left)) + normalize(branchDistance(right));

                                case "||":
                                        return Math.min(normalize(branchDistance(left)), normalize(branchDistance(right)));

                                case "==":
                                        if(right.type == TypeEnum.INT && left.type == TypeEnum.INT){
                                                return Math.abs(left.int_value - right.int_value);
                                        }
                                        else if(right.type == TypeEnum.BOOL && left.type == TypeEnum.BOOL){
                                                return right.value != left.value ? 1 : 0;
                                        }
                                        else if(right.type == TypeEnum.STRING && left.type == TypeEnum.STRING){
                                                return getLevenshteinDistance(right.str_value, left.str_value);
                                        }
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);

                                case "!=":
                                        if(right.type == TypeEnum.INT && left.type == TypeEnum.INT){
                                                return right.int_value != left.int_value ? 1 : 0;
                                        }
                                        else if(right.type == TypeEnum.BOOL && left.type == TypeEnum.BOOL){
                                                return right.value != left.value ? 1 : 0;
                                        }
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);

                                default:
                                        System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);
                               
                       }
               }

                System.out.println("Error - Condition type: " + condition.type.toString() + ", Operator: " + condition.operator);
                return 0;
        }

        /**
         * Write your solution that specifies what should happen when a new branch has been found.
         */
        static void encounteredNewBranch(MyVar condition, boolean value, int line_nr) {
                // do something useful
                System.out.println(condition.toString());
                System.out.println(value);
                System.out.println(line_nr);
                System.out.println("Distance" + branchDistance(condition));
                System.out.println("---------------");
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
                 * more branches. Right now we just generate a complete random sequence
                 * using the given input symbols. Please change it to your own code.
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
                initialize(DistanceTracker.inputSymbols);
                DistanceTracker.runNextFuzzedSequence(currentTrace.toArray(new String[0]));

                // Place here your code to guide your fuzzer with its search.
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

        /**
         * Method that is used for catching the output from standard out.
         * You should write your own logic here.
         * @param out the string that has been outputted in the standard out.
         */
        public static void output(String out){
                System.out.println(out);
        }
}
