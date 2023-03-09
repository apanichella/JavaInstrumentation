# Learning models
For this lab, you will implement the L* (L-star) algorithm for learning mealy machines of the RERS Problems.
In previous lab assignments, you have implemented methods to find errors in the reachability problems of the RERS challenge (problems 11-19). For this lab we will be using the Linear Temporal Logic (LTL) problems, these are also in the `RERS` folder and correspond to problems 1-9. Specifically, you should learn mealy machines from problems 1, 2, 4 and 7. Our ProblemPin is also still available in the `custom_problems` folder, and can be useful for debugging your implementation on a smaller example.

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
In the example below, there are only two input symbols: `0` and `1`. For each sequence `S·E` and `S·A·E`, the last output of running that word/trace is shown.
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
To convert this to a pdf, you need you to have graphviz installed: `apt install graphviz`. You can then use `dot -Tpdf -O hypothesis.dot` to create a pdf. For any inconsistencies, the hypothesis will create dummy states or edges ending with a `?`. The dummy states and edges are also colored red in the pdf.

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

If you find an inconsistency, you can return something useful to add to either S or E, for example: (`return Optional.of(sa)`). In `LearningLab`, you can use the methods for consistency and closedness. To create a observation table that is closed an consistent by adding to S or E, for example:
```java
Word<String> newPrefix = ...;
observationTable.addToS(newPrefix);
```
By using the methods `addToS` and `addToE`, the observation table will automatically be populated with the output of the system under learn.

- What is wrong with an inconsistent observation table? Can you explain it in terms of the mealy machine that can be created from the observation table.
- What is wrong with an observation table that is not closed? Can you explain it in terms of the mealy machine that can be created from the observation table.

## Task 2: learning your first state machine, test for equivalence
Once you have a closed and consistent hypothesis model, you can initially use the `RandomWalkEquivalenceChecker`, but in a later task, you will implement the `WMethodEquivalenceChecker`, to verify a method and possibly get a counterexample, use the `verify` method.

```java
MealyMachine hypothesis = observationTable.generateHypothesis();
Optional<Word<String>> counterexample = equivalenceChecker.verify(hypothesis);
```


- When the equivalence checker does not return a counterexample, you are done learning, but is your model guaranteed to be correct? Why (not)?.

## Task 3: processing a counter example
When you receive a counterexample from the equivalence checker, you then need to process the counterexample such that the observation table is updated to include the behavior for that example. For the processing, you might need to get the output of the system for a specific input. To get the final output after running a trace/word, you can use `sul.getLastOutput(...)`.

After processing a counterexample, you can complete the learning loop by continuously making the observation table consistent, checking for equivalence, and when a counterexample is found, incorporate it into the observation table.


## Task 4: implementing the W-method
Up to now, you have tested your method using a random walk equivalence checker. Although the random walk equivalence checker can be powerful, it has no guarantees on finding counterexamples.

For this task, you will implement the W-method for equivalence checking. The W-method checks all strings composed of an access sequence (A), a word of length `w` over the input symbols (W), and a distinguishing sequence (D).

The W-method should be implemented in `WMethodEquivalenceChecker.java`. The verify method should return a counterexample if it finds one. To get the access sequences and the distinguishing sequences, you can use the following methods:
```java
accessSequenceGenerator.getAccessSequences(); // For the access sequences
distinguishingSequenceGenerator.getDistinguishingSequences(); // For the distinguishing sequences
```

To get the output of the system for a specific input you can again use `sul.getLastOutput(...)`.



- When the w-method equivalence checker does not return a counterexample, you are done learning, but is your model guaranteed to be equivalent to the system under learn? Why (not)?.

## Task 5: run on RERS problems

Learn models from the RERS challenges 1, 2, 4, 7 and the `ProblemPin` in the `custom_problems` folder using your W-method.
- Plot the number of states over time for each RERS challenge and the ProblemPin.
Include the final renders of the problems in your report.
- The `ProblemPin` represents a keypad where someone must enter a passcode. View the mealy machine of this problem and explain if the model matches your expectation. Do you think this is 'secure'? Why/ why not?