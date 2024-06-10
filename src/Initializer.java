@FunctionalInterface
public interface Initializer<T> {
    /**
     * Initializes the given instance from method or constructor (depending on
     * type of ParsingFactory).
     * @param args arguments
     * @return instance
     * @throws ParsingException if failed to initialize object
     */
    T initialize(Object[] args) throws ParsingException;
}
