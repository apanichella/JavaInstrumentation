package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * The observation of the L* algorithm for learning mealy machines.
 */

public class ObservationTable {
    private static final String SEPERATOR = ",";
    private static final String LAMBDA = "λ";

    private LinkedHashSet<String> S;
    private LinkedHashSet<String> E;
    private String[] alphabet;

    private Map<String, ArrayList<String>> table;

    public ObservationTable(String[] alphabet) {
        this.S = new LinkedHashSet<>();
        this.E = new LinkedHashSet<>();
        this.alphabet = alphabet;
        table = new HashMap<>();
        this.E.add(LAMBDA);
        // this.S.add(LAMBDA);
        //
        this.addToS(LAMBDA);
        // fillMissing();
    }

    public String join(String... symbols) {
        // Join the symbols with the SEPERATOR, but removing any empty strings
        return String.join(SEPERATOR,
                Arrays.stream(symbols).filter(
                        item -> !item.isEmpty() && !item.equals(LAMBDA)).collect(Collectors.toList()));
        // return String.join(SEPERATOR, symbols);
    }

    public String[] toArrayTrace(String trace) {
        // Join the symbols with the SEPERATOR, but removing any empty strings
        return Arrays.stream(trace.split(SEPERATOR))
                .filter(item -> !item.isEmpty() && !item.equals(LAMBDA)).toArray(String[]::new);
    }

    public String getResult(String trace) {
        String res = LearningTracker.runNextTrace(toArrayTrace(trace));
        // System.out.printf("Output for trace %s is %s\n", trace, res);
        return res;
    }

    public void addToS(String s) {
        if (S.add(s)) {
            addRow(s);
            for (String symbol : alphabet) {
                addRow(join(s, symbol));
            }
        }
    }

    public void addToE(String e) {
        if (E.add(e)) {
            for(Entry<String, ArrayList<String>> entry : table.entrySet()) {
                String joined = join(entry.getKey(), e);
                entry.getValue().add(getResult(joined));
            }
        }
    }

    public void addRow(String base) {
        if (table.containsKey(base)) {
            return;
        } else {
            ArrayList<String> row = new ArrayList<>();
            for (String e : E) {
                // System.out.printf("base: %s, e: %s, joined: %s\n", base, e, join(base, e));
                row.add(getResult(join(base, e)));
            }
            table.put(base, row);
        }
    }

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
        String empty = "▪";
        for (int i = 0; i < minSizes.length; i++) {
            for (int j = 0; j < minSizes[i]; j++) {
                empty += "─";
            }
            empty += "▪";
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
    };

}
