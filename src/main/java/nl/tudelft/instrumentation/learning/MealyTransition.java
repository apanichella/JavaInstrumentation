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


    public boolean equals(Object other) {
        if (other instanceof MealyTransition) {
            MealyTransition that = (MealyTransition) other;
            return this.output.equals(that.output) && this.to.equals(that.to);
        }
        return false;

    }

    public MealyState getToState() {
        return to;
    }


}
