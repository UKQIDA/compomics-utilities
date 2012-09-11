package com.compomics.software;

import com.compomics.util.gui.UtilitiesGUIDefaults;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import java.io.*;
import javax.swing.JOptionPane;

/**
 * A general wrapper for compomics tools. All tools shall contain a
 * resources/conf folder. In it a JavaOptions.txt and splash screen. Eventually
 * JavaHome.txt and proxy/uniprotjapi.properties.
 *
 * @author Marc Vaudel
 */
public class CompomicsWrapper {

    /**
     * If set to true debug output will be written to the screen and to
     * startup.log.
     */
    private boolean useStartUpLog = true;
    /**
     * Writes the debug output to startup.log.
     */
    private BufferedWriter bw = null;
    /**
     * True if this the first time the wrapper tries to launch the application.
     * If the first launch fails, e.g., due to memory settings, it is set to
     * false.
     */
    private boolean firstTry = true;
    /**
     * Is set to true if proxy settings are found in the JavaOptions file.
     */
    private boolean proxySettingsFound = false;
    /**
     * The user preferences.
     */
    private UtilitiesUserPreferences userPreferences;

    /**
     * Constructor.
     */
    public CompomicsWrapper() {
    }

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param toolName 
     * @param jarFile the jar file to execute
     * @param splashName the splash name, for example peptide-shaker-splash.png
     * @param mainClass the main class to execute, for example
     * eu.isas.peptideshaker.gui.PeptideShakerGUI
     */
    public void launchTool(String toolName, File jarFile, String splashName, String mainClass) {

        try {
            try {
                userPreferences = UtilitiesUserPreferences.loadUserPreferences();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (useStartUpLog) {
                File folder = new File(jarFile.getParentFile(), "resources/conf");
                if (!folder.exists()) {
                    throw new FileNotFoundException("'resources/conf' folder not found.");
                }
                File debugOutput = new File(folder, "startup.log");
                bw = new BufferedWriter(new FileWriter(debugOutput));
                bw.write("Memory settings read from the user preferences: " + userPreferences.getMemoryPreference() + System.getProperty("line.separator"));
            }

            try {
                UtilitiesGUIDefaults.setLookAndFeel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                // perhaps not the optimal way of catching this error, but seems to work
                JOptionPane.showMessageDialog(null,
                        "Seems like you are trying to start " + toolName + " from within a zip file!",
                        toolName + " - Startup Failed", JOptionPane.ERROR_MESSAGE);
            }
            launch(jarFile, splashName, mainClass);

            if (useStartUpLog) {
                bw.flush();
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null,
                    "Failed to start " + toolName + ":" + System.getProperty("line.separator")
                    + e.getMessage(),
                    toolName + " - Startup Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Launches the jar file with parameters to the jvm.
     *
     * @throws java.lang.Exception
     * @param jarFile the jar file to execute
     * @param splashName the splash name, for example peptide-shaker-splash.png
     * @param mainClass the main class to execute, for example
     * eu.isas.peptideshaker.gui.PeptideShakerGUI
     */
    private void launch(File jarFile, String splashName, String mainClass) throws Exception {

        String temp = "", cmdLine;
        String options = "", currentOption;

        File confFolder = new File(jarFile.getParentFile(), "resources/conf");
        if (!confFolder.exists()) {
            throw new FileNotFoundException("'resources/conf' folder not found.");
        }
        File javaOptions = new File(confFolder, "JavaOptions.txt");
        File nonStandardJavaHome = new File(confFolder, "JavaHome.txt");

        File uniprotApiPropertiesFile = new File(confFolder, "proxy/uniprotjapi.properties");
        String uniprotApiProperties = "";

        // read any java option settings
        if (javaOptions.exists()) {

            try {
                FileReader f = new FileReader(javaOptions);
                BufferedReader b = new BufferedReader(f);

                currentOption = b.readLine();

                while (currentOption != null) {
                    if (currentOption.startsWith("-Xmx")) {
                        if (firstTry) {
                            currentOption = currentOption.substring(4, currentOption.length() - 1);
                            boolean input = false;
                            for (char c : currentOption.toCharArray()) {
                                if (c != '*') {
                                    input = true;
                                    break;
                                }
                            }
                            if (input) {
                                try {
                                    userPreferences.setMemoryPreference(new Integer(currentOption));
                                    saveNewSettings(jarFile);
                                    if (useStartUpLog) {
                                        bw.write("New memory setting saved: " + userPreferences.getMemoryPreference() + System.getProperty("line.separator"));
                                    }
                                } catch (Exception e) {

                                    javax.swing.JOptionPane.showMessageDialog(null,
                                            "Could not parse the memory setting:" + currentOption
                                            + ". The value was reset to" + userPreferences.getMemoryPreference() + ".",
                                            "Wrong memory settings", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        }
                    } else if (!currentOption.startsWith("#")) {

                        // extract the proxy settings as these are needed for uniprotjapi.properties
                        if (currentOption.startsWith("-Dhttp")) {

                            proxySettingsFound = true;
                            String[] tempProxySetting = currentOption.split("=");

                            if (tempProxySetting[0].equalsIgnoreCase("-Dhttp.proxyHost")) { // proxy host
                                uniprotApiProperties += "proxy.host=" + tempProxySetting[1] + System.getProperty("line.separator");
                            } else if (tempProxySetting[0].equalsIgnoreCase("-Dhttp.proxyPort")) { // proxy port
                                uniprotApiProperties += "proxy.port=" + tempProxySetting[1] + System.getProperty("line.separator");
                            } else if (tempProxySetting[0].equalsIgnoreCase("-Dhttp.proxyUser")) { // proxy user name
                                uniprotApiProperties += "username=" + tempProxySetting[1] + System.getProperty("line.separator");
                            } else if (tempProxySetting[0].equalsIgnoreCase("-Dhttp.proxyPassword")) { // proxy password
                                uniprotApiProperties += "password=" + tempProxySetting[1] + System.getProperty("line.separator");
                            }
                        }

                        options += currentOption + " ";
                    }
                    currentOption = b.readLine();
                }

                // create the uniprot japi proxy settings file
                if (proxySettingsFound) {
                    FileWriter uniprotProxyWriter = new FileWriter(uniprotApiPropertiesFile);
                    BufferedWriter uniprotProxyBufferedWriter = new BufferedWriter(uniprotProxyWriter);
                    uniprotProxyBufferedWriter.write(uniprotApiProperties);
                    uniprotProxyBufferedWriter.close();
                    uniprotProxyWriter.close();
                }

                b.close();
                f.close();

                options += "-Xmx" + userPreferences.getMemoryPreference() + "M";

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                if (useStartUpLog) {
                    bw.write(ex.getMessage());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                if (useStartUpLog) {
                    bw.write(ex.getMessage());
                }
            }
        } else {
            options = "-Xms128M -Xmx1024M";
        }

        // get the default java home location
        String javaHome = System.getProperty("java.home") + File.separator
                + "bin" + File.separator;

        // check if the user has set a non-standard Java home location
        boolean usingStandardJavaHome = true;

        if (nonStandardJavaHome.exists()) {

            try {
                FileReader f = new FileReader(nonStandardJavaHome);
                BufferedReader b = new BufferedReader(f);

                String tempLocation = b.readLine();

                if (new File(tempLocation).exists()
                        && (new File(tempLocation, "java.exe").exists() || new File(tempLocation, "java").exists())) {
                    javaHome = tempLocation;
                    usingStandardJavaHome = false;
                } else {
                    if (firstTry) {
                        JOptionPane.showMessageDialog(null, "Non-standard Java home location not found.\n"
                                + "Using default Java home.", "Java Home Not Found!", JOptionPane.WARNING_MESSAGE);
                    }
                }

                b.close();
                f.close();

            } catch (FileNotFoundException ex) {
                if (firstTry) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Non-standard Java home location not found.\n"
                            + "Using default Java home", "Java Home Not Found!", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {

                if (firstTry) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error when reading non-standard Java home location.\n"
                            + "Using default Java home.", "Java Home Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        // set up the quote type, windows or linux/mac
        String quote = "";

        if (System.getProperty("os.name").lastIndexOf("Windows") != -1) { // @TODO: no quotes on mac/linux?
            quote = "\"";
        }

        if (useStartUpLog) {
            bw.write("original java.home: " + javaHome + System.getProperty("line.separator"));
        }

        // try to force the use of 64 bit Java if available
        if (usingStandardJavaHome && javaHome.lastIndexOf(" (x86)") != -1 && System.getProperty("os.name").lastIndexOf("Windows") != -1) {

            // default java 32 bit windows home looks like this:    C:\Program Files (x86)\Java\jre6\bin\javaw.exe
            // default java 64 bit windows home looks like this:    C:\Program Files\Java\jre6\bin\javaw.exe

            String tempJavaHome = javaHome.replaceAll(" \\(x86\\)", "");

            if (useStartUpLog) {
                bw.write("temp java.home: " + tempJavaHome + System.getProperty("line.separator"));
            }

            if (new File(tempJavaHome).exists()) {
                javaHome = tempJavaHome;
            }
        }

        if (useStartUpLog) {
            bw.write("new java.home: " + javaHome + System.getProperty("line.separator"));
        }

        // get the splash 
        String splashPath = confFolder.getAbsolutePath() + File.separator + splashName;

        // set the correct slashes for the splash path
        if (System.getProperty("os.name").lastIndexOf("Windows") != -1) {
            splashPath = splashPath.replace("/", "\\");

            // remove the initial '\' at the start of the line 
            if (splashPath.startsWith("\\") && !splashPath.startsWith("\\\\")) {
                splashPath = splashPath.substring(1);
            }
        }

        String uniprotProxyClassPath = "";

        // add the classpath for the uniprot proxy file
        if (proxySettingsFound) {
            uniprotProxyClassPath = confFolder.getAbsolutePath() + File.separator + "proxy";

            // set the correct slashes for the proxy path
            if (System.getProperty("os.name").lastIndexOf("Windows") != -1) {
                uniprotProxyClassPath = uniprotProxyClassPath.replace("/", "\\");

                // remove the initial '\' at the start of the line 
                if (uniprotProxyClassPath.startsWith("\\") && !uniprotProxyClassPath.startsWith("\\\\")) {
                    uniprotProxyClassPath = uniprotProxyClassPath.substring(1);
                }
            }

            uniprotProxyClassPath = ";" + quote + uniprotProxyClassPath + quote;
        }

        // create the complete command line
        cmdLine = javaHome + "java -splash:" + quote + splashPath + quote + " " + options + " -cp "
                + quote + jarFile.getAbsolutePath() + quote + uniprotProxyClassPath
                + " " + mainClass;

        if (useStartUpLog) {
            System.out.println(System.getProperty("line.separator") + cmdLine + System.getProperty("line.separator") + System.getProperty("line.separator"));
            bw.write(System.getProperty("line.separator") + "Command line: " + cmdLine + System.getProperty("line.separator") + System.getProperty("line.separator"));
        }

        // try to run the command line
        try {
            Process p = Runtime.getRuntime().exec(cmdLine);

            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);

            String line = br.readLine();

            boolean error = false;

            while (line != null) {

                if (useStartUpLog) {
                    System.out.println(line);
                    bw.write(line + System.getProperty("line.separator"));
                }

                temp += line + System.getProperty("line.separator");
                line = br.readLine();
                error = true;
            }

            int exitVal = p.waitFor();

            if (useStartUpLog) {
                System.out.println("Process exitValue: " + exitVal);
                bw.write("Process exitValue: " + exitVal + System.getProperty("line.separator"));
            }

            // an error occured
            if (error) {

                firstTry = false;
                temp = temp.toLowerCase();

                // if needed, try re-launching with reduced memory settings
                if (temp.contains("could not create the java virtual machine")) {
                    if (userPreferences.getMemoryPreference() > 3 * 1024) {
                        userPreferences.setMemoryPreference(userPreferences.getMemoryPreference() - 1024);
                        saveNewSettings(jarFile);
                        launch(jarFile, splashName, mainClass);
                    } else if (userPreferences.getMemoryPreference() > 1024) {
                        userPreferences.setMemoryPreference(userPreferences.getMemoryPreference() - 512);
                        saveNewSettings(jarFile);
                        launch(jarFile, splashName, mainClass);
                    } else {
                        if (useStartUpLog) {
                            bw.write("Memory Limit:" + userPreferences.getMemoryPreference() + System.getProperty("line.separator"));
                            bw.flush();
                            bw.close();
                        }

                        javax.swing.JOptionPane.showMessageDialog(null,
                                "Failed to create the Java virtual machine.\n\n"
                                + "Inspect the log file for details: resources/conf/startup.log.\n\n"
                                + "Then go to Troubleshooting at http://", //@TODO move help to tool independent website
                                "Startup Failed", JOptionPane.ERROR_MESSAGE);

                        System.exit(0);
                    }
                } else {

                    if (useStartUpLog) {
                        bw.flush();
                        bw.close();
                    }

                    if (temp.lastIndexOf("NoClassDefFound") != -1) {
                        JOptionPane.showMessageDialog(null,
                                "Seems like you are trying to start the tool from within a zip file!",
                                "Sartup Failed", JOptionPane.ERROR_MESSAGE);
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(null,
                                "An error occurred when starting the tool.\n\n"
                                + "Inspect the log file for details: resources/conf/startup.log.\n\n"
                                + "Then go to Troubleshooting at http://", //@TODO move help to tool independent website
                                "Startup Error", JOptionPane.ERROR_MESSAGE);
                    }

                    System.exit(0);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Saves the new memory settings.
     */
    private void saveNewSettings(File jarFile) throws FileNotFoundException {
        try {
            UtilitiesUserPreferences.saveUserPreferences(userPreferences);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveJavaOptions(jarFile);
    }

    /**
     * Creates a new javaOptions text file with the new settings.
     */
    private void saveJavaOptions(File jarFile) throws FileNotFoundException {

        String currentLine, lines = "";

        File confFolder = new File(jarFile.getParentFile(), "resources/conf");
        if (!confFolder.exists()) {
            throw new FileNotFoundException("'resources/conf' folder not found.");
        }
        File javaOptions = new File(confFolder, "JavaOptions.txt");

        // read any java option settings
        if (javaOptions.exists()) {

            try {
                FileReader f = new FileReader(javaOptions);
                BufferedReader b = new BufferedReader(f);

                while ((currentLine = b.readLine()) != null) {
                    if (!currentLine.startsWith("-Xmx")) {
                        lines += currentLine + System.getProperty("line.separator");
                    }
                }
                b.close();
                f.close();

                FileWriter fw = new FileWriter(javaOptions);
                BufferedWriter bow = new BufferedWriter(fw);
                bow.write(lines);
                bow.write("-Xmx" + userPreferences.getMemoryPreference() + "M" + System.getProperty("line.separator"));

                bow.close();
                fw.close();

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}