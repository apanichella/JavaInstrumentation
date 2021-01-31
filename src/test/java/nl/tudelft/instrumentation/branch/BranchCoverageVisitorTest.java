package nl.tudelft.instrumentation.branch;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import nl.tudelft.instrumentation.line.LineCoverageVisitor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BranchCoverageVisitorTest {

    public CompilationUnit instrument(String code){
        // read string as Java code
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new StringReader(code));
        CompilationUnit unit = results.getResult().get();

        // print code
        System.out.println(unit.toString());

        // instrumentation
        unit.accept(new BranchCoverageVisitor("Example.java"), null);
        return unit;
    }

    @Test
    public void testSimpleIf(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public String triangle(int a, int b, int c) {\n")
                .append("        if (a <= 0 || b <= 0 || c <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(2, count);
    }

    @Test
    public void testSimpleIfElse(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public String triangle(int a, int b, int c) {\n")
                .append("        if (a <= 0 || b <= 0 || c <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("        else \n" )
                .append("            return \"VALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(2, count);
    }

    @Test
    public void testBlockIfElse(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public String triangle(int a, int b, int c) {\n")
                .append("        if (a <= 0 || b <= 0 || c <= 0) {\n")
                .append("            return \"INVALID\";\n" )
                .append("        } else { \n" )
                .append("            return \"VALID\";\n" )
                .append("        }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(2, count);
    }

    @Test
    public void testSimpleFor(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public double[] array(int size, double element){\n")
                .append("        double[] array = new double[size];\n")
                .append("        for(int i=0; i<size; i++)\n")
                .append("            array[i] = element;\n")
                .append("        \n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
       assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
       assertEquals(2, count);
    }

    @Test
    public void testNestedFor(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public double[] array(int size, double element){\n")
                .append("        double[] array = new double[size];\n")
                .append("        if (element > 0.0) \n")
                .append("            for(int i=0; i<size; i++)\n")
                .append("                array[i] = element;\n")
                .append("        \n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(4, count);
    }

    @Test
    public void testSimpleWhile(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public double[] array(int size, double element){\n")
                .append("        double[] array = new double[size];\n")
                .append("        int i=0;\n")
                .append("        while (i<size) {\n")
                .append("            array[i] = element;\n")
                .append("            i++;\n")
                .append("        } \n")
                .append("        \n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(2, count);
    }

    @Test
    public void testNestedWhile(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public double[] array(int size, double element){\n")
                .append("        double[] array = new double[size];\n")
                .append("        if (size > 0) {\n")
                .append("           int i=0;\n")
                .append("           while (i<size) {\n")
                .append("               array[i] = element;\n")
                .append("               i++;\n")
                .append("           } \n")
                .append("        } \n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(4, count);
    }

    @Test
    public void testSimpleForEach(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public example array(){\n")
                .append("        // create an array\n")
                .append("        int[] numbers = {3, 9, 5, -5}; \n")
                .append("        \n")
                .append("        // for each loop \n")
                .append("        for (int number: numbers) {\n")
                .append("            System.out.println(number);\n")
                .append("        } \n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(2, count);
    }

    @Test
    public void testSwitch(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Example {\n")
                .append("    public example size(int day){\n")
                .append("    switch (day) {\n")
                .append("    case 1:\n")
                .append("        System.out.println(\"Monday\");\n")
                .append("        break;\n")
                .append("    case 2:\n")
                .append("        System.out.println(\"Tuesday\");\n")
                .append("        break;\n")
                .append("    case 3:\n")
                .append("        System.out.println(\"Wednesday\");\n")
                .append("        break;\n")
                .append("    case 4:\n")
                .append("        System.out.println(\"Thursday\");\n")
                .append("        break;\n")
                .append("    case 5:\n")
                .append("        System.out.println(\"Friday\");\n")
                .append("        break;\n")
                .append("    case 6:\n")
                .append("        System.out.println(\"Saturday\");\n")
                .append("        break;\n")
                .append("    case 7:\n")
                .append("        System.out.println(\"Sunday\");\n")
                .append("        break;\n")
                .append("    }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(7, count);
    }

    @Test
    public void testNestedSwitch(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Example {\n")
                .append("    public example size(int day){\n")
                .append("    if (day > 0) \n")
                .append("    switch (day) {\n")
                .append("    case 1:\n")
                .append("        System.out.println(\"Monday\");\n")
                .append("        break;\n")
                .append("    case 2:\n")
                .append("        System.out.println(\"Tuesday\");\n")
                .append("        break;\n")
                .append("    case 3:\n")
                .append("        System.out.println(\"Wednesday\");\n")
                .append("        break;\n")
                .append("    case 4:\n")
                .append("        System.out.println(\"Thursday\");\n")
                .append("        break;\n")
                .append("    case 5:\n")
                .append("        System.out.println(\"Friday\");\n")
                .append("        break;\n")
                .append("    case 6:\n")
                .append("        System.out.println(\"Saturday\");\n")
                .append("        break;\n")
                .append("    case 7:\n")
                .append("        System.out.println(\"Sunday\");\n")
                .append("        break;\n")
                .append("    }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(9, count);
    }

    @Test
    public void testLambdaIf(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Example {\n")
                .append("private boolean field;\n")
                .append("\n")
                .append("public void example(int a1542365894, int a) {\n")
                .append("        float f = (a1542365894 > 0) ? (float) 1.0 : (float) 0.0; \n")
                .append("        String value = (a < 0) ? \"INVALID\" : \"VALID\";\n")
                .append("        Integer b = (a1542365894 > 0) ? new Integer(1) : new Integer(0); \n")
                .append("        int i = 0;\n")
                .append("        i += (a > 0) ? 1 : 0; \n")
                .append("        field = (a > 0) ? true : false; \n")
                .append("    }\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.branch.BranchCoverageTracker.updateCoverage"));
        assertEquals(10, count);
    }
}