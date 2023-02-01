package nl.tudelft.instrumentation.learning;

import java.util.Optional;

/**
 * @author Bram Verboom
 */

public abstract class EquivalenceChecker {

    SystemUnderLearn sul;
    String[] alphabet;

    public EquivalenceChecker(SystemUnderLearn sul, String[] alphabet) {
        this.sul = sul;
        this.alphabet = alphabet;

    }

    /**
     * Method for verifying a hypothesis against the SUL.
     * @param hypothesis The hypothesis to verify
     * @return A counterexample or an empty option if no counterexample was found
     */
    public abstract Optional<String[]> verify(MealyMachine hypothesis);

}
