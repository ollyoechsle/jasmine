package jasmine.javasource;

import java.util.Vector;
import java.io.File;

/**
 * Produces the source for a Java Class. Allow Java code to be produced
 * programmatically in an easy way without cluttering up code. Code produced
 * in this way is more likely to be syntactically correct.
 *
 * @author Olly Oechsle, University of Essex, Date: 04-Jun-2007
 * @version 1.0
 */
public class JavaClass {

    protected String className;
    protected String packageName;
    protected Vector<String> importList;
    public JavaDoc javadoc;
    protected Vector<String> implementsList;
    protected String extendsClass;
    protected Vector<JavaVariable> variables;
    protected Vector<JavaMethod> methods;

    /**
     * Constructs the class with the given className
     * @param className
     */
    public JavaClass(String className) {
        this.className = className;
        this.packageName = null;
        this.importList = new Vector<String>();
        this.javadoc = new JavaDoc();
        this.implementsList = new Vector<String>();
        this.extendsClass = null;
        this.variables = new Vector<JavaVariable>();
        this.methods = new Vector<JavaMethod>();
    }

    /**
     * Adds an import statement to the class
     * @param importStatement The fully qualified name of the class to import.
     */
    public void addImport(String importStatement) {
        importList.add(importStatement);
    }

    /**
     * Adds the name of an interface that this class implements.
     */
    public void addImplements(String interfaceName) {
        implementsList.add(interfaceName);
    }

    /**
     * Sets the package of this class
     */
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Sets which classname this class overrides.
     */
    public void setExtends(String classname) {
        this.extendsClass = classname;
    }

    /**
     * Adds a global member variable to the class.
     */
    public void addVariable(JavaVariable v) {
        this.variables.add(v);
    }

    public void addMethod(JavaMethod m) {
        this.methods.add(m);
    }

    /**
     * Saves the file to the given directory. The file is named {className}.java
     * @param directory The location to save the java source to.
     */
    public void save(File directory) {

    }

    public String toSource() {

        StringBuffer buffer = new StringBuffer();

        if (packageName != null) {
            buffer.append("package " + packageName + ";\n\n");
        }

        if (importList.size() > 0) {
            for (int i = 0; i < importList.size(); i++) {
                String importName = importList.elementAt(i);
                buffer.append("import " + importName + ";\n");
            }
            buffer.append("\n");
        }

        buffer.append(javadoc.toSource());
        
        buffer.append("public class ");
        buffer.append(className);
        buffer.append(" ");

        if (implementsList.size() > 0) {
            for (int i = 0; i < importList.size(); i++) {
                buffer.append(importList.elementAt(i));
                if (i < importList.size() - 1) {
                    buffer.append(", ");
                }
            }
            buffer.append(" ");
        }

        if (extendsClass != null) {
            buffer.append("extends " + extendsClass);
            buffer.append(" ");
        }

        buffer.append("{\n\n");

        if (variables.size() > 0) {
            for (int i = 0; i < variables.size(); i++) {
                JavaVariable v = variables.elementAt(i);
                buffer.append(v.toSource());
                buffer.append("\n");
            }
            buffer.append("\n");
        }

        if (methods.size() > 0) {
            for (int i = 0; i < methods.size(); i++) {
                JavaMethod method = methods.elementAt(i);
                buffer.append(method.toSource());
                buffer.append("\n");
            }
            buffer.append("\n");
        }

        buffer.append("\n}");

        return buffer.toString();

    }

}
