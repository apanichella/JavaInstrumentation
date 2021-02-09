package nl.tudelft.instrumentation.patching;

import java.util.*;
import java.util.Random;

public class PatchingLab {

        static Random r = new Random();

        static void initialize(){
                // initialize the population based on OperatorTracker.operators
        }

        static boolean encounteredOperator(String operator, int left, int right, int operator_nr){
                // do something useful
                if(OperatorTracker.testing){
                        // these operators are executed in test OperatorTracker.current_test:
                        System.out.print(String.valueOf(OperatorTracker.current_test) + ":" + String.valueOf(operator_nr) + " ");
                }
                String replacememt = OperatorTracker.operators[operator_nr];
                if(replacememt.equals("!=")) return left != right;
                if(replacememt.equals("==")) return left == right;
                if(replacememt.equals("<")) return left < right;
                if(replacememt.equals(">")) return left > right;
                if(replacememt.equals("<=")) return left <= right;
                if(replacememt.equals(">=")) return left >= right;
                return false;
        }

        static boolean encounteredOperator(String operator, boolean left, boolean right, int operator_nr){
                // do something useful
                if(OperatorTracker.testing){
                        // these operators are executed in test OperatorTracker.current_test:
                        System.out.print(String.valueOf(OperatorTracker.current_test) + ":" + String.valueOf(operator_nr) + " ");
                }
                String replacememt = OperatorTracker.operators[operator_nr];
                if(replacememt.equals("!=")) return left != right;
                if(replacememt.equals("==")) return left == right;
                return false;
        }

        static String fuzz(String[] inputSymbols){
                // do something useful
                LinkedList<Boolean> test_result = OperatorTracker.test_result;
                System.out.println("test result:");
                for(Boolean b : test_result){
                        if(b.booleanValue()) System.out.print(1);
                        else System.out.print("0");
                }
                System.out.println();
                // use test_result to update the operators, currently random
                for(int i = 0; i < 10; ++i){
                        int index = r.nextInt(OperatorTracker.operators.length);
                        String op = OperatorTracker.operators[index];
                        if(op.equals("<") || op.equals(">") || op.equals(">=") || op.equals("<=") ){
                                int value = r.nextInt(4);
                                if(value == 0) OperatorTracker.operators[index] = "<";
                                if(value == 1) OperatorTracker.operators[index] = ">";
                                if(value == 2) OperatorTracker.operators[index] = "<=";
                                if(value == 3) OperatorTracker.operators[index] = ">=";
                        } else {
                                if(op.equals("==")) OperatorTracker.operators[index] = "!=";
                                if(op.equals("!=")) OperatorTracker.operators[index] = "==";
                        }
                }
                System.out.println("Testing new operators");
                for(String s : OperatorTracker.operators){
                        System.out.print(s + " ");
                }
                System.out.println();
                OperatorTracker.startTesting();
                return "R";
        }

        static void output(String out){
                System.out.println(out);
        }
}