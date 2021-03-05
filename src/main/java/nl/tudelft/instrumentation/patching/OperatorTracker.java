package nl.tudelft.instrumentation.patching;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to start and run the tests for the algorithm that you
 * will build to do automated code patching.
 *
 * @ Sicco Verwer.
 */
public class OperatorTracker {

    static String[] operators;
    static Vector<String[]> tests = new Vector< String[] >();
    static boolean testing = false;

    static LinkedList< String > next_test = new LinkedList< String >();
    static String outputs = new String();
    static LinkedList<Boolean> test_result = new LinkedList<Boolean>();
    static int current_test = 0;


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
     * Fuzz input from a given list of input symbols.
     * @param inputSymbols the input symbols
     * @return a fuzzed input.
     */
    public static String fuzz(String[] inputSymbols){
        if(testing){
            if(!next_test.isEmpty()) return next_test.pop();
            checkOutput();
            addTest();
            return "#";
        }
        return PatchingLab.fuzz(inputSymbols);
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
     * Start running the tests.
     */
    public static void startTesting(){
        test_result.clear();
        next_test.clear();
        outputs = "";
        current_test = -1;
        testing = true;
    }

    /**
     * Add a test case to the list of tests.
     */
    public static void addTest(){
        if(current_test >= tests.size()){
            current_test = 0;
            testing = false;
        } else {
            outputs = "";
            next_test.clear();
            for(String s : tests.elementAt(current_test)[0].split(",")) next_test.add(s);
        }
    }

    /**
     * Check the output of the program with the expected output from
     * the a test case and see if they match. Thus this method is used to
     * assess how well you algorithm is working.
     */
    public static void checkOutput() {
        if (current_test != -1){
            System.out.println(current_test);
            if (outputs.equals(tests.elementAt(current_test)[1])) {
                test_result.add(new Boolean(true));
            } else {
                test_result.add(new Boolean(false));
            }
        }
        current_test++;
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
        try (Stream<String> stream = new BufferedReader(
                new InputStreamReader(OperatorTracker.class.getResourceAsStream("/tests.txt"))).lines()
            )
        {
            stream.forEach(s -> {
                tests.add(s.split("->"));
            });
            System.out.println("Read tests:");
            for(String[] s : tests){
                for(String st : s) System.out.print(st + " ");
                System.out.println();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
