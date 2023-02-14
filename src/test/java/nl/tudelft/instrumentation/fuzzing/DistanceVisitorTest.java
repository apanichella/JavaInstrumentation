package nl.tudelft.instrumentation.fuzzing;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import nl.tudelft.instrumentation.branch.BranchCoverageVisitor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistanceVisitorTest {

    public CompilationUnit instrument(String code){
        // read string as Java code
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new StringReader(code));
        CompilationUnit unit = results.getResult().get();

        // print code
        System.out.println(unit.toString());

        // instrumentation
        unit.accept(new DistanceVisitor("Example.java"), null);
        return unit;
    }

    @Test
    public void testSimpleIfShouldCreateOneBinaryExpression(){
        String binExpr = "DistanceTracker.binaryExpr";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), binExpr);
        assertTrue(unit.toString().contains(binExpr));
        assertEquals(1, count);
    }

    @Test
    public void testSimpleIfShouldCreateOneMyIfMethodCall(){
        String myIfCall = "DistanceTracker.myIf";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), myIfCall);
        assertTrue(unit.toString().contains(myIfCall));
        assertEquals(1, count);
    }

    @Test
    public void testSimpleIfShouldCreateTwoMyVars(){
        String myVar = "DistanceTracker.MyVar";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), myVar);
        assertTrue(unit.toString().contains(myVar));
        assertEquals(2, count);
    }

    @Test
    public void testNestedIfShouldCreateTwoMyIfCalls(){
        String myIf = "DistanceTracker.myIf";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) {\n")
                .append("           if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("        }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), myIf);
        assertTrue(unit.toString().contains(myIf));
        assertEquals(2, count);
    }

    @Test
    public void testSimpleIfShouldCreateOneUnaryExpression(){
        String unExpr = "DistanceTracker.unaryExpr";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (!a)\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), unExpr);
        assertTrue(unit.toString().contains(unExpr));
        assertEquals(1, count);
    }

    @Test
    public void testEqualsShouldConvertToMyVarEquals(){
        String myVarEquals = "DistanceTracker.equals";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), myVarEquals);
        assertTrue(unit.toString().contains(myVarEquals));
        assertEquals(1, count);
    }

    @Test
    public void testInstrumentationShouldAddImport(){
        String imprt = "import nl.tudelft.instrumentation.fuzzing.*;";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), imprt);
        assertTrue(unit.toString().contains(imprt));
        assertEquals(1, count);
    }

    @Test
    public void testManyComparisonsInIfCreatesManyBinaryExpressions(){
        String binExpr = "DistanceTracker.binaryExpr";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b) && a.equals(b) && a.equals(b) && a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), binExpr);
        assertTrue(unit.toString().contains(binExpr));
        assertEquals(3, count);
    }

    @Test
    public void testInstrumentationShouldAddImplementToClass(){
        String implementsExpr = "implements CallableTraceRunner<Void>";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b) && a.equals(b) && a.equals(b) && a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), implementsExpr);
        assertTrue(unit.toString().contains(implementsExpr));
        assertEquals(1, count);
    }

    @Test
    public void testInstrumentationShouldAddImportForCallable(){
        String importCallable = "import nl.tudelft.instrumentation.runner.CallableTraceRunner;";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b) && a.equals(b) && a.equals(b) && a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), importCallable);
        assertTrue(unit.toString().contains(importCallable));
        assertEquals(1, count);
    }

    @Test
    public void testInstrumentationShouldCreateCallMethod(){
        String callMethod = "public Void call() {\n" +
                "        Test cp = new Test();\n" +
                "        for (String s : sequence) {\n" +
                "            try {\n" +
                "                cp.calculateOutput(s);\n" +
                "            } catch (Exception e) {\n" +
                "                FuzzingLab.output(\"Invalid input: \" + e.getMessage());\n" +
                "            }\n" +
                "        }\n" +
                "        return null;\n" +
                "    }";

        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b) && a.equals(b) && a.equals(b) && a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), callMethod);
        assertTrue(unit.toString().contains(callMethod));
        assertEquals(1, count);
    }

    @Test
    public void testInstrumentationShouldCreateSetSequenceMethod(){
        String setSequenceMethod = "public void setSequence(String[] trace) {\n" +
                "        sequence = trace;\n" +
                "    }";

        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b) && a.equals(b) && a.equals(b) && a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), setSequenceMethod);
        assertTrue(unit.toString().contains(setSequenceMethod));
        assertEquals(1, count);
    }

    @Test
    public void testInstrumentationShouldCreateSequenceAttribute(){
        String sequenceAttribute = "public String[] sequence;";

        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a.equals(b) && a.equals(b) && a.equals(b) && a.equals(b))\n")
                .append("           return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), sequenceAttribute);
        assertTrue(unit.toString().contains(sequenceAttribute));
        assertEquals(1, count);
    }

    @Test
    public void testInstrumentationShouldCreateRunCall(){
        String runCall = "DistanceTracker.run(eca.inputs, eca);";

        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        Test eca = new Test();\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), runCall);
        assertTrue(unit.toString().contains(runCall));
        assertEquals(1, count);
    }

}
