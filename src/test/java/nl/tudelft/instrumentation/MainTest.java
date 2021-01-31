package nl.tudelft.instrumentation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    public void testMain_BranchCoverage() throws FileNotFoundException {
        String[] args = {
            "-f", System.getProperty("user.dir")+"/src/main/resources/Example.java",
                "-t", "branch"
        };
        Main.main(args);
        assertEquals("Example.java", Main.file.getName());
        assertTrue(Main.file.exists());

        int count = StringUtils.countMatches(Main.unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertEquals(10, count);
    }

    @Test
    public void testMain_LineCoverage() throws FileNotFoundException {
        String[] args = {
                "-f", System.getProperty("user.dir")+"/src/main/resources/Example.java",
                "-t", "line"
        };
        Main.main(args);
        assertEquals("Example.java", Main.file.getName());
        assertTrue(Main.file.exists());

        int count = StringUtils.countMatches(Main.unit.toString(), "nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage");
        assertEquals(9, count);
    }

    @Test
    public void testMain_missingParameters() {
        String[] args = {
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Main.main(args);
        });
    }

    @Test
    public void testMain_wrongParameters() {
        String[] args = {
                "-f", System.getProperty("user.dir")+"/src/main/resources/Example.java",
                "-t", "other"
        };
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Main.main(args);
        });
    }

    @Test
    public void testMain_wrongFile() throws FileNotFoundException {
        String[] args = {
                "-f", System.getProperty("user.dir")+"/src/main/resources/Example2.java",
                "-t", "line"
        };
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            Main.main(args);
        });
    }
}