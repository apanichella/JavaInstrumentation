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
        String binExpr = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.binaryExpr";
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
        String myIfCall = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.myIf";
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
        String myVar = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.MyVar";
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
        String myIf = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.myIf";
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
        String unExpr = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.unaryExpr";
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
        String myVarEquals = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.equals";
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
        String binExpr = "nl.tudelft.instrumentation.fuzzing.DistanceTracker.binaryExpr";
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

}
