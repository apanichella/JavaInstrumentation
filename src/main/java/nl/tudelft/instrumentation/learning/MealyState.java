package nl.tudelft.instrumentation.learning;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bram Verboom
 */

public class MealyState {

    // Outgoing edges of the state. Each edge consist of an input symbol and a
    // transition.
    private Map<String, MealyTransition> edges;

    public MealyState() {
        this.edges = new HashMap<>();
    }

    public void addEdge(String symbol, MealyTransition transition) {
        edges.put(symbol, transition);
    }

    public MealyTransition next(String symbol) {
        return edges.get(symbol);
    }

}
