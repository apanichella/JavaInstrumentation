package nl.tudelft.instrumentation.learning;

/**
 * @author Bram Verboom
 */

public abstract class SystemUnderLearn {

    public abstract String[] getOutput(String[] trace);

    public String getLastOutput(String[] trace) {
        String[] out = getOutput(trace);
        if(out.length > 0) {
            return out[out.length-1];
        } else {
            return "";
        }
    }

    /**
     * Get the output of running the given trace.
     * @param trace the trace to run on the system
     * @return the output of the system, for each symbol in the trace, an ouput is included.
     */
    public String[] getOutput(Word<String> trace) {
        return getOutput(trace.asList().toArray(new String[0]));
    }

    /**
     * Get the last output of running the given trace.
     * @param trace the trace to run on the system
     * @return the last output of the system
     */
    public String getLastOutput(Word<String> trace) {
        return getLastOutput(trace.asList().toArray(new String[0]));
    }

}

