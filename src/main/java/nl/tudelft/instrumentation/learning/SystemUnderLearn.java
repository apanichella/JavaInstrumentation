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

}

