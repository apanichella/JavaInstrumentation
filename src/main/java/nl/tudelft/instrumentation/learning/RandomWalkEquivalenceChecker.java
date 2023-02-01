package nl.tudelft.instrumentation.learning;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
 * 
 */

public class RandomWalkEquivalenceChecker extends EquivalenceChecker {

    private int numberOfSamples;
    private int depth;

    private Random rng;

    public RandomWalkEquivalenceChecker(SystemUnderLearn sul, String[] alphabet, int depth, int numberOfSamples) {
        super(sul, alphabet);
        this.depth = depth;
        this.numberOfSamples = numberOfSamples;
        this.rng = new Random();
    }

    @Override
    public Optional<String[]> verify(MealyMachine hyp) {
        String[] inputs = new String[depth];
        for (int i = 0; i < numberOfSamples; i++) {
            for (int j = 0; j < depth; j++) {
                inputs[i] = alphabet[rng.nextInt(alphabet.length)];
            }
            String[] modelOutput = hyp.getOutput(inputs);
            String[] realOutput = sul.getOutput(inputs);
            for (int j = 0; j < depth; j++) {
                // If the model output does not match the real output
                if (!modelOutput[j].equals(realOutput[j])) {
                    // Construct counterexample
                    return Optional.of(Arrays.asList(inputs).subList(0, j + 1).toArray(String[]::new));
                }
            }
        }
        // no counterexample found
        return Optional.empty();
    }

}
