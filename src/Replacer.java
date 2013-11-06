/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 11/5/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Replacer {
    private String find, replace;

    public Replacer(String toFind, String replaceWith) {
        find = toFind;
        replace = replaceWith;
    }

    public String apply(String input) {
        return input.replace(find, replace);
    }
}
