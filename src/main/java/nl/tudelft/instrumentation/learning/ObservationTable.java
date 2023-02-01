package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A
 */

public class ObservationTable {
    private static final String SEPERATOR = ",";

    private SortedSet<String> S;
    private SortedSet<String> E;
    private String[] alphabet;

    private Map<String, ArrayList<String>> table;

    public ObservationTable(String[] alphabet) {
        this.S = new TreeSet<>();
        this.E = new TreeSet<>();
        String lambda = ""; // The empty string
        this.S.add(lambda);
        this.E.add(lambda);

        this.alphabet = alphabet;
        table = new HashMap<>();
        fillMissing();
    }

    public String join(String... symbols) {
        // Join the symbols with the SEPERATOR, but removing any empty strings
        return String.join(SEPERATOR,
                Arrays.stream(symbols).filter(item -> !item.isEmpty()).collect(Collectors.toList()));
        // return String.join(SEPERATOR, symbols);
    }

    public String[] toArrayTrace(String trace) {
        // Join the symbols with the SEPERATOR, but removing any empty strings
        return Arrays.stream(trace.split(SEPERATOR))
                .filter(item -> !item.isEmpty()).toArray(String[]::new);
    }

    public String getResult(String trace) {
        return LearningTracker.runNextTrace(toArrayTrace(trace));
    }

    public void addRow(String base) {
        if (table.containsKey(base)) {
            return;
        } else {
            ArrayList<String> row = new ArrayList<>();
            for (String e : E) {
                System.out.printf("base: %s, e: %s, joined: %s\n", base, e, join(base, e));
                row.add(getResult(join(base, e)));
            }
            table.put(base, row);
        }
    }

    private void fillMissing() {
        for (String e : E) {
            for (String s : S) {
                String toCheck = join(e, s);
                addRow(toCheck);
                for (String symbol : alphabet) {
                    addRow(join(toCheck, symbol));
                }
            }
        }
    }

    public void print() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> header = new ArrayList<>();
        rows.add(null);
        header.add("T");
        for (String e: E){
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
        int total = 0;
		for (ArrayList<String> row : rows) {
            if (row == null) {
                continue;
            }
            total = 0;
            for (int i = 0; i < minSizes.length; i++) {
                minSizes[i] = Math.max(minSizes[i], row.get(i).length());
                total += minSizes[i];
            }
        }
        String empty = "├";
        for (int i = 0; i < total+rows.get(1).size()-1; i++) {
            empty += "─";
        };
        empty += "┤";
		for (ArrayList<String> row : rows) {
            if (row == null) {
                System.out.println(empty);
                continue;
            }
            for (int i = 0; i < minSizes.length; i++) {
                int l = minSizes[i];
                String f = "%-"+l+"s";
                // System.out.println(f);
                row.set(i, String.format(f, row.get(i)));
            }
            System.out.printf("│%s│\n", String.join("│", row));
        }
    };

}
