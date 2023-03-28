package nl.tudelft.instrumentation.learning;

import java.util.Optional;

/**
 * @author Bram Verboom
 */

public abstract class EquivalenceChecker {

    final SystemUnderLearn sul;
    final String[] inputSymbols;

    public EquivalenceChecker(SystemUnderLearn sul, String[] inputSymbols) {
        this.sul = sul;
        this.inputSymbols = inputSymbols;

    }

    /**
     * Method for verifying a hypothesis against the SUL.
     * @param hypothesis The hypothesis to verify
     * @return A counterexample or an empty option if no counterexample was found
     */
    public abstract Optional<Word<String>> verify(MealyMachine hypothesis);

}
