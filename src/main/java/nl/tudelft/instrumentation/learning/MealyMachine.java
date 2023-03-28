package nl.tudelft.instrumentation.learning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

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
        return output.toArray(new String[0]);
    }

    public MealyState[] getStates() {
        Set<MealyState> states = new LinkedHashSet<>(); // Use LinkedHashSet to maintain insertion order
        List<MealyState> q = new ArrayList<>();
        q.add(initialState);
        states.add(initialState);
        while (!q.isEmpty()) {
            MealyState s = q.remove(0);
            for (MealyState next : s.getNextStates()) {
                if (states.add(next)) {
                    q.add(next);
                }
            }
        }

        return states.toArray(new MealyState[0]);
    }

    public String getColor(String s) {
        if (s.contains("?")) {
            return "red";
        }
        return "black";
    }

    public void writeToDot(String filename) {
        MealyState[] states = getStates();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            out.write("digraph {\nrankdir=LR\n");
            for (MealyState state : states) {
                out.write(
                        String.format("\t%s [color=\"%s\"]\n", state.name, getColor(state.name)));
                for (Entry<String, MealyTransition> edge : state.getTransitions()) {
                    String label = edge.getValue().output;
                    out.write(String.format("\t%s -> %s [ label=\"%s/%s\" color=\"%s\"]\n", state.name,
                            edge.getValue().to.name,
                            edge.getKey(), label, getColor(label)));
                }
            }
            out.write("}\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
