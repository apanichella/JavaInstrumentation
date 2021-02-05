package nl.tudelft.instrumentation.fuzzing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FuzzerTest {

    @Test
    public void testCreatingFuzzerShouldInitializeCurrentTrace() {
        Fuzzer f = new Fuzzer(new String[]{"A", "B", "C", "D"});
        assertEquals(f.getTraceLength() + 1, f.getCurrentTrace().size());
    }

    @Test
    public void testGeneratingRandomTraceShouldGenerateRightTraceFormat() {
        String[] symbols = new String[]{"A", "B", "C", "D"};
        Fuzzer f = new Fuzzer(symbols);
        f.generateRandomTrace(symbols);
        boolean b = f.getCurrentTrace().size() == 11 && f.getCurrentTrace().get(10).equals("R");
        assertEquals(true, b);
    }

    @Test
    public void testFuzzShouldDecrementCurrentTraceLength() {
        Fuzzer f = new Fuzzer(new String[]{"A", "B", "C", "D"});
        int beforeFuzzLength = f.getCurrentTrace().size();
        f.fuzz();
        assertEquals(beforeFuzzLength-1, f.getCurrentTrace().size());
    }

    @Test
    public void testFuzzingFromWholeTraceShouldReturnNull() {
        Fuzzer f = new Fuzzer(new String[]{"A", "B", "C", "D"});
        String nextInput = "";
        for (int i = 0; i < f.getTraceLength()+2; i++) {
            nextInput = f.fuzz();
        }
        assertNull(nextInput);
    }
    @Test
    public void testFuzzShouldChangeCurrentTraceSymbol() {
        Fuzzer f = new Fuzzer(new String[]{"A", "B", "C", "D"});
        String symbol = f.fuzz();
        assertEquals(symbol, f.getCurrentTraceSymbol());
    }
}
