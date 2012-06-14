package jasmine.gp.util;

public interface Deployable {

    /**
     * Gets the template for the java code. Must include a placeholder called CLASSNAME
     * and one called CODE. Code doesn't need a method signature.
     * @return
     */
    public String getJavaTemplate();

    public String getCode();

}
