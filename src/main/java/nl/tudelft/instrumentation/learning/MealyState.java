package nl.tudelft.instrumentation.learning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Bram Verboom
 */

public class MealyState {

    // Outgoing edges of the state. Each edge consist of an input symbol and a
    // transition.
    private Map<String, MealyTransition> edges;
    public final String name;

    public MealyState(String name) {
        this.name = name;
        this.edges = new HashMap<>();
    }

    public void addEdge(String symbol, MealyTransition transition) {
        edges.put(symbol, transition);
    }

    public MealyTransition next(String symbol) {
        return edges.get(symbol);
    }

    public Set<MealyState> getNextStates() {
        return this.edges.values().stream().map(MealyTransition::getToState).collect(Collectors.toSet());
    }

    public Set<Entry<String, MealyTransition>> getTransitions() {
        return this.edges.entrySet();
    }
}
