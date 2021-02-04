package nl.tudelft.instrumentation.fuzzing;

import java.util.*;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to wrap the primitive types and expressions
 * in an object. This way we can keep track of values and operators
 * that were used. It also makes it easier for us to compute the branch
 * distance.
 *
 * @author Sicco Verwer
 */
public class MyVar {

    int type = -1; // the type of the variable

    public boolean value = false; // the boolean value of the variable (if it's a boolean).
    public int int_value = 0; // the integer value of the variable (if it's an integer).
    public String str_value = ""; // the string value of the variable (if it's a string).

    public String operator = ""; // the operator of the expression variable (if it's an expression).
    public MyVar left = null; // the left-hand side value of the expression.
    public MyVar right = null; // the right-hand side value of the expression.

    /**
     * Constructor to wrap a boolean in a MyVar object.
     * @param b the boolean value.
     */
    MyVar(boolean b){
        this.type = 1;
        this.value = b;
    }

    /**
     * Constructor to wrap an integer in a MyVar object.
     * @param i the integer value.
     */
    MyVar(int i){
        this.type = 2;
        this.int_value = i;
    }

    /**
     * Constructor to wrap a string in a MyVar object.
     * @param s the string value.
     */
    MyVar(String s){
        this.type = 3;
        this.str_value = s;
    }

    /**
     * Constructor to wrap an unary expression in a MyVar object.
     * @param l the value/variable of the unary expression
     * @param o  the operator of the unary expression.
     */
    MyVar(MyVar l, String o){
        this.type = 4;
        this.left = l;
        this.operator = o;
    }

    /**
     * Constructor to wrap a binary expression in a MyVar object.
     * @param l the left-hand side value/variable of the binary expression.
     * @param r the right-hand side value/variable of the binary expression.
     * @param o the operator of the binary expression.
     */
    MyVar(MyVar l, MyVar r, String o){
        this.type = 5;
        this.left = l;
        this.right = r;
        this.operator = o;
    }

}
