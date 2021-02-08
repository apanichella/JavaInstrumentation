# JavaInstrumentation
This repository contains an implementation of a Java instrumentation tool to do source-code level instrumentation on the RERS 2020 problems. The instrumentation is done with the help of the JavaParser (https://github.com/javaparser/javaparser).

Byte-code level instrumentation would be more efficient as it does not require recompiling the instrumented code. We opted for source-code level instrumentation since it is easier to understand. Notice that this project is made for educational purposes and is mostly used for courses taught at TU Delft.

Instrumentation is implemented as follows:

* **Line Coverage** is implemented by the following classes:
  * `LineCoverageVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for coverage analysis
  * `LineCoverageTracker.java` updates line coverage data at runtime and generates the coverage report (`coverage.json`)
* **Branch Coverage** is implemented by the following classes:
  * `BranchCoverageVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for coverage analysis
  * `BranchCoverageTracker.java` updates branch coverage data at runtime and generates the coverage report (`branch-coverage.json`)
* **Branch Distance Computation** is implemented by the following classes:
  * `DistanceVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for branch distance computation.
  * `DistanceTracker.java` wraps each variable/expression in an object. These objects are then used for the branch distance computation.
* **Symbolic Execution** is implemented by the following classes:
  * `PathVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for symbolic execution.
  * `PathTracker.java` converts the variables and expressions in the Java file to Z3 variables and expressions respectively. These will be used to do symbolic execution with Z3.
* **Code Patching Using Genetic Algorithms** is implemented by the following classes:
  * `OperatorVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for code patching.
  * `OperatorTracker.java` converts an operator to another operator in order to introduce faults in the original Java file.
* `Main.java` is the main file and can be used to generate the instrumented Java file
* `CommandLineParser.java` is used to parse the arguments that were given to this tool in order to generate the corresponding instrumentation.

**NOTE:** This tool instruments only one Java file at the time.

# Build and run the tool
To build the project, make sure you have navigated to the root of this project and run the following Maven command:

`mvn clean package`

To instrument a given Java file, use the following command:

`java -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=distance --file=*JavaFile* > *TargetFile`

Where `*JavaFile*` is the path to the Java file to instrument, `*TargetFile*` is the file (file name and path) where you want to save the instrumented code.
Note that the flags `--file` and `--type` are required for instrumenting a Java file. 
The flag `--file` is used to specify the path to the Java file and the `--type` flag is used to specify what kind of instrumentation should be done on the Java file.

# Compiling and Running the Instrumented Java File
To compile the instrumented Java file, you would need to run the following command:

`javac -cp target/aistr.jar:.  *InstrumentedJavaFile*`

Where `*InstrumentedJavaFile*` is path to the instrumented Java file. When compiling the instrumented Java file for symbolic execution, a different command is needed:

`javac -cp target/aistr.jar:lib/com.microsoft.z3.jar:.  *InstrumentedJavaFile*`

Notice that we have added an extra Jar file to the command as we need Z3 to do the symbolic execution. 

To run the instrumented Java file, you would need to run the following command:

`java -cp target/aistr.jar:.  *InstrumentedJavaFile*`

Again, if when runnning the instrumented Java file for symbolic execution, we would need to add the Z3 Jar file to the command:

`java -cp target/aistr:lib/com.microsoft.z3.jar:.  *InstrumentedJavaFile*`

# Setting

The code has been tested with the following configuration:

* Maven 3.5.4
* Java 8
* JavaParser 3.18.0