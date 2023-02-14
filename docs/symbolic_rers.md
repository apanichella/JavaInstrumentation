# Symbolic Execution
In this tutorial, I show how I ran KLEE on a RERS problem.

The c code will not compile as given, we need to make a few changes, and some more to run KLEE on it.

**NOTE**: The following example was done on Problem10 from RERS Challenge 2017 but the same modifications can also be made on the RERS 2020 problems. The output that you see here may not reflect the output of the RERS 2020 problems.

First, replace:

```C++
    extern void __VERIFIER_error(int);
```

with:

```C++
#include <klee/klee.h>

void __VERIFIER_error(int i) {
    fprintf(stderr, "error_%d ", i);
    assert(0);
}

```

The assert causes a crash, which is what we want to find using KLEE.

Then, replace:

```C++
int main()
{
    // main i/o-loop
    while(1)
    {
        // read input
        int input;
        scanf("%d", &input);        
        // operate eca engine
        if((input != 2) && (input != 5) && (input != 3) && (input != 1) && (input != 4))
          return -2;
        calculate_output(input);
    }
}
```

with, e.g.,:

```C++
int main()
{
    int length = 20;
    int program[length];
    klee_make_symbolic(program, sizeof(program), "program");

    // main i/o-loop
    for (int i = 0; i < length; ++i) {
        // read input
        int input = program[i];
    if((input != 1) && (input != 2) && (input != 3) && (input != 4) && (input != 5)) return 0;
        // operate eca engine
        calculate_output(input);
    }
}
```

This makes all input symbolic, for up to length 20 inputs. KLEE will try to trigger new code branches with symbolic values for these 20 integer inputs. In contrast to AFL, we do not need to specify the input files. Everything is determined by analyzing the code.

You can compile the obtained .c file using:

```
clang-6.0 -I /path/to/klee/source/include -emit-llvm -g -c /path/to/Problem/10/Problem10.c -o /path/to/output/folder/Problem10.bc
```

(the source files of klee are located oin `/home/str/klee/`)

You can run the obtained llvm code using:

```
./path/to/klee/binary/klee /path/to/instrumented/Problem10.bc
```

(all binaries of KLEE are located in `/home/str/klee/build/bin/`)

This generated the following output:

```
KLEE: output directory is "/home/klee/sicco/ReachabilityRERS2017/Problem10/klee-out-1"
KLEE: Using STP solver backend
WARNING: this target does not support the llvm.stacksave intrinsic.
KLEE: WARNING: undefined reference to function: fflush
KLEE: WARNING: undefined reference to function: fprintf
KLEE: WARNING: undefined reference to function: printf
KLEE: WARNING: undefined reference to variable: stderr
KLEE: WARNING: undefined reference to variable: stdout
KLEE: WARNING ONCE: calling external: printf(45009888, 25)
25
KLEE: WARNING ONCE: calling external: fflush(47316350944256)
26
25
KLEE: ERROR: /home/klee/sicco/ReachabilityRERS2017/Problem10/Problem10.c:2456: external call with symbolic argument: fprintf
KLEE: NOTE: now ignoring this error at this location
21
20
22
20
21
22
26
22
20
22
20
23
26
26
24
20
22
25
20
22
21
20
22
20
23
21
20
20
21
20
23
22
24
22
23
20
22
20
20
22
22
22
21
23
25
20
25
21
20
25
21
22
21
26
KLEE: WARNING ONCE: calling external: fprintf(47316350943680, 41577152, 46)
error_46 KLEE: ERROR: /home/klee/sicco/ReachabilityRERS2017/Problem10/Problem10.c:10: ASSERTION FAIL: 0
KLEE: NOTE: now ignoring this error at this location
25
26
```

You can get rid of the warnings if you like. At this point error 46 has been generated, a little further more will occur:

```
error_12 26
error_2 error_22 26
22
error_30 error_70 21
22
20
error_37 error_65 23
23
...
```

The results were written to `klee-out-1` folder on my machine. The results are in binary form, you can inspect them using the ktest tool:

```
./path/to/ktest-tool klee-out-1/test000001.ktest
```

(again, all binaries of KLEE are located in `/home/str/klee/build/bin/`)

gives:

```
ktest file : 'klee-last/test000001.ktest'
args       : ['Problem10.bc']
num objects: 1
object    0: name: b'program'
object    0: size: 80
object    0: data: b'\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
```

An input with only \x00's. In test000002.ktest, the first one is a \x04:

```
object    0: data: b'\x04\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
```

etc.

Running

```
./path/to/klee-stats ./klee-out-1/
```

(again, all binaries of KLEE are located in `/home/str/klee/build/bin/`)

gives some coverage information:

```
----------------------------------------------------------------------------
|    Path     |  Instrs|  Time(s)|  ICov(%)|  BCov(%)|  ICount|  TSolver(%)|
----------------------------------------------------------------------------
|./klee-out-4/|  245185|     6.14|    83.52|    75.58|    6952|       54.67|
----------------------------------------------------------------------------
```

With klee run test you can replay inputs, the following works on my system:

```
export LD_LIBRARY_PATH=/home/str/klee/build/lib/:$LD_LIBRARY_PATH
clang-6.0 -I /home/str/klee/include -L /home/str/klee/build/lib Problem10.c -o Problem10.bc -lkleeRuntest
KTEST_FILE=klee-out-1/test000001.ktest ./Problem10.bc
```

which gives nothing, as expected with an input of all 0s. A more interesting cases:

```
KTEST_FILE=klee-out-1/test000227.ktest ./Problem10.bc 
26
21
21
23
23
Invalid input: 2
Invalid input: 2
21
error_30 Problem10.bc: Problem10.c:10: void __VERIFIER_error(int): Assertion `0' failed.
Aborted
```

In case you are interested (as I am), you can find the query send to the SMT solver:

```
cat klee-out-1/test000227.kquery
array program[80] : w32 -> w8 = symbolic
(query [(Eq 5
             (ReadLSB w32 0 program))
         (Eq 1
             (ReadLSB w32 4 program))
         (Eq 2
             (ReadLSB w32 8 program))
         (Eq 3
             (ReadLSB w32 12 program))
         (Eq 3
             (ReadLSB w32 16 program))
         (Eq 2
             N0:(ReadLSB w32 20 program))
         (Eq false (Eq 5 N0))
         (Eq false (Eq 4 N0))
         (Eq false (Eq 3 N0))
         (Eq false (Eq 1 N0))
         (Eq 2
             N1:(ReadLSB w32 24 program))
         (Eq false (Eq 5 N1))
         (Eq false (Eq 4 N1))
         (Eq false (Eq 3 N1))
         (Eq false (Eq 1 N1))
         (Eq 3
             (ReadLSB w32 28 program))]
        false)
```

compare this with the generated input (which seems to satisfy all the constraints, of which some seem not very optimized...e.g.: (N1=2 and N1 != 5 and N1 != 4 and N1 != 3 and N1 != 1):

```
ktest-tool klee-out-1/test000227.ktest
ktest file : 'klee-out-1/test000227.ktest'
args       : ['Problem10.bc']
num objects: 1
object    0: name: b'program'
object    0: size: 80
object    0: data: b'\x05\x00\x00\x00\x01\x00\x00\x00\x02\x00\x00\x00\x03\x00\x00\x00\x03\x00\x00\x00\x02\x00\x00\x00\x02\x00\x00\x00\x03\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
```

and send it to the SMT solver, although I am not yet sure on how to get the output from the solver..(anyone can figure it out from the very brief documentation? http://klee.github.io/docs/kleaver-options/ and http://klee.github.io/docs/kquery/)

```
./path/to/kleaver klee-out-1/test000227.kquery
```

(again, all binaries of KLEE are located in `/home/str/klee/build/bin/`)


We created a Python script that computes the summary of the errors that were found by KLEE. The script is located in `scripts/analyze_klee_live.py`. The script requires the output of KLEE to be its input. To run KLEE while collecting the summary, run the command listed below. This pipes the output of KLEE on stderr to the `analyze_klee_live.py`.

```
./path/to/klee/binary/klee /path/to/instrumented/Problem10.bc 2>&1 | python3 ./path/to/analyze_klee_live.py
```

Running this command gives the following summary:
```
...

Summary:
error_8 found in 13.299s
error_77 found in 11.550s
error_79 found in 13.261s
Found 3 unique errors

...
```


Read more on the features of KLEE at: http://klee.github.io/docs/. Happy bug hunting.

https://feliam.wordpress.com/2010/10/07/the-symbolic-maze/ is also well-worth checking out.
