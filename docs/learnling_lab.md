# Learning models
For this lab you will implement the L* (L-star) algorithm for learning mealy machines for the RERS Problems.
In previous lab assignments, you have implemented methods to find errors in the reachability problems of the RERS challenge (problems 11-19). For this lab we will be using the Linear Temporal Logic (LTL) problems, these are also in the `RERS` folder and correspond to problems 1-9. Specifically, you should learn mealy machines from problems 1, 2, 4 and 7. Our ProblemPin is also still available in the `custom_problems` folder, and can be usefull for debug your implementation on a smaller example.

We have again provided the setup and instrumentation. To instrument the files for this lab, you can use the `learning` option like so:
`java -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=learning --file=Problem1.java > instrumented/Problem1.java`.
The instrumentation is minimal and only captures the output of the program.
You will be working on three different files located in `src/main/java/nl/tudelft/instrumentation/learning`:
- `LearningLab.java` for the general learning loop:  updating the observation table to make it closed and consistent, generating a hypothesis model, checking for equivalence and possibly incorporating the counterexample into the observation table.
- `ObservationTable.java` for checking whether the observation table is closed and consistent.
- `WMethodEquivalenceChecker.java` for implementing the W-method equivalence checker.


## Task 1: updating the observation table, make closed & consistent
The first step in learning a model using L* is to update the observation table to make it closed and consistent.
The methods that need to be implemented can be found in the `ObservationTable.java`.

In `LearningLab.java` we print the current observation table using `ObservationTable.print()`.An example of the observation table is shown below. The row starting with E contains all the suffixes in `ObservationTable.E`.The Rows labeled with `S` contain all the prefixes in `ObservationTable.S`. Note that for the table below, the suffixes are `0` and `1` and `S` only contains the empty string. The rows with `S·A` are all the suffixes concatenated with each symbol in the input alphabet.
In the example below, there are only two input symbols: `0` and `1`. For each sequence `S·E` and `S·A·E`, the last ouput of running that word/trace is shown.
```
------------------------
| E  |   | 0    | 1    |
------------------------
| S  |   | ODD  | ODD  |
------------------------
| SA | 0 | EVEN | ODD  |
|    | 1 | ODD  | EVEN |
------------------------
```

To get a visual representation of an hypothesis, you can save the hypothesis as a dot file using 
```java
MealyMachine hypothesis = observationTable.generateHypothesis();
hypothesis.writeToDot("hypothesis.dot");
```
To convert this to a pdf, you need you to have graphviz installed: `apt install graphviz`. You can then use `dot -Tpdf -O hypothesis.dot` to create a pdf. For any inconsistiencies, the hypothesis will create dummy states or edges ending with a `?`. The dummy states and edges are also colored red in the pdf.

![Rendered version of the mealy machine from observation table shown above](../hypothesis.dot.png)

Your task is to implement the two methods
`ObservationTable.checkForClosed` and `ObservationTable.checkForConsistent`. To get a row of the observation table for a specific word from `S` and compare it to another row, you can use the following example:
```java
// Get the first word from S.
Word<String> s = S.get(0); 
 // Get the row corresponding to s
ArrayList<String> row1 = table.get(s);
// Get a symbol from the input alphabet
String a = inputSymbols[0]; 

Word<String> sa = s.append(a); 
// Get the row corresponding to s·a
ArrayList<String> row2 = table.get(sa);
// Check if the rows are equal
boolean areRowsEqual = row1.equals(row2);
```

If you find an incosistency, you can return something usefull to add to either S or E, for example: (`return Optional.of(sa)`). In `LearningLab`, you can use the methods for consistency and closedness. To create a observation table that is closed an consistent by adding to S or E, for example:
```java
Word<String> newPrefix = ...;
observationTable.addToS(newPrefix);
```
By using the methods `addToS` and `addToE`, the observationtable will automatically be populated with the output of the system under learn.

### Questions:
- What

## Task 2: learning your first state machine, test for equivalence
Once you have a closed and consistent hypothesis model, you can initially use the `RandomWalkEquivalenceChecker`, but in a later task, you will implement the `WMethodEquivalenceChecker`, to verify a method and possibly get a counterexample, use the `verify` method.

```java
MealyMachine hypothesis = observationTable.generateHypothesis();
Optional<Word<String>> counterexample = equivalenceChecker.verify(hypothesis);
```

When the equivalence checker not return a counterexample.

## Task 3: processing a counter example
When you receive a counterexample from the equivalence checker, you then need to process the counterexaple such that the observation table is updated to include the behavior for that example. For the processing, you might need to get the output of the system for a specific input.
## Task 4: implementing the w method
## Task 5: run on RERS problems