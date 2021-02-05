package nl.tudelft.instrumentation.symbolic;
import java.util.*;

/**
 * A fuzzer that randomly generates input. This fuzzer can be made smarter
 * by adapting its search strategy.
 */
public class Fuzzer {

    private List<String> inputs;
    private Random random;
    public Queue<String> inputsQ;
    public Set<String> triedInputs;
    public int numBranchesFound = 0;
    //public boolean visitingLayer = false;

    public Fuzzer(String[] inputs) {
        this.inputs = new ArrayList<>(Arrays.asList(inputs));
        this.random = new Random();
        this.inputsQ = new LinkedList<>();
        this.triedInputs = new HashSet<>();
    }

    /**
     * Generate a single input from the list of possible inputs.
     * @return an input
     */
    public String fuzz() {
        if (!inputsQ.isEmpty()) {
            String nextInput = inputsQ.poll();
            updateInputList(nextInput);
            return nextInput;
        }
        else {
            String input = this.inputs.get(this.random.nextInt(this.inputs.size()));
            String nextInput = mutate(input);
            System.out.println("Actual input: " + input);
            System.out.println("Mutated input: "  + nextInput);
            return nextInput;
        }
    }

    /**
     * Update the list of inputs to fuzz from.
     * @param input new input
     */
    public void updateInputList(String input) {
//        this.inputs.remove(this.random.nextInt(this.inputs.size()));
        this.inputs.add(input);
    }

    /**
     * Add an input to the queue when a new branch has been found and
     * the path contraints was solvable. This makes sure that we are
     * visiting the branches in a breadth first search manner.
     * @param input
     */
    public void addToQueue(String input) {
        this.inputsQ.add(input);
    }

    public String mutate(String nextInput){
        char c = (char) nextInput.charAt(1);
        String mutatedInput = "";
        if (this.random.nextDouble() > 0.50) {
            int c_num = c;
            int offset = this.random.nextInt();
            if (this.random.nextBoolean()) {
                offset = -offset;
            }
            c_num += offset;
            mutatedInput += nextInput.charAt(0);
            mutatedInput += (char) c_num;
        } else {
          mutatedInput = nextInput;
        }

        if (this.random.nextDouble() > 0.80) {
            mutatedInput = mutatedInput.toLowerCase();
        }

        return mutatedInput;
    }
}