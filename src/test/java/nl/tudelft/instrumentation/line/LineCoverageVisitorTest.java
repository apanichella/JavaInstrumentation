package nl.tudelft.instrumentation.line;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import nl.tudelft.instrumentation.line.LineCoverageVisitor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class LineCoverageVisitorTest {

    @Test
    public void testIfInstrumentation(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class Triangle {\n")
                .append("    public enum Type { INVALID, EQUILATERAL,\n")
                .append("        ISOSCELES, SCALENE };\n")
                .append("\n")
                .append("    public Type triangle(int a, int b, int c) {\n")
                .append("        if (a <= 0 || b <= 0 || c <= 0) \n")
                .append("            return Type.INVALID;\n" )
                .append("        \n")
                .append("        if (! (a + b > c && a + c >= b && b + c > a)) {\n")
                .append("            return Type.INVALID;\n")
                .append("        }\n")
                .append("        if (a == b && b == c) {\n")
                .append("            return Type.EQUILATERAL;\n")
                .append("        }\n")
                .append("        if (a == b || b == c || a == c) {\n")
                .append("            return Type.ISOSCELES;\n")
                .append("        }\n")
                .append("        return Type.SCALENE;\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();

        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new StringReader(code));
        CompilationUnit unit = results.getResult().get();
        System.out.println(unit.toString());

        unit.accept(new LineCoverageVisitor("Triangle.java"), null);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage"));
        assertEquals(9, count);
    }

    @Test
    public void testForCycle(){
        StringBuilder builder = new StringBuilder();
        builder.append("public class ForExample {\n")
                .append("    public double[] array(int size, double element){\n")
                .append("        if (size <= 0)\n")
                .append("            throw new IllegalArgumentException();\n")
                .append("        else ")
                .append("            System.out.println(\"OK\"); \n")
                .append("\n")
                .append("        double[] array = new double[size];\n")
                .append("\n")
                .append("        for(int i=0; i<size; i++)\n")
                .append("            array[i] = element;\n")
                .append("        \n")
                .append("\n")
                .append("        return array;\n")
                .append("    }\n")
                .append("}");

        String code = builder.toString();

        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new StringReader(code));
        CompilationUnit unit = results.getResult().get();

        unit.accept(new LineCoverageVisitor("ForExample.java"), null);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage"));
        assertEquals(7, count);
    }

    @Test
    public void testIf2(){
        // a1542365894 += (a1542365894 + 20) > a1542365894 ? 1 : 0;
        StringBuilder builder = new StringBuilder();
        builder.append("public class ForExample {\n")
                .append("    public void example(int a1542365894, int iterations){\n")
                .append("        for (int i=0; i<iterations; i++){ \n")
                .append("            a1542365894 += (a1542365894 + 20) > a1542365894 ? 1 : 0; \n")
                .append("        } ")
                .append("    }\n")
                .append("}");
        String code = builder.toString();

        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new StringReader(code));
        CompilationUnit unit = results.getResult().get();

        unit.accept(new LineCoverageVisitor("ForExample.java"), null);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), "nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage");
        assertTrue(unit.toString().contains("nl.tudelft.instrumentation.line.LineCoverageTracker.updateCoverage"));
        //assertEquals(7, count);
    }
}