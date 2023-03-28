package nl.tudelft.instrumentation.learning;

import java.util.Optional;

public class WMethodEquivalenceChecker extends EquivalenceChecker{

    private int w;
    private AccessSequenceGenerator accessSequenceGenerator;
    private DistinguishingSequenceGenerator distinguishingSequenceGenerator;

    public WMethodEquivalenceChecker(SystemUnderLearn sul, String[] inputSymbols, int w, DistinguishingSequenceGenerator dg, AccessSequenceGenerator ag) {
        super(sul, inputSymbols);
        this.w = w;
        this.distinguishingSequenceGenerator= dg;
        this.accessSequenceGenerator= ag;
    }

    @Override
    public Optional<Word<String>> verify(MealyMachine hypothesis) {
        // TODO implement the W-method equivalence checker
        throw new RuntimeException("Unimplemented method 'verify'");
    }

}
