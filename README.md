# JavaInstrumentation
This repository contains an implementation of a small Java instrumentation tool to generate the line-coverage report. The tool performs source-code level instrumentation using JavaParser (https://github.com/javaparser/javaparser).

Byte-code level instrumentation would be more efficient as it does not require to recompile the instrumented code. I opted for source-code level instrumentation since it is easier to understand. Notice that this project is made for educational purposes and mostly for courses taught at TU Delft. 

Instrumentation and coverage tracking is implemented as follows:

* `CoverageVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for coverage analysis
* `LineCoverageTracker.java` updates coverage data at runtime and generates the coverage report (`coverage.json`)
* `Main.java` is the main file and can be used to generate the instrumented Java file

Notice that this tool instruments only one Java file at the time and can be used as a starting point to create fuzzers targeting single Java methods/classes.

# Build and run the tool
To build the project, run the following Maven commands:

`mvn clean package`

To instrument a given Java file, use the following command:
`java -cp target/JavaInstrumentation-1.0-SNAPSHOT-jar-with-dependencies.jar  \`
`
-Dcoverage.report.path=coverage.json nl.tudelft.instrumentation.Main /`
`*JavaFile* > *TargetFile* `

where `*JavaFile*` is the Java file to instrument; *TargetFile* is the file (file name and path) where you want to save the instrumented code.

# Setting

The code has been tested with the following configuration:

* Maven 3.5.4
* Java 8
* JavaParser 3.18.0