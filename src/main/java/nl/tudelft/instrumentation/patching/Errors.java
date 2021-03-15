package nl.tudelft.instrumentation.patching;

public class Errors {
    public static void __VERIFIER_error(int value){
        PatchingLab.output("Error "+ value);
    }
}