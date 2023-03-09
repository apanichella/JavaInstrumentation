package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Bram Verboom
 *         The observation of the L* algorithm for learning mealy machines.
 */

public class ObservationTable {
    private static final String SEPERATOR = ",";
    private static final String LAMBDA = " ";
    private static final List<String> EMPTY = new ArrayList<>();

    private String[] alphabet;

    private List<List<String>> S;
    private List<List<String>> E;

    // The actual observations: a map with (S u S A) as keys where each value
    // (row) represents the observations corresponding to (S u S A) E.
    private Map<List<String>, ArrayList<String>> table;
    private SystemUnderLearn sul;

    public ObservationTable(String[] alphabet, SystemUnderLearn sul) {
        this.sul = sul;
        this.S = new ArrayList<>();
        this.E = new ArrayList<>();
        this.alphabet = alphabet;
        table = new HashMap<>();
        this.addToS(new ArrayList<>());
        for (String s : this.alphabet) {
            ArrayList<String> sym = new ArrayList<>();
            sym.add(s);
            this.addToE(sym);
        }
    }

    public List<String> join(List<String> a, List<String> b) {
        List<String> joined = new ArrayList<>(a);
        joined.addAll(b);
        return joined;
    }

    public List<String> join(List<String> a, String b) {
        List<String> joined = new ArrayList<>(a);
        joined.add(b);
        return joined;
    }

    public List<String> join(String a, List<String> b) {
        List<String> joined = new ArrayList<>(b.size()+1);
        joined.add(a);
        joined.addAll(b);
        return joined;
    }

    public String[] toArrayTrace(List<String> trace) {
        return trace.toArray(new String[0]);
    }

    private String getResult(List<String> trace) {
        String res = sul.getLastOutput(toArrayTrace(trace));
        // System.out.printf("Output for trace %s is %s\n", trace, res);
        return res;
    }

    /**
     * Method that is used for adding a new prefix to S
     * 
     * @param prefix the prefix to add to S, must be a list of symbols in the
     *               alphabet
     */
    public void addToS(List<String> prefix) {
        System.out.printf("Adding %s to S\n", String.join(",", prefix));
        if (!S.contains(prefix)) {
            S.add(prefix);
            addRow(prefix);
            for (String symbol : alphabet) {
                addRow(join(prefix, symbol));
            }
        }
    }

    /**
     * Method that is used for adding a new suffix to E
     *
     * @param suffix the suffix to add to E, must be a list of symbols in the
     *               alphabet
     */
    public void addToE(List<String> suffix) {
        System.out.printf("Adding %s to E\n", String.join(",", suffix));
        if (!E.contains(suffix)) {
            E.add(suffix);
            for (Entry<List<String>, ArrayList<String>> entry : table.entrySet()) {
                List<String> joined = join(entry.getKey(), suffix);
                entry.getValue().add(getResult(joined));
            }
        }
    }

    /**
     * Method for adding a row to the observation table.
     *
     * Adds a row to the observation table and fills it with the correct
     * observations.
     */
    private void addRow(List<String> base) {
        if (table.containsKey(base)) {
            return;
        } else {
            ArrayList<String> row = new ArrayList<>();
            for (List<String> e : E) {
                row.add(getResult(join(base, e)));
            }
            table.put(base, row);
        }
    }

    /**
     * Method that is used for checking whether the observation table is closed
     *
     * You should write your own logic here.
     *
     * @return an Optional.empty() if the table is consistent, or an Optional.of(_)
     *         with something usefull to extend the observation table with.
     */
    public Optional<List<String>> checkForClosed() {
        // TODO
        return Optional.empty();
    }

    /**
     * Method that is used for checking whether the observation table is consistent
     *
     * You should write your own logic here.
     *
     * @return an Optional.empty() if the table is consistent, or an Optional.of(_)
     *         with something usefull to extend the observation table with.
     */
    public Optional<List<String>> checkForConsistent() {
        // TODO
        return Optional.empty();
    }

    private String rowToKey(ArrayList<String> input) {
        return String.join(",", input);
    }

    /**
     * Method to generate a {@link MealyMachine} from this observation table.
     *
     * Note: in order to generate a MealyMachine the observation table must be
     * consistent and closed.
     *
     * @return an MealyMachine that reflects the observations
     */
    public MealyMachine generateHypothesis() {
        Map<String, MealyState> states = new HashMap<>();
        int numStates = 0;
        for (List<String> s : S) {
            String key = rowToKey(table.get(s));
            if (!states.containsKey(key)) {
                states.put(key, new MealyState(String.format("s%s", numStates++)));
            }
        }
        for (List<String> s : S) {
            ArrayList<String> baseRow = table.get(s);
            MealyState from = states.get(rowToKey(baseRow));
            int index_in_alphabet = 0;
            for (String sym : alphabet) {
                List<String> base = join(s, sym);
                ArrayList<String> row = table.get(base);
                String toKey = rowToKey(row);
                String output = baseRow.get(index_in_alphabet++);

                if (states.containsKey(toKey)) {
                    MealyTransition newTransition = new MealyTransition(output, states.get(toKey));
                    MealyTransition t = from.next(sym);
                    if (t != null && !t.equals(newTransition)) {
                        // assert false: "Observation table is not consistent";
                        MealyState unknownState = new MealyState(String.format("\"s%s?\"", numStates++));
                        newTransition = new MealyTransition(output, unknownState);
                        for (String sym2 : alphabet) {
                            unknownState.addEdge(sym2, new MealyTransition("?", unknownState));
                        }
                        from.addEdge(sym, new MealyTransition("?", unknownState));
                    } else {
                        from.addEdge(sym, newTransition);
                    }
                } else {
                    // assert false : "Observation table is not closed";
                    MealyState unknownState = new MealyState(String.format("\"s%s?\"", numStates++));
                    MealyTransition newTransition = new MealyTransition(output, unknownState);
                    for (String sym2 : alphabet) {
                        MealyTransition newTransition2 = new MealyTransition("?", unknownState);
                        unknownState.addEdge(sym2, newTransition2);
                    }
                    from.addEdge(sym, newTransition);
                }
            }
        }
        MealyState initialState = states.get(rowToKey(table.get(EMPTY)));
        return new MealyMachine(initialState);
    }

    public static String pretty(List<String> trace) {
        if(trace.size() == 0)  {
            return LAMBDA;
        }
        return String.join(SEPERATOR, trace);

    }

    /**
     * Method to print the observation table in a nice way.
     */
    public void print() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> header = new ArrayList<>();
        rows.add(null);
        header.add("T");
        for (List<String> e : E) {
            header.add(pretty(e));
        }
        rows.add(header);
        rows.add(null);
        for (List<String> s : S) {
            ArrayList<String> row = new ArrayList<>();
            row.add(pretty(s));
            row.addAll(table.get(s));
            rows.add(row);
        }
        rows.add(null);
        for (List<String> s : S) {
            for (String symbol : alphabet) {
                List<String> joined = join(s, symbol);
                ArrayList<String> row = new ArrayList<>();
                row.add(pretty(joined));
                row.addAll(table.get(joined));
                rows.add(row);
            }
        }
        rows.add(null);
        int[] minSizes = new int[rows.get(1).size()];
        Arrays.fill(minSizes, 1);
        for (ArrayList<String> row : rows) {
            if (row == null) {
                continue;
            }
            for (int i = 0; i < minSizes.length; i++) {
                minSizes[i] = Math.max(minSizes[i], row.get(i).length());
            }
        }
        String empty = "·";
        for (int i = 0; i < minSizes.length; i++) {
            for (int j = 0; j < minSizes[i]; j++) {
                empty += "─";
            }
            empty += "·";
        }
        for (ArrayList<String> row : rows) {
            if (row == null) {
                System.out.println(empty);
                continue;
            }
            for (int i = 0; i < minSizes.length; i++) {
                int l = minSizes[i];
                String f = "%-" + l + "s";
                // System.out.println(f);
                row.set(i, String.format(f, row.get(i)));
            }
            System.out.printf("│%s│\n", String.join("│", row));
        }
    }

}
