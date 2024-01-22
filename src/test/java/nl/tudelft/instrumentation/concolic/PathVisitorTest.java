package nl.tudelft.instrumentation.concolic;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathVisitorTest {

    public CompilationUnit instrument(String code){
        // read string as Java code
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new StringReader(code));
        CompilationUnit unit = results.getResult().get();

        // print code
        System.out.println(unit.toString());

        // instrumentation
        unit.accept(new PathVisitor("Example.java"), null);
        return unit;
    }

    @Test
    public void testSimpleIntGlobalShouldCreateMyVar(){
        String createMyVar = "MyVar my_a = PathTracker.myVar(55, \"my_a\");";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    int a = 55;\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), createMyVar);
        assertTrue(unit.toString().contains(createMyVar));
        assertEquals(1, count);
    }

    @Test
    public void testSimpleBoolGlobalShouldCreateMyVar(){
        String createMyVar = "MyVar my_bool = PathTracker.myVar(true, \"my_bool\");";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    boolean bool = true;\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), createMyVar);
        assertTrue(unit.toString().contains(createMyVar));
        assertEquals(1, count);
    }

    @Test
    public void testSimpleArrayGlobalShouldCreateMyVar(){
        String createMyVar = "MyVar[] my_arr = { PathTracker.myVar(1, \"my_arr0\"), PathTracker.myVar(2, \"my_arr1\"), PathTracker.myVar(3, \"my_arr2\") };";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    int[] arr = {1, 2 ,3};\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), createMyVar);
        assertTrue(unit.toString().contains(createMyVar));
        assertEquals(1, count);
    }

    @Test
    public void testSimpleStringGlobalShouldCreateMyVar(){
        String createMyVar = "MyVar my_s = PathTracker.myVar(\"test\", \"my_s\");";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    String s = \"test\";\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), createMyVar);
        assertTrue(unit.toString().contains(createMyVar));
        assertEquals(1, count);
    }

    @Test
    public void testArrayAssignmentToVarGlobalShouldCreateMyVar(){
        String createMyVar = "MyVar[] my_arrB = PathTracker.myVar(my_arrA, \"my_arrB\");";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    int[] arrA = {1, 2 ,3};\n")
                .append("    int[] arrB = arrA;\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) \n")
                .append("            return \"INVALID\";\n" )
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), createMyVar);
        assertTrue(unit.toString().contains(createMyVar));
        assertEquals(1, count);
    }

    @Test
    public void testSimpleIfShouldCreateOneBinaryExpression(){
        String binExpr = "PathTracker.binaryExpr";
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
        String myIfCall = "PathTracker.myIf";
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
    public void testSimpleIfShouldCreateTempVar(){
        String myVar = "PathTracker.tempVar";
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
        assertEquals(1, count);
    }

    @Test
    public void testNestedIfShouldCreateTwoMyIfCalls(){
        String myIf = "PathTracker.myIf";
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
        String unExpr = "PathTracker.unaryExpr";
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
        String myVarEquals = "PathTracker.equals";
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
        String imprt = "import nl.tudelft.instrumentation.concolic.*";
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
        String binExpr = "PathTracker.binaryExpr";
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
    public void testIncrementShouldCreateIncrementCall(){
        String increment= "PathTracker.increment";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a++ <= 0) {\n")
                .append("           return \"INVALID\";\n" )
                .append("       }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), increment);
        assertTrue(unit.toString().contains(increment));
        assertEquals(1, count);
    }

    @Test
    public void testIncrementShouldCreateIncrementCallForSingleStatement(){
        String increment= "PathTracker.increment";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        a++;\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), increment);
        assertTrue(unit.toString().contains(increment));
        assertEquals(1, count);
    }

    @Test
    public void testConditionalShoudlCreateMyAssignAndConditionalCall(){
        String conditional = "PathTracker.conditional";
        String myAssign = "PathTracker.myAssign";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a <= 0) {\n")
                .append("           a += (a+ 20) > a ? 1 : 0;")
                .append("           return \"INVALID\";\n" )
                .append("       }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println("HERE");
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), conditional);
        int count2 = StringUtils.countMatches(unit.toString(), myAssign);
        assertTrue(unit.toString().contains(conditional) && unit.toString().contains(myAssign));
        assertEquals(2, count + count2);
    }

    @Test
    public void testArrayAccessShouldCreateArrayIndCall(){
        String arrayInd = "PathTracker.arrayInd";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a[0] <= 0) {\n")
                .append("           return \"INVALID\";\n" )
                .append("       }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), arrayInd);
        assertTrue(unit.toString().contains(arrayInd));
        assertEquals(1, count);
    }

    @Test
    public void testDecrementShouldCreateIncrementCall(){
        String decrement = "PathTracker.increment";
        StringBuilder builder = new StringBuilder();
        builder.append("public class Test {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        if (a-- <= 0) {\n")
                .append("           return \"INVALID\";\n" )
                .append("       }\n")
                .append("    }\n")
                .append("\n")
                .append("}");

        String code = builder.toString();
        CompilationUnit unit = instrument(code);
        System.out.println(unit.toString());

        int count = StringUtils.countMatches(unit.toString(), decrement);
        assertTrue(unit.toString().contains(decrement));
        assertEquals(1, count);
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
                "                MyVar my_s = PathTracker.myInputVar(s, \"input\");\n" +
                "                cp.calculateOutput(s, my_s);\n" +
                "            } catch (IllegalArgumentException | IllegalStateException e) {\n" +
                "                ConcolicExecutionLab.output(\"Invalid input: \" + e.getMessage());\n" +
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
        String runCall = "PathTracker.run(eca.inputs, eca);";

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
