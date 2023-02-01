package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A
 */

public class ObservationTable {

    private Set<String> s;
    private Set<String> e;
    private String[] alphabet;

    private Map<String, ArrayList<String>> table;

    public ObservationTable(String[] alphabet) {
        this.s = new HashSet<>();
        this.s.add("");
        this.e = new HashSet<>();
        this.e.add("");
        this.alphabet = alphabet;
        table = new HashMap<>();
    }

    public void fillMissing() {

    }

}
