import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProblemEven {
    static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    private String[] inputs = { "0", "1"};

    public boolean cf = true;
    public boolean even0s = true;
    public boolean even1s = true;

    public void calculateOutput(String input) {
        cf = true;
        if(input.equals(inputs[0])) {
            cf=false;
            even0s = !even0s;
        }
        if(input.equals(inputs[1])) {
            cf=false;
            even1s = !even1s;
        }
        if(even0s && even1s) {
            System.out.println("EVEN");
        } else {
            System.out.println("ODD");
        }

        if (cf) {
            throw new IllegalArgumentException("Current state has no transition for this input!");
        }
    }

    public static void main(String[] args) throws Exception {
        // init system and input reader
        ProblemEven eca = new ProblemEven();

        // main i/o-loop
        while (true) {
            // read input
            String input = stdin.readLine();

            try {
                // operate eca engine output =
                eca.calculateOutput(input);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid input: " + e.getMessage());
            }
        }
    }
}
