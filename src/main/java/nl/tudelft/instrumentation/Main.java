package nl.tudelft.instrumentation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new FileReader(file));
        CompilationUnit unit = results.getResult().get();
        unit.accept(new CoverageVisitor(file.getAbsolutePath()), null);
        System.out.println(unit.toString());
    }
}
