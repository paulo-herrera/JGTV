/*
 *  Copyright (C) 2020 Paulo A. Herrera <pauloa.herrera@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.iidp.jgtv.others;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/*
 * Simple class to store, parse, and retrieve command line options.
 */
public class CLIParser {
    private HashMap<String, Option> shortNames = new HashMap<String,Option>();
    private HashMap<String, Option> longNames = new HashMap<String,Option>();

    //private HashMap<String, String> map = new HashMap<String,String>();
    public final String usage;

    public CLIParser(String programName) {
        usage = programName + "[options]";
    }

    public static void listArgs(String[] args) {
        for(String arg: args) {
            System.out.println(arg);
        }
    }

    /** Parse arguments */
    public void parse(String[] args) throws Exception {
        // parse only if there are options
        if (args.length > 0) {
            if ( args[0].equals("-h") || args[0].equals("--help") ) {
                System.out.println(usage);
                printOptions();
                System.exit(0);
            } else {
                processArgs(args);
            }
        }
    }

    /**
     * Processes arguments and set new options values.
     *
     * @param args Command line arguments.
     * @throws Exception If it finds a bad formed option or a required option is missing.
     */
    private void processArgs (String[] args) throws Exception {
        // TODO: Should we return additional args?

        Option opt = null;
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];

            // Option
            if (arg.charAt(0) == '-') {
                String name = null;
                if (arg.charAt(1) != '-') {          // Short option
                    name = arg.substring(1);
                } else if (arg.charAt(1) == '-') {   // Long option
                    name = arg.substring(2);
                }

                // Check for "name=value" option
                if (name.contains("=")) {
                    String[] strs = name.split("=");
                    name = strs[0];
                    String value = strs[1];
                    opt = this.get(name);
                    opt.value(value);
                    opt = null;
                } else {
                    opt = this.get(name);
                    if (opt.isFlag) {
                        opt.switchFlag();
                        opt = null;
                        // check next option is
                        if (i < args.length - 1 && args[i+1].charAt(0) != '-') {
                            throw new Exception("Bad formed option: " + args[i+1]);
                        }
                    }
                }
            } else {
                if (opt == null) {
                    throw new Exception("Expected option, got value: " + arg);
                }
                opt.value(arg);
                opt = null;
            }
        }

        // CHECK IF ALL OPTIONS HAVE BEEN ASSIGNED VALUES
        for ( String key : shortNames.keySet()) {
            opt = shortNames.get(key);
            if (opt.value == null) {
                throw new Exception("Missing required option in arguments list. Option: " + opt.shortName);
            }
        }
        for ( String key : longNames.keySet()) {
            opt = longNames.get(key);
            if (opt.value == null) {
                throw new Exception("Missing required option in arguments list. Option: " + opt.longName);
            }
        }
    }

    public Option option() {
        return new Option(this);
    }

    public Option get(String optName) {
        if (shortNames.containsKey(optName)) {
            return shortNames.get(optName);
        } else if (longNames.containsKey(optName)) {
            return longNames.get(optName);
        } else {
            assert false: "Unknown option name: " + optName;
            return null;
        }
    }

    public void printOptions() {
        System.out.println("OPTIONS: ");
        for ( String key : shortNames.keySet()) {
            Option opt = shortNames.get(key);
            if ((opt.shortName != null) && (opt.longName == null)) {
                System.out.println(opt);
            }
        }

        for ( String key : longNames.keySet()) {
            Option opt = longNames.get(key);
            System.out.println(opt);
        }
    }

    private void registerLongName(Option opt) {
        assert (opt.longName != null);
        longNames.put(opt.longName, opt);
    }

    private void registerShortName(Option opt) {
        assert (opt.shortName != null);
        shortNames.put(opt.shortName, opt);
    }

    // ===================================
    // OPTION CLASS HELPER
    // ===================================
    public static final class Option {
        String shortName = null;
        String longName = null;
        String value = null;
        String type = null;
        String help = null;
        boolean isFlag = false;
        CLIParser parser = null;

        Option(CLIParser parser) {
            this.parser = parser;
        }

        /**
         * Sets long option name.
         *
         * @param name Long option name. It should be in the format "--long".
         * @return this option.
         */
        public Option longName(String name) {
            if (name.charAt(0) != '-' || name.charAt(1) != '-') {
                assert(false) : "Bad formed long option name";
            }
            longName = name.substring(2);
            parser.registerLongName(this);
            return this;
        }

        /**
         * Sets short option name.
         *
         * @param name Short option name. It should be in the format "-s".
         * @return this option.
         */
        public Option shortName(String name) {
            if (name.charAt(0) != '-') {
                assert(false) : "Bad formed short option name: " + name;
            }
            shortName = name.substring(1);
            parser.registerShortName(this);
            return this;
        }

        /** Sets option value */
        public Option value(String value) {
            this.value = value;
            return this;
        }

        /** Sets option help or description */
        public Option help(String help) {
            this.help = help;
            return this;
        }

        /**
         * Indicates that this option is a flag, i.e. a boolean option.
         *
         * If option is present in argument list, then its default value is negated.
         */
        public Option setAsFlag() {
            isFlag = true;
            return this;
        }

        /** Returns value of this option as a string. */
        public String asString() {
            return value;
        }

        /** Returns value of this option as an integer. */
        public int asInt() throws Exception {
            if (value == null) throw new Exception("Unset int option: " + shortName + "  " + longName);
            return Integer.valueOf(value);
        }

        /** Returns value of this option as a double. */
        public double asDouble() throws Exception {
            if (value == null) throw new Exception("Unset double option: " + shortName + "  " + longName);
            return Double.valueOf(value);
        }

        /** Returns value of this option as a boolean. */
        public boolean asBoolean() throws Exception {
            if (value == null) throw new Exception("Unset boolean option: " + shortName + "  " + longName);
            return Boolean.valueOf(value);
        }

        Option switchFlag() {
            assert (isFlag);
            boolean flag = Boolean.valueOf(value);
            value = Boolean.toString(!flag);
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);

            if (this.shortName != null) {
                //sb.append("-").append(this.shortName).append(" ");
                formatter.format("-%-3s ", shortName);
            } else {
                formatter.format("%-4s ", " ");
            }

            if (this.longName != null) {
                //sb.append("--").append(this.longName).append(" ");
                formatter.format("--%-10s ", longName);
            } else {
                formatter.format("%-12s ", " ");
            }

            if (this.help != null) {
                //sb.append(this.help);
                formatter.format("%-24s ", help);
            }

            if (this.value != null) {
                sb.append("  [DEFAULT=").append(this.value).append("] ");
            } else {
                sb.append("  [DEFAULT=").append(" ").append("] ");
            }

            return sb.toString();
        }
    }

    // TESTS
    private static void test1(String[] args) throws Exception {
        CLIParser cli = new CLIParser("");
        cli.option().shortName("-i").longName("--input").value("/Users/paulo/input").help("Input path");
        cli.option().longName("--hdf5").value("true").help("Create HDF5 file").setAsFlag();
        cli.option().shortName("-v").value("True").help("Verbose output").setAsFlag();
        cli.option().shortName("-t").value("0.5").help("Time step");
        cli.printOptions();

        String [] args2 = new String[]{"--hdf5", "-v", "-i", "/root", "-t=0.2"};
        cli.processArgs(args2);
        System.out.println("After parsing.." );
        cli.printOptions();
    }

    public static void main(String[] args) throws Exception {
        test1(args);
    }

}