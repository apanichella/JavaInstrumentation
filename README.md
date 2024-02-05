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
* **Concolic Execution** is implemented by the following classes:
  * `PathVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for symbolic execution.
  * `PathTracker.java` converts the variables and expressions in the Java file to Z3 variables and expressions respectively. These will be used to do symbolic execution with Z3.
* **Code Patching Using Genetic Algorithms** is implemented by the following classes:
  * `OperatorVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for code patching.
  * `OperatorTracker.java` converts an operator to another operator in order to introduce faults in the original Java file.
* **Model Inference using L* Algorithm** is implemented by the following classes:
  * `MembershipVisitor.java` visits the AST (obtained with JavaParsers) of the Java file under analysis and injects new statements for automated model inference.
  * `LearningTracker.java` makes minimal changes to a RERS problem file as this lab does not need to do a lot of instrumentation. We just need to change the program flow.
* `Main.java` is the main file and can be used to generate the instrumented Java file
* `CommandLineParser.java` is used to parse the arguments that were given to this tool in order to generate the corresponding instrumentation.

**NOTE:** This tool instruments only one Java file at a time.


# Using Dev Containers To Run The Tool
We have included a `devcontainer.json` file in this repository. This file can be used to run the tool in a Docker container. This is useful if you do not want to install all the dependencies on your machine. To use this file, you need to have the following installed on your machine:
- Docker
- Visual Studio Code
- Remote Extension Pack for Visual Studio Code

Once you have installed these dependencies, you can open this repository in VS Code and start a Dev Container. This will automatically install all the dependencies and build the project. You can then use the pre-configured tasks to instrument a Java file and run it. 


# Manually build and run the tool 
To build the project, make sure you have navigated to the root of this project and run the following Maven command:

`mvn clean package`

To instrument a given Java file, use the following command:

`java -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=*TypeOfInstrumentation* --file=*PathToJavaFile* > *OutputPath*`

Where `*PathToJavaFile*` is the path to the Java file to instrument, `*OutputPath*` is the file (file name and path) where you want to save the instrumented Java file. The `*TypeOfInstrumentation*` is the type of instrumentation that you want to do. You can choose between the following options: `line`, `branch`, `fuzzing`, `concolic`, `patching`, and `learning`.
Note that the flags `--file` and `--type` are required for instrumenting a Java file.

# Examples illustrating how to compile and run the instrumented files
In this section, we present you an example for each lab on how to instrument RERS problem and how to run the instrumented Java file. For the sake of simplicity, we will use the directory structure of this repository to how a RERS problem is instrumented. These examples do assume that the project has already been built using Maven.

## Lab 1 - Fuzzing
Say we want to instrument `Problem1.java` of the RERS 2020 problem. We move the `Problem1.java` to the root directory to get the following structure:
```
JavaInstrumentation
  |- docs
  |- lib
  |- src
  |- .gitignore
  |- libz3java.dylib
  |- pom.xml
  |- Problem1.java
  |- README.md
```

We then create a new directory in which we want to store the instrumented Java file, let's call it `instrumented`. Let's now instrument the file by running the following command at the **root** directory: 

`java -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=fuzzing --file=Problem1.java > instrumented/Problem1.java`

We should now have the following structure:
```
JavaInstrumentation
  |- docs
  |- instrumented
    |- Problem1.java
  |- lib
  |- src
  |- .gitignore
  |- libz3java.dylib
  |- pom.xml
  |- Problem1.java
  |- README.md
```

Let's now compile the instrumented file using the following command:

`javac -cp target/aistr.jar:. instrumented/Problem1.java`

Let's now run the instrumented Java file using the following command:

`java -cp target/aistr.jar:./instrumented:. Problem1 `

Because the file is in a folder, we need to add this to the classpath so that Java knows where to look for the class.

You should see the following output in the terminal:

```
Found a new branch
(((e) == (g)) && (true))
Found a new branch
(((f) == (g)) && (true))
Found a new branch
(((g) == (g)) && (true))
Found a new branch
(((8) == (4)) && (true))
Found a new branch
((true) && ((8) == (5)))
Found a new branch
(((8) == (6)) && (true))
Found a new branch
(((8) == (7)) && (true))
Found a new branch
(((8) == (8)) && (true))
.
.
.
Woohoo, looping!
```
### Compiling and running Reachability Problems
If you are compiling and running one of the Reachability Problems (Problem 11 - 19), make sure you also compile the Error class **together** with the instrumented file. We have included the `Errors.java` file for you in the root of the repository.

For compilation, you would need the following command:

`javac -cp target/aistr.jar:. Errors.java instrumented/Problem11.java`

And to run a Reachability problem:

`java -cp target/aistr.jar:./instrumented:. Problem11`

## Lab 2 - Symbolic Execution
For Lab 2, it is very similar to the steps that are shown for Lab 1. However, there are a few changes in the commands.

First of all, we need to use the following command to instrument a java file:

`java -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=symbolic --file=Problem1.java > instrumented/Problem1.java`

Second of all, we need to add the Z3 library to the classpath to be able to do symbolic execution. We would then compile  using the following command:

`javac -cp target/aistr.jar:lib/com.microsoft.z3.jar:. instrumented/Problem1.java `

Finally, we also need to add the Z3 library to the classpath when running an instrumented Java file for the second lab:

`java -cp target/aistr.jar:lib/com.microsoft.z3.jar:./instrumented:. Problem1`

## Lab 3 - Automated Patching with Genetic Algorithms
For Lab 3, it is almost identical to the steps shown for Lab 1. The only change is to use the `patching` type when instrumenting the file:

`java -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=patching --file=Problem1.java > instrumented/Problem1.java`


# Setting

The code has been tested with the following configuration:

* Maven 3.5.4
* Java 8
* JavaParser 3.18.0
