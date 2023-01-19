# Fuzzing problems from the RERS challenge

We are going to fuzz programs from the RERS 2020 challenge. If you are interested in competing in next year's edition of RERS, please send a mail to Sicco!
The first step is to download and install AFL:

AFL - http://lcamtuf.coredump.cx/afl/

Simply run make and you are good to go. To make it easier for you, AFL is already installed on the docker image for you (located in `/home/str/AFL`).

Then download and unpack the RERS Reachability challenge programs:

http://rers-challenge.org/2020/problems/sequential/SeqReachabilityRers2020.zip.


Again, to make it easier for you, we have already downloaded the challenge programs for you (located in `/home/str/RERS`).

The following example was generated using older RERS problems and may not reflect the same results as the newer version but the same (code) changes that are shown in the 
example can still be used on the newer RERS problems.

The archives contain highly obfuscated c and java code, e.g., :

In `Problem11/Problem11.c`:

```C++
...
void errorCheck() {
	    if(((a1392217129 == 10 && a1304806974 == 36) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(0);
	    }
	    if(((a270033534 == 35 && a894128990 == 34) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(1);
	    }
	    if(((a2021551447 == 6 && a71487061 == 15) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(2);
	    }
	    if(((a553209804 == 8 && a919251806 == 8) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(3);
	    }
	    if(((a547511656 == 7 && a168638684 == 8) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(4);
	    }
	    if(((a1209890889 == 10 && a1735071167 == 13) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(5);
	    }
	    if(((a1209890889 == 11 && a1735071167 == 13) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(6);
	    }
	    if(((a894128990 == 32 && a780728121 == 35) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(7);
	    }
	    if(((a76449788 == 33 && a71487061 == 12) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(8);
	    }
	    if(((a76449788 == 32 && a71487061 == 12) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(9);
	    }
	    if(((a1367961664 == 34 && a168638684 == 13) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(10);
	    }
	    if(((a1023470345 == 16 && a1735071167 == 10) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(11);
	    }
	    if(((a553209804 == 7 && a919251806 == 8) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(12);
	    }
	    if(((a2021551447 == 5 && a71487061 == 15) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(13);
	    }
	    if(((a823314445 == 16 && a1735071167 == 9) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(14);
	    }
	    if(((a521273111 == 9 && a1735071167 == 11) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(15);
	    }
	    if(((a1575755525 == 7 && a894128990 == 35) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(16);
	    }
	    if(((a2021551447 == 3 && a71487061 == 15) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(17);
	    }
	    if(((a1304806974 == 33 && a894128990 == 32) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(18);
	    }
	    if(((a1980207818 == 32 && a71487061 == 11) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(19);
	    }
	    if(((a1643269988 == 33 && a71487061 == 13) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(20);
	    }
	    if(((a1023470345 == 9 && a1735071167 == 10) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(21);
	    }
	    if(((a61085062 == 36 && a1360225377 == 34) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(22);
	    }
	    if(((a2128642633 == 9 && a168638684 == 11) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(23);
	    }
	    if(((a894128990 == 33 && a780728121 == 35) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(24);
	    }
	    if(((a1575755525 == 10 && a894128990 == 35) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(25);
	    }
	    if(((a521273111 == 2 && a1735071167 == 11) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(26);
	    }
	    if(((a1643269988 == 34 && a71487061 == 14) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(27);
	    }
	    if(((a1209890889 == 9 && a1735071167 == 13) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(28);
	    }
	    if(((a1370900715 == 15 && a780728121 == 32) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(29);
	    }
	    if(((a1824730115 == 15 && a168638684 == 15) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(30);
	    }
	    if(((a1392217129 == 12 && a1360225377 == 32) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(31);
	    }
	    if(((a2128642633 == 11 && a168638684 == 11) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(32);
	    }
	    if(((a1392217129 == 14 && a1304806974 == 36) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(33);
	    }
	    if(((a1980207818 == 33 && a71487061 == 11) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(34);
	    }
	    if(((a1643269988 == 35 && a71487061 == 14) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(35);
	    }
	    if(((a419289115 == 14 && a168638684 == 10) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(36);
	    }
	    if(((a1980207818 == 34 && a71487061 == 11) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(37);
	    }
	    if(((a547511656 == 11 && a168638684 == 8) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(38);
	    }
	    if(((a894128990 == 33 && a780728121 == 36) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(39);
	    }
	    if(((a1360225377 == 32 && a71487061 == 10) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(40);
	    }
	    if(((a553986020 == 11 && a1360225377 == 33) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(41);
	    }
	    if(((a729595174 == 32 && a168638684 == 14) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(42);
	    }
	    if(((a419289115 == 10 && a780728121 == 34) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(43);
	    }
	    if(((a547511656 == 6 && a168638684 == 8) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(44);
	    }
	    if(((a419289115 == 14 && a780728121 == 34) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(45);
	    }
	    if(((a1392217129 == 17 && a1304806974 == 36) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(46);
	    }
	    if(((a1367961664 == 36 && a1304806974 == 32) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(47);
	    }
	    if(((a399501280 == 9 && a1304806974 == 35) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(48);
	    }
	    if(((a278675587 == 32 && a1360225377 == 35) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(49);
	    }
	    if(((a1283870923 == 35 && a894128990 == 33) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(50);
	    }
	    if(((a729595174 == 36 && a168638684 == 14) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(51);
	    }
	    if(((a512217640 == 33 && a1735071167 == 12) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(52);
	    }
	    if(((a399501280 == 12 && a1304806974 == 35) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(53);
	    }
	    if(((a1353794423 == 36 && a919251806 == 6) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(54);
	    }
	    if(((a547511656 == 11 && a919251806 == 10) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(55);
	    }
	    if(((a399501280 == 8 && a1304806974 == 35) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(56);
	    }
	    if(((a553986020 == 10 && a1360225377 == 33) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(57);
	    }
	    if(((a1367961664 == 35 && a1304806974 == 32) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(58);
	    }
	    if(((a1209890889 == 6 && a1735071167 == 13) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(59);
	    }
	    if(((a823314445 == 12 && a1735071167 == 9) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(60);
	    }
	    if(((a419289115 == 12 && a168638684 == 10) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(61);
	    }
	    if(((a1643269988 == 34 && a71487061 == 13) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(62);
	    }
	    if(((a1353794423 == 34 && a168638684 == 12) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(63);
	    }
	    if(((a894128990 == 34 && a1304806974 == 34) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(64);
	    }
	    if(((a1575755525 == 8 && a894128990 == 35) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(65);
	    }
	    if(((a572079232 == 33 && a919251806 == 4) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(66);
	    }
	    if(((a399501280 == 13 && a1304806974 == 35) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(67);
	    }
	    if(((a419289115 == 13 && a168638684 == 10) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(68);
	    }
	    if(((a547511656 == 12 && a168638684 == 8) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(69);
	    }
	    if(((a1392217129 == 15 && a1304806974 == 36) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(70);
	    }
	    if(((a1370900715 == 9 && a780728121 == 32) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(71);
	    }
	    if(((a1392217129 == 17 && a1360225377 == 32) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(72);
	    }
	    if(((a1575755525 == 9 && a894128990 == 35) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(73);
	    }
	    if(((a76449788 == 34 && a71487061 == 12) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(74);
	    }
	    if(((a553209804 == 6 && a919251806 == 8) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(75);
	    }
	    if(((a1643269988 == 36 && a919251806 == 5) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(76);
	    }
	    if(((a823314445 == 11 && a1735071167 == 9) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(77);
	    }
	    if(((a1023470345 == 11 && a1735071167 == 15) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(78);
	    }
	    if(((a894128990 == 34 && a780728121 == 35) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(79);
	    }
	    if(((a1643269988 == 35 && a71487061 == 13) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(80);
	    }
	    if(((a1367961664 == 32 && a1304806974 == 32) && a1537379265 == 15)){
	    cf = 0;
	    __VERIFIER_error(81);
	    }
	    if(((a1283870923 == 34 && a894128990 == 33) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(82);
	    }
	    if(((a2021551447 == 7 && a71487061 == 15) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(83);
	    }
	    if(((a894128990 == 33 && a1735071167 == 14) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(84);
	    }
	    if(((a1565409750 == 9 && a168638684 == 9) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(85);
	    }
	    if(((a1283870923 == 32 && a894128990 == 33) && a1537379265 == 12)){
	    cf = 0;
	    __VERIFIER_error(86);
	    }
	    if(((a990334020 == 32 && a71487061 == 17) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(87);
	    }
	    if(((a512217640 == 35 && a1735071167 == 12) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(88);
	    }
	    if(((a1367961664 == 33 && a168638684 == 13) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(89);
	    }
	    if(((a894128990 == 36 && a780728121 == 35) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(90);
	    }
	    if(((a1643269988 == 36 && a71487061 == 14) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(91);
	    }
	    if(((a1643269988 == 32 && a71487061 == 13) && a1537379265 == 10)){
	    cf = 0;
	    __VERIFIER_error(92);
	    }
	    if(((a894128990 == 35 && a780728121 == 36) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(93);
	    }
	    if(((a1023470345 == 12 && a1735071167 == 10) && a1537379265 == 11)){
	    cf = 0;
	    __VERIFIER_error(94);
	    }
	    if(((a1370900715 == 13 && a780728121 == 32) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(95);
	    }
	    if(((a1353794423 == 35 && a168638684 == 12) && a1537379265 == 14)){
	    cf = 0;
	    __VERIFIER_error(96);
	    }
	    if(((a1370900715 == 10 && a780728121 == 32) && a1537379265 == 13)){
	    cf = 0;
	    __VERIFIER_error(97);
	    }
	    if(((a553986020 == 8 && a1360225377 == 33) && a1537379265 == 8)){
	    cf = 0;
	    __VERIFIER_error(98);
	    }
	    if(((a547511656 == 8 && a919251806 == 10) && a1537379265 == 9)){
	    cf = 0;
	    __VERIFIER_error(99);
	    }
	}
 void calculate_outputm46(int input) {
    if((((a1360225377 == 32 && (a1537379265 == 8 &&  cf==1 )) && a1392217129 == 14) && (input == 10))) {
    	cf = 0;
    	a894128990 = 32 ;
    	a780728121 = 36 ;
    	a1537379265 = 13; 
    	 printf("%d\n", 25); fflush(stdout); 
    } if((((( cf==1  && a1360225377 == 32) && (input == 1)) && a1537379265 == 8) && a1392217129 == 14)) {
    	cf = 0;
    	a1643269988 = 34 ;
    	a1537379265 = 10;
    	a71487061 = 13; 
    	 printf("%d\n", 18); fflush(stdout); 
    } 
}

...
 void calculate_output(int input) {
        cf = 1;

    if((a1537379265 == 8 &&  cf==1 )) {
    	if(( cf==1  && a1360225377 == 32)) {
    		calculate_outputm1(input);
    	} 
    	if(( cf==1  && a1360225377 == 34)) {
    		calculate_outputm3(input);
    	} 
    	if((a1360225377 == 35 &&  cf==1 )) {
    		calculate_outputm4(input);
    	} 
    } 
    if((a1537379265 == 9 &&  cf==1 )) {
    	if((a919251806 == 8 &&  cf==1 )) {
    		calculate_outputm8(input);
    	} 
    	if((a919251806 == 11 &&  cf==1 )) {
    		calculate_outputm10(input);
    	} 
    } 
    if((a1537379265 == 10 &&  cf==1 )) {
    	if((a71487061 == 17 &&  cf==1 )) {
    		calculate_outputm17(input);
    	} 
    } 
    if(( cf==1  && a1537379265 == 11)) {
    	if(( cf==1  && a1735071167 == 9)) {
    		calculate_outputm18(input);
    	} 
    	if((a1735071167 == 11 &&  cf==1 )) {
    		calculate_outputm20(input);
    	} 
    	if((a1735071167 == 14 &&  cf==1 )) {
    		calculate_outputm23(input);
    	} 
    } 
    if((a1537379265 == 12 &&  cf==1 )) {
    	if((a894128990 == 32 &&  cf==1 )) {
    		calculate_outputm25(input);
    	} 
    } 
    if((a1537379265 == 13 &&  cf==1 )) {
    	if((a780728121 == 36 &&  cf==1 )) {
    		calculate_outputm32(input);
    	} 
    } 
    if((a1537379265 == 14 &&  cf==1 )) {
    	if((a168638684 == 10 &&  cf==1 )) {
    		calculate_outputm35(input);
    	} 
    	if((a168638684 == 12 &&  cf==1 )) {
    		calculate_outputm37(input);
    	} 
    	if((a168638684 == 15 &&  cf==1 )) {
    		calculate_outputm40(input);
    	} 
    } 
    if((a1537379265 == 15 &&  cf==1 )) {
    	if((a1304806974 == 32 &&  cf==1 )) {
    		calculate_outputm41(input);
    	} 
    	if(( cf==1  && a1304806974 == 34)) {
    		calculate_outputm42(input);
    	} 
    } 
    errorCheck();

    if( cf==1 ) 
    	fprintf(stderr, "Invalid input: %d\n", input); 
}

int main()
{
    // main i/o-loop
    while(1)
    {
        // read input
        int input;
        scanf("%d", &input);        
        // operate eca engine
        if((input != 4) && (input != 3) && (input != 10) && (input != 2) && (input != 9) && (input != 6) && (input != 1) && (input != 8) && (input != 7) && (input != 5))
          return -2;
        calculate_output(input);
    }
}
```

Good luck understanding the logic underlying this code... All we know is that it is a simple state machine, which has been obfuscated to make analysis harder. In case you are interested in obfuscation: check out Tigress: http://tigress.cs.arizona.edu. We of course use automated tools in order to try to understand it!

The goal of the RERS challenge is to discover exactly which VERIFIER_error(s) are reachable, some of the if statements in errorCheck() can never occur, while other can occur but only after reasing a very long input. The input to the program is simply a long list of integers, seperated by spaces or line ends. After reading an input, the program returns an integer, or gives an error.

The c code will not compile as given, we need to make a few changes, and some more to run AFL on it.

First, replace:

```C++
    extern void __VERIFIER_error(int);
(line 6)
```

with:

```C++
void __VERIFIER_error(int i) {
    fprintf(stderr, "error_%d ", i);
    assert(0);
}
```

The assert causes a crash, which is what we want to find using AFL.

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
        if((input != 4) && (input != 3) && (input != 10) && (input != 2) && (input != 9) && (input != 6) && (input != 1) && (input != 8) && (input != 7) && (input != 5))
          return -2;
        calculate_output(input);
    }
}
```

with:

```C++
int main()
{
    // main i/o-loop
    while(1)
    {
        // read input
        int input = 0;
        int ret = scanf("%d", &input);
        if (ret != 1) return 0;        
        // operate eca engine
        if((input != 4) && (input != 3) && (input != 10) && (input != 2) && (input != 9) && (input != 6) && (input != 1) && (input != 8) && (input != 7) && (input != 5))
          return -2;
        calculate_output(input);
    }
}
```

This avoids a hang in the scanf function created when the input ends.

Now all we need to run afl is to create two directories: tests and findings. In tests you have to provide AFL with some example inputs, makes sure that you give an integer with a space or new line. For instance, I have the file 1.txt in the test directory, containing:

```C++
"1
"
```

so a 1 with a new line.

Make sure that you create files with all possible input symbols (otherwise AFL has to discover these which might take a long time).

Now we are ready to fuzz.

First compile the file you edited (for example Problem11.c) using the afl-gcc compiler or in my case afl-clang:

`path_to_afl/afl-clang Problem11.c -o Problem11`

Then run afl on the obtained binary:

`path_to_afl/afl-fuzz -i path_to_test_dir -o path_to_findings_dir path_to_binary/Problem11`

You should see AFL starting, perhaps with some warnings due to useless input files, you can simply ignore these.

Keep on fuzzing.


## Retrieving results
Then after some time, you can investigate the findings directory. You will find for instance the crashes found in the crashes directory, and interesting test cases (which it uses to trigger new paths) in queue. You can run the crashes found in the fuzzed program in order to answer the reachability problems:

```C++
cd findings/crashes
cat id:000000,sig:06,src:000001,op:havoc,rep:4 | ../../Problem11
```

Gives in my case:

```
16
18
error_51 Assertion failed: (0), function __VERIFIER_error, file Problem11.c, line 8.
Abort trap: 6
```

**Note**: In the newer version of RERS problems, you will not see `Abort trap: 6` but you will see `Aborted` instead.

We created a Python script that computes the summary of the errors that were found by AFL. The script is located in `scripts/analyze_afl.py`. The script requires the AFL finding directory and the binary to run the crash files with: `./scripts/analyze_afl AFL_FINDINGS_DIRECTORY PROBLEM_BINARY`. If you have the files and folders in line with the examples above, you can run `./scripts/analyze_afl findings Problem11` to get all the errors.
Running the script gives the following summary:

```
Summary:
error_0 found in 9.47s
error_60 found in 1.01s
error_62 found in 4.53s
error_95 found in 4.03s
Found 4 unique errors in "./afl/afl/11/findings"
```



In this way, and some extra tricks, we obtained third place in the reachability category of the RERS 2016 challenge: http://rers-challenge.org/2016/index.php?page=results. We are team Radboud (together with PhD students from Radboud university), for some reason they still did not update the names. We wrote a paper describing our approach (including learning which you will learn later in this course), which is available at https://arxiv.org/pdf/1611.02429.pdf. Please take a look if you are interested.
AFL can reach quite some errors, but to compete in the 2020 challenge we expect combinations will be required with learning, mutation, tainting, and concolic execution.
