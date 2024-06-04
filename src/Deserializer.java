public interface Deserializer {
    Object[] deserialize(String[] stringArgs) throws ParsingException;
}
