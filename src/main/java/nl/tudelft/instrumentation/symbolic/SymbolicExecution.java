package nl.tudelft.instrumentation.symbolic;

import java.sql.SQLOutput;
import java.util.*;
import com.microsoft.z3.*;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to call the Z3 solver and check whether the solver can find
 * solutions (inputs) that can satify the path contraint that is given to the solver.
 *
 * You should write your logic of symbolic execution in this class.
 *
 * @author Sicco Verwer
 */
public class SymbolicExecution {


    /**
     * This method contains code that calls the Z3 solver and check whether it solve the path
     * constraint that is constructed for the new branch. The solver will try to inputs that can
     * satify the path contraint. We can use these inputs to reach this newly discovered branch.
     * @param new_branch the branch that we have discovered and want to visit.
     * @param printmodel boolean value that specifies whether the path constraint should be printed in the terminal or not.
     *                   By default, the path contraints is printed in the terminal.
     */
    public static void solve(BoolExpr new_branch, boolean printmodel){
        Solver s = PathTracker.ctx.mkSolver();

        s.add(PathTracker.z3model);
        s.add(PathTracker.z3branches);
        s.add(new_branch);

        if(printmodel){
            System.out.print("Model: ");
            System.out.println(PathTracker.z3model);
            System.out.print("Branches: ");
            System.out.println(PathTracker.z3branches);
            System.out.print("New branch: ");
            System.out.println(new_branch);
        }

        if(s.check() == Status.SATISFIABLE){
            System.out.println("satisfiable input values:");
            Model m = s.getModel();
            for(MyVar v : PathTracker.inputs){
                System.out.println(m.evaluate(v.z3var, true).toString());
            }
        } else {
            System.out.println("unsatisfiable");
        }
    }

    /**
     * Method that contains the code on what should be done when a new branch has been discovered.
     * @param condition the condition of the branch.
     * @param value the value of the condition.
     * @param line_nr the line number where we have discovered the branch.
     */
    public static void encounteredNewBranch(MyVar condition, boolean value, int line_nr){
        /**
         * Write here your code on what should happen when you have discovered a new branch.
         * In the lines below, you can see that we have made a call to the "solve" function.
         * This functions tries to feed the path constraint that is constructed so far to the
         * Z3 solver and see whether it can find inputs that satisfy the path constraint.
         *
         * What can you do with the inputs that are found by the solver?
         */
        solve(PathTracker.ctx.mkEq(condition.z3var, PathTracker.ctx.mkFalse()), true);
        solve(PathTracker.ctx.mkEq(condition.z3var, PathTracker.ctx.mkTrue()), true);
    }
}
