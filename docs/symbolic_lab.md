# Symbolic lab

In this lab, you will use symbolic execution to find as many paths through the RERS code as possible.

You will again need to modify the SymbolicExecutionLab.java file provided in this repository.
Once you have instrumented the RERS code, the instrumentation will make sure the appropriate methods in
SymbolicExecutionLab are called to build the path constraints for the current trace.

### createVar example
As an example, variable definitions are handled the following way:
```java
static MyVar createVar(String name, Expr value, Sort s){
        Context c = PathTracker.ctx;
        // create var, assign value, add to path constraint
        // we show how to do it for creating new symbols
        // please add similar steps to the functions below in order to obtain a path constraint
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s);
        PathTracker.z3model = c.mkAnd(c.mkEq(z3var, value), PathTracker.z3model);
        return new MyVar(z3var, name);
    }
```

Lets go through this line by line:

The instrumentation calls this function with the name of the variable, it's value, and the sort of the variable.
The sort is something the z3 smt solver needs under the hood, and is not something you need to worry about.
You just need to pass it on to the appropriate method.

First, we obtain the pathtracker context, which we can use to create new z3 constraints.

Then, we define a new z3 constant using the name of the variable and the conveniently built in counter to give it a number, this will come in handy later as we will see.
In the next line we append a rule using this z3 constant to the current z3 model in the path tracker. We do this using an and clause, with on one side an equality constraint
specifying the value of our variable, and on the other side the current z3model.

Finally, we return a MyVar containing the corresponding z3var and its name.

You will need to fill in the rest of the methods in a similar way.

### PathTracker
The pathtracker class is there to help you build your path constraints and call the solver.
It contains two sets of constraints, the z3model and z3branches. Do not forget to add the branches you
encounter to z3branches! A good place to do this would be in the encounteredNewBranch method in SymbolicFuzzingLab.

When you call solve, z3 will try to solve for the inputs considering your current path constraint. If successful,
it will tell you the inputs needed to satisfy the constraint. You should use this to augment your fuzzer by trying to solve
for the opposite side of the branch you are currently on.