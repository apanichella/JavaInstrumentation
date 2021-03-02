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

Then, we define a new z3 constant using the name of the variable and the conveniently built in counter to give it a number, this will come in handy for single static assignment later.
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

### Example model
PathTracker's solve method allows you to specify a boolean value telling it to print the path constraint. For problem 1,  
without any changes to the provided code, initially the model looks like this:
```
Model: (and (= my_a547336540_17 "g")
     (= my_cf_16 true)
     (= my_a1855872761_15 "g")
     (= my_a612577343_14 8)
     (= my_a1305805768_13 13)
     (= my_a691849188_12 10)
     (= my_a1122863037_11 10)
     (= my_inputs9_10 "iJ")
     (= my_inputs8_9 "iI")
     (= my_inputs7_8 "iH")
     (= my_inputs6_7 "iG")
     (= my_inputs5_6 "iF")
     (= my_inputs4_5 "iE")
     (= my_inputs3_4 "iD")
     (= my_inputs2_3 "iC")
     (= my_inputs1_2 "iB")
     (= my_inputs0_1 "iA")
     true)
Branches: true
New branch: (= false false)
```
This shows us the constraints for the global variables in problem 1.
It is up to you to put in more constraints as you encounter them going through the code.

Once you have implemented the required functionality in SymbolicExecutionLab, it will start looking more like this (somewhere during the run):
```
Model: (and (= my_cf_21 true)
     (= my_a1122863037_19 14)
     (= my_a547336540_18 "i")
     (= my_cf_17 false)
     (= my_cf_16 true)
     (= my_a612577343_14 5)
     (= my_cf_13 false)
     (= my_cf_12 true)
     (= my_a612577343_10 4)
     (= my_a547336540_9 "g")
     (= my_cf_8 false)
     (= my_cf_7 true)
     (= my_a1305805768_5 15)
     (= my_a547336540_4 "f")
     (= my_cf_3 false)
     (= my_cf_2 true)
     true)
Branches: (let ((a!1 (not (and (and my_cf_21 (= my_a1122863037_19 14))
                     (= "iD" input_20)
                     (= "i" my_a547336540_18)))))
  (and a!1
       (not (and (= "iC" input_20)
                 (= "i" my_a547336540_18)
                 (= my_a1122863037_19 14)
                 my_cf_21))
       (not (and my_cf_21
                 (= "iH" input_20)
                 (= my_a1122863037_19 14)
                 (= "i" my_a547336540_18)))
       (and my_cf_21 (= my_a1122863037_19 14))
       (not (and my_cf_21 (= my_a1122863037_19 13)))
       (= my_a612577343_14 5)
       (= "iC" input_15)
       my_cf_16
       (= "g" my_a547336540_9)
       (not (and (= my_a612577343_14 4)
                 (= "iA" input_11)
                 (= "g" my_a547336540_9)
                 my_cf_13))
       (= "g" my_a547336540_9)
       (= "iF" input_11)
       my_cf_12
       (= my_a612577343_10 4)
       true))
New branch: (= (and (= my_a1122863037_19 14)
        (= "i" my_a547336540_18)
        my_cf_21
        (= "iE" input_20))
   true)
```
