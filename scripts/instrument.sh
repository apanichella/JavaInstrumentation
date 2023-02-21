#!/bin/bash
# cd /home/str/JavaInstrumentation
mkdir -p instrumented
DATASET="../RERS"
CUSTOM_DATASET=${2:-"./custom_problems"}

# If the target is not built yet:
# mvn clean package

instrument () {
    FILE="$CUSTOM_DATASET/Problem$1.java"
    if [ -f "$FILE" ]; then
      echo "Custom problem: $1"
    else 
      echo "Normal problem: $1"
      FILE="$DATASET/Problem$1/Problem$1.java"
    fi
    echo "Instrumenting $1";
    java -XX:+UseG1GC -Xmx4g -cp target/aistr.jar nl.tudelft.instrumentation.Main --type=$2 --file="$FILE" > "instrumented/Problem$1.java" &&
    echo "Compiling $1" &&
    javac -cp target/aistr.jar:lib/com.microsoft.z3.jar:. Errors.java "instrumented/Problem$1.java"
}

echo $2
for i in $(ls $CUSTOM_DATASET | grep .java); do
  temp=${i#Problem}
  instrument ${temp%.java} $1
done
for i in $(ls $DATASET | sed -E "/Problem(3|5|6|8|9|16|18|19)$/d"); do
  instrument ${i#Problem} $1
done