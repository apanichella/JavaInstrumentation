package nl.tudelft.instrumentation.learning;

/**
 * @author Bram Verboom
 */

public class MealyTransition {

    public final String output;
    public final MealyState to;

    public MealyTransition(String output, MealyState to) {
        this.output = output;
        this.to = to;
    }

}
