package com.oceanprotocol.squid;

import com.oceanprotocol.squid.cli.ArgsParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CLI {

    static final Logger log= LogManager.getLogger(CLI.class);

    static final String CLI_CMD= "java -jar target/squid*.jar CLI ";

    /**
     * Squid CLI tool.
     * It supports the following command:
     * - register. Given a URL the tool register the URL on-chain
     * - get. Given a DID the tool get access to an asset
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ArgsParser argsParser= null;

        try {
            log.debug("Parsing input parameters ");
            argsParser= ArgsParser.builder(args);
            argsParser.registerEnvironmentVariables();

        } catch (ParseException ex)	{
            log.error("Unable to parse arguments");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CLI_CMD, ArgsParser.getDefaultOptions());
            System.exit(1);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            log.error("Using default values");
        }

        if (argsParser.getCommand().equals(ArgsParser.commandOptions.register.toString())) {
            log.debug("Registering a new AssetMetadata!");

            //PublisherWorker worker= new PublisherWorker('m ')

        } else if (argsParser.getCommand().equals(ArgsParser.commandOptions.get.toString())) {
            log.debug("Getting an existing AssetMetadata!");

        } else	{
            log.error("Unable to parse arguments");
            System.exit(1);
        }

    }

}
