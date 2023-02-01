package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bram Verboom
 */

public class MealyMachine extends SystemUnderLearn {

    private MealyState initialState;

    public MealyMachine(MealyState initialState) {
        this.initialState = initialState;
    }

    public String[] getOutput(String[] trace) {
        MealyState s = initialState;
        List<String> output = new ArrayList<>();
        for (String sym : trace) {
            MealyTransition t = s.next(sym);
            output.add(t.output);
            s = t.to;
        }
        assert output.size() == trace.length;
        return output.toArray(String[]::new);
    }

}
