package nl.tudelft.instrumentation.patching;

import java.util.concurrent.Callable;

public interface CallableTraceRunner<T> extends Callable<T> {
    void setSequence(String[] ll);
}
