package nl.tudelft.instrumentation.learning;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.algorithms.ttt.mealy.TTTLearnerMealy;
import de.learnlib.algorithms.ttt.mealy.TTTLearnerMealyBuilder;
import de.learnlib.api.algorithm.LearningAlgorithm.MealyLearner;
import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.Query;
import de.learnlib.filter.statistic.oracle.MealyCounterOracle;
import de.learnlib.oracle.equivalence.MealyWMethodEQOracle;
import de.learnlib.util.Experiment.MealyExperiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.WordBuilder;
import net.automatalib.words.impl.Alphabets;



public class LearnLibRunner {

    public class RersOracle implements MealyMembershipOracle<String, String> {

        @Override
        public void processQueries(Collection<? extends Query<String, Word<String>>> arg0) {
            for (Query<String, Word<String>> q : arg0) {
                String[] query = q.getInput().asList().toArray(new String[0]);
                WordBuilder<String> w = new WordBuilder<>();
                String[] output = LearningTracker.runNextTrace(query);
                List<String> out = Arrays.asList(output);
                // Only add output for the suffix
                w.addAll(out.subList(q.getPrefix().size(), out.size()));
                q.answer(w.toWord());
            }
    
        }
    }


    public void start(int w) {
        int EXPLORATION_DEPTH = w;
        Alphabet<String> inputs = Alphabets.fromArray(LearningTracker.inputSymbols);

        MealyMembershipOracle<String, String> sul = new RersOracle();
        MealyCounterOracle<String, String> mCounter = new MealyCounterOracle<String, String>(sul, "membership queries");
        MealyMembershipOracle<String, String> m = mCounter;

        // construct L* instance
        ExtensibleLStarMealy<String, String> lstar = new ExtensibleLStarMealyBuilder<String, String>()
                .withAlphabet(inputs) // input
                .withOracle(m)
                .create();
        
        // construct TTT instance
        TTTLearnerMealy<String, String> ttt = new TTTLearnerMealyBuilder<String, String>()
                .withAlphabet(inputs)
                .withOracle(m)
                .create();

        MealyLearner<String, String> learner = lstar;
        // MealyLearner<String, String> learner = ttt;

        // construct a W-method conformance test exploring the system up to depth
        // EXPLORATION_DEPTH from every state of a hypothesis
        MealyWMethodEQOracle<String, String> wMethod = new MealyWMethodEQOracle<String, String>(m, EXPLORATION_DEPTH);

        MealyExperiment<String, String> experiment = new MealyExperiment<String, String>(learner, wMethod, inputs);

        // turn on time profiling
        experiment.setProfile(true);

        // enable logging of models
        experiment.setLogModels(true);

        experiment.run();
        System.out.println("Done running");

        // get learned model
        MealyMachine<?, String, ?, String> result = experiment.getFinalHypothesis();

        // report results
        System.out.println("-------------------------------------------------------");
        System.out.println(SimpleProfiler.getResults());
        System.out.println(experiment.getRounds().getSummary());
        System.out.println(mCounter.getStatisticalData().getSummary());
        System.out.println("States: " + result.size());
        System.out.println("Sigma: " + inputs.size());
        System.out.println();
        System.out.println("Model: ");
        try {
            GraphDOT.write(result, inputs, new BufferedWriter(new FileWriter("learnlib-final.dot")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-------------------------------------------------------");
        if (learner.equals(lstar)) {
            // System.out.println("Final observation table:");
            // new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);
            // OTUtils.displayHTMLInBrowser(lstar.getObservationTable());
            // OTUtils.displayHTMLInBrowser(lstar.getHypothesisModel());
        }
    }
    

}
