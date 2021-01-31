package nl.tudelft.instrumentation;

public class Example {
    private boolean field;

    public void example(int a1542365894, int a) {
        float f = (a1542365894 > 0) ? (float) 1.0 : (float) 0.0;
        String value = (a < 0) ? "INVALID" : "VALID";
        Integer b = (a1542365894 > 0) ? new Integer(1) : new Integer(0);
        int i = 0;
        i += (a > 0) ? 1 : 0;
        field = (a > 0) ? true : false;
    }

    public static void main(String[] args) {
        Example example = new Example();
        example.example(10, 1);
        example.example(-1, -1);
    }
}



