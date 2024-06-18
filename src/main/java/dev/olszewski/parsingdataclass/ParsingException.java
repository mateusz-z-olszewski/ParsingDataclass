package dev.olszewski.parsingdataclass;

/**
 * The exception that can be thrown when an already existing ParsingFactory fails to
 * initialize a new object, for example because of type or arity mismatch between
 * the parsing method and given arguments.
 * This is not a runtime exception, because it can be expected that parsing an unknown
 * input fails.
 */
public class ParsingException extends RuntimeException {
    public ParsingException(String s){
        super(s);
    }
}
