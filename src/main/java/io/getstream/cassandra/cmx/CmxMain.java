package io.getstream.cassandra.cmx;

import io.getstream.cassandra.cmx.collectors.CompactionMetricsCollector;
import io.getstream.cassandra.cmx.collectors.MetricsCollector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.HashSet;

public class CmxMain {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption(OptionBuilder
                .withLongOpt("metric")
                .withDescription("Required. Metric: Latency, Compaction.")
                .isRequired(true)
                .hasArg()
                .create("m"));

        options.addOption(OptionBuilder
                .withLongOpt("keyspace")
                .withDescription("Required: keyspace")
                .isRequired(true)
                .hasArg()
                .create("k"));

        options.addOption(OptionBuilder
                .withLongOpt("n")
                .withDescription("RMI endpoint: ip:port. Default localhost:7199")
                .isRequired(false)
                .hasArg()
                .create("n"));

        options.addOption(OptionBuilder
                .withLongOpt("cf")
                .withDescription("Column families, comma separated")
                .withValueSeparator(',')
                .isRequired(false)
                .hasArgs()
                .create("c"));

        options.addOption(OptionBuilder
                .withLongOpt("time-interval")
                .withDescription("Polling interval (in seconds). Default: 60 seconds")
                .isRequired(false)
                .hasArg()
                .create("t"));

        options.addOption(OptionBuilder
                .withLongOpt("sort")
                .withDescription("Sort: OneMinuteRate, FiveMinuteRate, FifteenMinuteRate. Default: OneMinuteRate")
                .isRequired(false)
                .hasArg()
                .create("s"));

        options.addOption(OptionBuilder
                .withLongOpt("limit")
                .withDescription("Display only 'limit' results. Default: 10")
                .isRequired(false)
                .hasArg()
                .create("l"));

        try {
            CommandLine line = parser.parse(options, args);
            MetricsCollector collector = null;

            switch (line.getOptionValue("m")) {
                case "Latency":
                    collector = new CompactionMetricsCollector(
                            line.getOptionValue("k"),
                            line.hasOption("c") == true ? new HashSet<>(Arrays.asList(line.getOptionValues("c"))) : null,
                            line.hasOption("l") == true ? Integer.valueOf(line.getOptionValue("l")) : 10,
                            line.hasOption("s") == true ? line.getOptionValue("s") : "OneMinuteRate"
                    );
                    break;
                case "Compaction":
                    collector = new CompactionMetricsCollector(
                            line.getOptionValue("k"),
                            line.hasOption("c") == true ? new HashSet<>(Arrays.asList(line.getOptionValues("c"))) : null,
                            line.hasOption("l") == true ? Integer.valueOf(line.getOptionValue("l")) : 10,
                            line.hasOption("s") == true ? line.getOptionValue("s") : "OneMinuteRate"
                    );
                    break;
            }

            new CollectorMain(
                    collector,
                    line.hasOption("n") == true ? (String)line.getOptionValue("n") : "localhost:7199",
                    line.hasOption("t") == true ? Integer.valueOf(line.getOptionValue("t")) : 60
            );
        } catch(ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();

            String jarName = new java.io.File(CmxMain.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();

            formatter.printHelp("java -jar " + jarName + " -k <keyspace> -m <metric_name> [options]",
                    "Cassandra's command-line Metrics Collector\n", options, "");
        }
    }
}
