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

    public abstract Optional<String[]> verify(MealyMachine hyp);

}
