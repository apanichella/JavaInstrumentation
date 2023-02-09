import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProblemPin {
    static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    private String[] inputs = { "T", "Z", "O", "N", "W", "E", "D", "R", "ENTER", "C"};

    public boolean cf = true;
    public int digit = 0;
    public boolean open = false;

    public void errorCheck() {
        if (open) {
            cf = false;
            Errors.__VERIFIER_error(0);
        }
    }

    public void calculateOutput(String input) {
        cf = true;
        if(cf && digit == 0 && input.equals("Z")) {
            digit += 1;
            cf = false;
            System.out.println("N");
        }
        if(cf && digit == 1 && input.equals("E")) {
            digit += 1;
            cf = false;
            System.out.println("N");
        }
        if(cf && digit == 2 && input.equals("R")) {
            digit += 1;
            cf = false;
            System.out.println("N");
        }
        if(cf && digit == 3 && input.equals("O")) {
            digit += 1;
            cf = false;
            System.out.println("N");
        }

        if(cf && digit == 4 && input.equals("ENTER")) {
            cf = false;
            open = true;
            System.out.println("Y");
        }

        if(cf && input.equals("ENTER")) {
            cf = false;
            digit = 0;
            System.out.println("X");
        }

        if(cf && (input.equals("T") || input.equals("Z") || input.equals("O") || input.equals("N") || input.equals("W") || input.equals("E") || input.equals("D") || input.equals("R"))) {
            cf = false;
            System.out.println("N");
        }

        errorCheck();
        if (cf) {
            throw new IllegalArgumentException("Current state has no transition for this input!");
        }
    }

    public static void main(String[] args) throws Exception {
        // init system and input reader
        ProblemPin eca = new ProblemPin();

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
