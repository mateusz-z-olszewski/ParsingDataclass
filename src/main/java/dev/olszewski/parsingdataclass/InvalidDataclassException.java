package dev.olszewski.parsingdataclass;

/**
 * Exception that is being raised during ParsingFactory initialization. It can be raised,
 * for example, when the given class has a number of @Parses/@ParsingDataclass annotations
 * different from one.
 * It is a RuntimeException because the class being passed to the factory is usually a constant
 * and therefore can be checked by the developer to be valid.
 */
public class InvalidDataclassException extends IllegalArgumentException  {
    /**
     * Constructor for InvalidDataclassException.
     * @param s error message
     */
    public InvalidDataclassException(String s) {
        super(s);
    }
}
