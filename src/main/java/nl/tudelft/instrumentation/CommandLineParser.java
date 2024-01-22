package nl.tudelft.instrumentation;

import com.github.javaparser.ast.visitor.GenericVisitor;
import nl.tudelft.instrumentation.branch.BranchCoverageVisitor;
import nl.tudelft.instrumentation.line.LineCoverageVisitor;
import nl.tudelft.instrumentation.patching.OperatorVisitor;
import nl.tudelft.instrumentation.concolic.PathVisitor;
import nl.tudelft.instrumentation.fuzzing.DistanceVisitor;
import nl.tudelft.instrumentation.learning.MembershipVisitor;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;

public class CommandLineParser {
    private GenericVisitor visitor;
    private File javaFile;

    /**
     * Parses application arguments
     *
     * @param args application arguments
     * @return <code>CommandLine</code> which represents a list of application
     * arguments.
     */
    private CommandLine parseArguments(String[] args) {
        Options options = getOptions();
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();

        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException ex) {
            System.err.println("Failed to parse command line arguments");
            System.err.println(ex.toString());
            printAppHelp();
            System.exit(1);
        }

        return line;
    }

    /**
     * Generates application command line options
     * @return application <code>Options</code>
     */
    private Options getOptions() {
        Options options = new Options();
        options.addOption("t", "type", true, "type of instrumentation");
        options.addOption("f", "file", true, "Java file to instrument");
        return options;
    }

    /**
     * Parses the arguments from a given command.
     * @param args the arguments.
     * @throws FileNotFoundException throws a FileNotFoundException if we cannot find the file that was given
     *                               as an argument.
     */
    public void parseCommandLine(String[] args) throws FileNotFoundException {
        CommandLine line = parseArguments(args);

        if (!line.hasOption("type") || !line.hasOption("file")){
            printAppHelp();
            throw new IllegalArgumentException();
        }
        String filename = line.getOptionValue("file");
        File file = new File(filename);
        if (!file.exists()) {
            String msg = "The following file does not exist " + file.getName();
            throw new FileNotFoundException(msg);
        } else {
            this.javaFile = file;
        }

        String type = line.getOptionValue("type");

        // Parses which type of instrumentation we should be doing.
        switch(type){
            case "line":
                visitor = new LineCoverageVisitor(file.getAbsolutePath());
                break;
            case "branch":
                visitor = new BranchCoverageVisitor(file.getAbsolutePath());
                break;
            case "fuzzing":
                visitor = new DistanceVisitor(file.getAbsolutePath());
                break;
            case "concolic":
                visitor = new PathVisitor(file.getAbsolutePath());
                break;
            case "patching":
                visitor = new OperatorVisitor(file.getAbsolutePath());
                break;
            case "learning":
                visitor = new MembershipVisitor();
                break;
            default:
                throw new IllegalArgumentException("Only six available types: \"branch\" , \"line\" , \"fuzzing\" , \"concolic\", \"patching\", \"learning\"");
        }
    }

    /**
     * Prints application help
     */
    private void printAppHelp() {
        Options options = getOptions();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("nl.tudelft.instrumentation.Main", options, true);
    }

    public GenericVisitor getVisitor() {
        return visitor;
    }

    public File getJavaFile() {
        return javaFile;
    }
}
