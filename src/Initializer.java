@FunctionalInterface
public interface Initializer<T> {
    T initialize(Object[] argGroups) throws ParsingException;
}
