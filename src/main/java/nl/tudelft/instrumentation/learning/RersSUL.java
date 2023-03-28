package nl.tudelft.instrumentation.learning;

/**
 * @author Bram Verboom
 */

public class RersSUL extends SystemUnderLearn {

    @Override
    public String[] getOutput(String[] trace) {
        return LearningTracker.runNextTrace(trace);
    }

}
