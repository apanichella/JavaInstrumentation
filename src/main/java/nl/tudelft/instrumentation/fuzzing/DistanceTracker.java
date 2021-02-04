package nl.tudelft.instrumentation.fuzzing;

import java.util.*;
import com.microsoft.z3.*;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to convert each Java primitive type (or an expression)
 * into a MyVar object. The MyVar objects makes it easier for the computation
 * of the branch distance when the myIf method is called.
 *
 * @author Sicco Verwer
 */
public class DistanceTracker {

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
     * @param condition
     * @param value
     * @param line_nr
     */
    public static void myIf(MyVar condition, boolean value, int line_nr){
        System.out.println("Found a new branch");
        FuzzingLab.encounteredNewBranch(condition, value, line_nr);
    }
}
