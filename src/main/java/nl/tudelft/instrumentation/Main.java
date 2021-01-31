package nl.tudelft.instrumentation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.GenericVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    protected static File file;
    protected static CompilationUnit unit;

    public static void main(String[] args) throws FileNotFoundException {
        // parse command line
        CommandLineParser command = new CommandLineParser();
        command.parseCommandLine(args);
        GenericVisitor visitor = command.getVisitor();
        file = command.getJavaFile();

        // parse the Java file
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> results = parser.parse(new FileReader(file));
        unit = results.getResult().get();

        // Instrumentation
        unit.accept(visitor, null);
        System.out.println(unit.toString());
    }
}
