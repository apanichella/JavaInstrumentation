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
import java.util.stream.Collectors;

/**
 * @author Bram Verboom
 *         The observation of the L* algorithm for learning mealy machines.
 */

public class ObservationTable {
    private static final String SEPERATOR = ",";
    private static final String LAMBDA = "λ";

    private String[] alphabet;

    private List<String> S;
    private List<String> E;

    // The actual observations: a map with (S ∪ S • A) as keys where each value
    // (row) represents the observations corresponding to (S ∪ S • A) • E.
    private Map<String, ArrayList<String>> table;
    private SystemUnderLearn sul;

    public ObservationTable(String[] alphabet, SystemUnderLearn sul) {
        this.sul = sul;
        this.S = new ArrayList<>();
        this.E = new ArrayList<>();
        this.alphabet = alphabet;
        table = new HashMap<>();
        this.addToS(new String[] {});
        for(String s : this.alphabet) {
            this.addToE(new String[] {s});
        }
    }

    public String join(String... symbols) {
        if (symbols.length == 0) {
            return LAMBDA;
        }
        // Join the symbols with the SEPERATOR, but remove any empty strings
        return String.join(SEPERATOR, Arrays.stream(symbols)
                .filter(item -> !item.isEmpty() && !item.equals(LAMBDA))
                .collect(Collectors.toList()));
    }

    public String[] toArrayTrace(String trace) {
        // Split the symbols on the SEPERATOR and removing any empty strings
        return Arrays.stream(trace.split(SEPERATOR))
                .filter(item -> !item.isEmpty() && !item.equals(LAMBDA))
                .toArray(String[]::new);
    }

    private String getResult(String trace) {
        String res = sul.getLastOutput(toArrayTrace(trace));
        // System.out.printf("Output for trace %s is %s\n", trace, res);
        return res;
    }

    /**
     * Method that is used for adding a new prefix to S
     * 
     * @param prefix the prefix to add to S, must be an array of symbols in the
     *               alphabet
     */
    public void addToS(String[] prefix) {
        String s = join(prefix);
        System.out.printf("Adding %s to S\n", s);
        if (!S.contains(s)) {
            S.add(s);
            addRow(s);
            for (String symbol : alphabet) {
                addRow(join(s, symbol));
            }
        }
    }

    /**
     * Method that is used for adding a new suffix to E
     *
     * @param suffix the suffix to add to E, must be an array of symbols in the
     *               alphabet
     */
    public void addToE(String[] suffix) {
        String e = join(suffix);
        System.out.printf("Adding %s to E\n", e);
        if (!E.contains(e)) {
            E.add(e);
            for (Entry<String, ArrayList<String>> entry : table.entrySet()) {
                String joined = join(entry.getKey(), e);
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
    private void addRow(String base) {
        if (table.containsKey(base)) {
            return;
        } else {
            ArrayList<String> row = new ArrayList<>();
            for (String e : E) {
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
    public Optional<String[]> checkForClosed() {
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
    public Optional<String[]> checkForConsistent() {
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
        for (String s : S) {
            String key =rowToKey(table.get(s)); 
            if (!states.containsKey(key)) {
                states.put(key, new MealyState(String.format("s%s", numStates++)));
            }
        }
        for (String s : S) {
            MealyState from = states.get(rowToKey(table.get(s)));
            for (String sym : alphabet) {
                String base = join(s, sym);
                ArrayList<String> row = table.get(base);
                String toKey = rowToKey(row);
                // Make states with "?" transitions
                assert states.containsKey(toKey) : "Observation table is not closed";
                String output = row.get(0);

                MealyTransition newTransition = new MealyTransition(output, states.get(toKey));
                MealyTransition t = from.next(sym);
                if (t != null) {
                    assert t.equals(newTransition) : "Observation table is not consistent";
                }
                from.addEdge(sym, newTransition);
            }
        }
        MealyState initialState = states.get(rowToKey(table.get(LAMBDA)));
        return new MealyMachine(initialState);
    }


    /**
     * Method to print the observation table in a nice way.
     */
    public void print() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> header = new ArrayList<>();
        rows.add(null);
        header.add("T");
        for (String e : E) {
            header.add(e);
        }
        rows.add(header);
        rows.add(null);
        for (String s : S) {
            ArrayList<String> row = new ArrayList<>();
            row.add(s);
            row.addAll(table.get(s));
            rows.add(row);
        }
        rows.add(null);
        for (String s : S) {
            for (String symbol : alphabet) {
                String joined = join(s, symbol);
                ArrayList<String> row = new ArrayList<>();
                row.add(joined);
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
