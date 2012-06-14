package jasmine.gp.util;


import jasmine.imaging.commons.util.FileIO;

import javax.swing.*;
import javax.tools.Tool;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Used to deploy code by compiling it into Java.
 */
public class Deployer {

    public static String classpathSwitch = "-classpath";

    /**
     * Compiles an evolved segmenter into bytecode
     *
     * @param object     The object to be deployed
     * @param sourceFile A file where the source will be saved to
     * @return The a reference to the class file
     */
    public File deploy(Deployable object, File sourceFile) throws Exception {

        // create the java code
        String java = object.getJavaTemplate();

        String methodSignature = "\npublic int segment(PixelLoader image, int x, int y)";
        String className = sourceFile.getName();
        int dotIndex = className.indexOf(".");
        if (dotIndex > -1) {
            className = className.substring(0, dotIndex);
        }

        // remove hyphens or spaces
        className = className.replaceAll(" ", "_");
        className = className.replaceAll("-", "_");

        File saveFolder = sourceFile.getParentFile();
        File javaFile = new File(saveFolder, className + ".java");
        File classFile = new File(saveFolder, className + ".class");
        java = java.replaceFirst("CLASSNAME", className);
        String code = object.getCode();
        java = java.replaceFirst("CODE", code);

        while (true) {
            String error = compile(javaFile, java, classpathSwitch);

            if (error == null) {
                // ensure that the class can be instantiated
                instantiateClass(classFile);
                return classFile;
            } else {
                if (confirm("I could not compile the code. Edit the source manually?\nThe error was:\n" + error)) {
                    try {
                        if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
                            // open in explorer
                            Runtime.getRuntime().exec("notepad.exe " + javaFile.getAbsolutePath());
                        } else {
                            // for mac
                            Runtime.getRuntime().exec("open " + sourceFile.getAbsolutePath());
                        }

                        if (!confirm("Attempt to recompile?")) {
                            break;
                        }

                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                } else {
                    break;
                }

            }
        }

        return null;

    }

    public void alert(String message) {
        System.out.println("ALERT: " + message);
    }

    public void setStatusText(String message) {
        System.out.println(message);
    }

    public boolean confirm(String message) {
        int response = JOptionPane.showConfirmDialog(null, message);
        return response == JOptionPane.YES_OPTION;
    }

    public File[] getJars() {
        return new File[]{};
    }

    public String compile(File javaFile, String java, String classpathSwitch) throws IOException {

        if (!javaFile.exists() || confirm("A source file with this name (" + javaFile.getName() + ") already exists. Replace?")) {
            try {
                // save the java to a file
                FileIO.saveToFile(java, javaFile);
            } catch (IOException e) {
                alert("Cannot save java source file! " + javaFile.getAbsolutePath());
                return "Cannot save file";
            }
        }

        StringBuffer output = new StringBuffer();

        File[] jars = getJars();
        if (jars == null) {
            setStatusText("Possibly missing required libraries");
            jars = new File[]{};
        }


        Process proc;

        try {

            // start the ls command running
            Runtime runtime = Runtime.getRuntime();
            StringBuffer command = new StringBuffer("javac ");
            if (jars.length > 0) {
                command.append(classpathSwitch);
                command.append(" \"");
                for (int i = 0; i < jars.length; i++) {
                    File jar = jars[i];
                    command.append(jar.getAbsolutePath());
                    if (i < jars.length - 1) command.append(";");
                }
                command.append("\" ");
            }
            command.append("\"");
            command.append(javaFile.getAbsolutePath());
            command.append("\"");

            System.out.println(command.toString());
            proc = runtime.exec(command.toString());

            // put a BufferedReader on the command output
            InputStream inputstream = proc.getErrorStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            // read the javac output
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                output.append(line);
                output.append("\n");
            }
        } catch (IOException e) {
            String message = e.getMessage();
            if (message.contains("javac")) {
                message = "It appears you don't have the Java JDK installed on your system. Please install it first in order to compile Java programs.";
            } else {
                e.printStackTrace();
            }
            alert(message);
            return e.getMessage();
        }

        // check for compile failure
        try {
            if (proc.waitFor() == 0) {
                alert("Saved compiled file to: " + javaFile.getAbsolutePath().replaceFirst(".java", ".class"));
                // everything is OK
                return null;
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return e.getMessage();
        }


        return output.toString();

    }

    protected static Object instantiateClass(File classFile) throws Exception {

        if (classFile.getName().endsWith(".class")) {

            String className = classFile.getName();
            // remove ".class" from the end of the name
            className = className.substring(0, className.length() - 6);

            // Create a File object on the root of the directory containing the class file
            File file = classFile.getParentFile();

            // Convert File to a URL
            URL url = file.toURL();          // file:/c:/myclasses/
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            //ClassLoader cl = new URLClassLoader(urls);

            ClassLoader wcl = Tool.class.getClassLoader();
            ClassLoader cl;
            if (wcl == null) {
                cl = new URLClassLoader(urls);
            } else {
                // for Java Web Start
                cl = new URLClassLoader(urls, wcl);
            }

            // Load in the class; MyClass.class should be located in
            Class cls = cl.loadClass(className);

            return cls.newInstance();


        } else {

            FileInputStream fis = new FileInputStream(classFile);
            ObjectInputStream in = new ObjectInputStream(fis);
            Object o = in.readObject();
            in.close();
            return o;

        }


    }


}
