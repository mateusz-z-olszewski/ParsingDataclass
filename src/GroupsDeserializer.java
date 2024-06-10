@FunctionalInterface
public interface GroupsDeserializer {
    /**
     * Deserializes strings representing matches of a group into objects they represent. The
     * order of methods chosen to perform deserialization:
     * <ol>
     *  <li>Built-in deserializers (for primitive types, their boxed versions, and Strings),</li>
     *  <li>ParsingFactory of the given declared type (in a recursive manner).</li>
     * </ol>
     *
     * @param stringArgs groups of a match (excluding the 0th group being the whole match)
     * @return Objects deserialized
     * @throws ParsingException if pattern matched an incorrect number of times
     */
    Object[] deserialize(String[] stringArgs) throws ParsingException;
}
