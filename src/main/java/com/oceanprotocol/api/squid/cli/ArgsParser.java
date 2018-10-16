package com.oceanprotocol.api.squid.cli;


import org.apache.commons.cli.*;
import org.apache.commons.exec.environment.EnvironmentUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Arguments parser class. Implemented to validate the input parameters
 * given by the user.
 * More information in builder method.
 */
public class ArgsParser {

    // List of available commands
    public enum commandOptions {register, get};

    private String command;
    private String url;
    private String did;

    private String evmUrl= "http://localhost:8545";
    private String secretStoreUrl= "http://localhost:8180";

    private static final String EVM_URL= "EVM_URL";
    private static final String SS_URL= "SECRET_STORE_URL";

    public ArgsParser() {}


    /**
     * Command-line parser method. The application should accept two parameters:
     * 	1. A command, which must be part of the commandOptions enum
     * 	2. An additional set of parameters
     *
     * @param args
     * @throws ParseException
     */
    public static ArgsParser builder(String[] args) throws ParseException {
        Options options=getDefaultOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("r")) {

            String url= null;
            if (cmd.hasOption("url")) {
                try {
                    url= cmd.getOptionValue("url");
                } catch (Exception ex)  {
                    throw new ParseException("Invalid url provided");
                }
                if (null == url)
                    throw new ParseException("Invalid url provided");
            }

            ArgsParser argsParser= new ArgsParser()
                    .setCommand(commandOptions.register.toString())
                    .setUrl(url);

            return argsParser;

        }   else if (cmd.hasOption("g"))    {

            String did= null;
            if (cmd.hasOption("did")) {
                try {
                    did= cmd.getOptionValue("did");
                } catch (Exception ex)  {
                    throw new ParseException("Invalid did provided");
                }
                if (null == did)
                    throw new ParseException("Invalid did provided");
            }

            ArgsParser argsParser= new ArgsParser()
                    .setCommand(commandOptions.register.toString())
                    .setDid(did);

            return argsParser;

        }

        throw new ParseException("Bad parameters used");
    }

    public ArgsParser registerEnvironmentVariables() throws IOException {
        Map<String, String> env = EnvironmentUtils.getProcEnvironment();

        if (env.containsKey(EVM_URL) && env.get(EVM_URL).length() > 0)
            this.evmUrl= env.get(EVM_URL);
        else
            throw new IOException("Unable to parse " + EVM_URL + " environment variable");

        if (env.containsKey(SS_URL) && env.get(SS_URL).length() > 0)
            this.secretStoreUrl= env.get(SS_URL);
        else
            throw new IOException("Unable to parse " + SS_URL + " environment variable");

        return this;
    }

    public static Options getDefaultOptions()   {
        Options options= new Options();

        options.addOption("r", "register", true, "register a new asset");
        options.addOption("g", "get", true, "get access to an existing asset");


        return options;
    }

    public ArgsParser setCommand(String command)    {
        this.command= command;
        return this;
    }

    public ArgsParser setUrl(String url)    {
        this.url= url;
        return this;
    }

    public ArgsParser setDid(String did)    {
        this.did= did;
        return this;
    }


    public String getCommand() {
        return command;
    }

    public String getUrl() {
        return url;
    }

    public String getDid() {
        return did;
    }

}
