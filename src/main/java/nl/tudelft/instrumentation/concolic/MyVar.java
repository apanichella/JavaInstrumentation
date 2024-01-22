package nl.tudelft.instrumentation.concolic;

import com.microsoft.z3.*;

/**
 * This class serves as a wrapper object for each of the java primitive
 * types that are used in the RERS problems. With this class, we can
 * transform and store each variable that is defined in a RERS problem
 * as a Z3 Expression. The Z3 expression can then be used in the construction
 * of a path constraint for the solver.
 *
 * @author Sicco Verwer
 */
public class MyVar {
    public Expr z3var; // the Z3 expression that will used in the construction of a path constraint.
    public String name = "v";

    /**
     * Create a new MyVar object from a Z3 expression that has already been given a name before.
     * @param v the Z3 expression.
     */
    MyVar(Expr v){
        this.z3var = v;
    }

    /**
     * Create a new MyVar object from a new Z3 expression that has been created.
     * @param v the Z3 expression
     * @param n the name of the variable.
     */
    MyVar(Expr v, String n){
        this.z3var = v;
        this.name = n;
    }

}
