package dev.olszewski.parsingdataclass;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This class can parse from a string instances of class {@code T}.
 * There are few possible places where class' regex can be declared so that it can be parsed:
 * <ul>
 *     <li>{@code T} is a record and it is annotated with {@link ParsingDataclass}.</li>
 *     <li>{@code T} is a class and it is annotated with @{@link ParsingDataclass}. This class must have a constructor
 *     which takes no arguments. Instances are initialized using the default constructor, then fields are set using
 *     reflection. Fields which should not be set during parsing can be annotated with @{@link NotParsed}.</li>
 *     <li>{@code T} is a class and there is a method returning {@code T}, annotated with @{@link Parses}, present.
 *     It may be either a constructor or a static factory method.</li>
 * </ul>
 * There should never be present at the same time more than one annotation which declares the
 * pattern (@{@link ParsingDataclass} or @{@link Parses}) in one class due to possible ambiguity.
 *
 * @param <T>
 */
abstract public sealed class ParsingFactory<T>
        permits ParsingConstructorFactory, ParsingDataclassFactory, ParsingMethodFactory, ParsingRecordFactory {
    protected final Class<? extends T> cls;
    protected final ParsingFactory<?>[] parsingFactories;
    protected final Class<?>[] types;
    protected final Pattern pattern;


    private final static HashMap<Class<?>, ParsingFactory<?>> instances
            = new HashMap<>();
    protected final static HashMap<Class<?>, Function<String, Object>> builtinDeserializers
            = new HashMap<>();

    static {
        initializeBuiltinDeserializers();
    }

    private static void initializeBuiltinDeserializers() {
        // initialize resolving for builtins
        builtinDeserializers.put(String.class, x -> x);
        builtinDeserializers.put(float.class, Float::valueOf);
        builtinDeserializers.put(Float.class, Float::valueOf);
        builtinDeserializers.put(double.class, Double::valueOf);
        builtinDeserializers.put(Double.class, Double::valueOf);
        builtinDeserializers.put(int.class, Integer::valueOf);
        builtinDeserializers.put(Integer.class, Integer::valueOf);
        builtinDeserializers.put(short.class, Short::valueOf);
        builtinDeserializers.put(Short.class, Short::valueOf);
        builtinDeserializers.put(boolean.class, Boolean::valueOf);
        builtinDeserializers.put(Boolean.class, Boolean::valueOf);
    }

    /**
     * Returns a ParsingFactory instance of the given class. Caches already instantiated instances
     * to speed up the process of creation. To avoid caching, use
     * {@link ParsingFactory#createParsingFactoryOf(Class)}
     *
     * @param cls Class to be parsed. Needs to adhere to rules for parsing dataclasses (see documentation).
     * @return Parsing factory of the given type
     */
    @SuppressWarnings("unchecked")
    public static synchronized <U> ParsingFactory<U> of(Class<U> cls) {
        if (instances.containsKey(cls))
            return (ParsingFactory<U>) instances.get(cls);
        else {
            ParsingFactory<U> out = createParsingFactoryOf(cls);
            instances.put(cls, out);
            return out;
        }
    }
    // todo: make so that deserializers of non-caching parsingFactories do not
    //  create parsing subfactories using caching.

    /**
     * Uses RegEx to transform the given string into an instance of the class being parsed
     * using capturing groups.
     *
     * @param string string to be parsed
     * @return instance of {@code T} created from the given string, or {@code null} if
     * the pattern failed to match, or input is null itself.
     * @throws ParsingException if the object could not have been instantiated, or pattern matched
     *                          an incorrect number of times.
     */
    public T parse(String string) {
        if(string == null) return null;
        Matcher m = this.pattern.matcher(string);
        if (!m.matches()) return null;
        int groups = m.groupCount();
        // create array of arguments that will be used to
        // instantiate an instance (from match groups).
        // important: skips empty groups.
        String[] strings = new String[groups];
        int s = 0;
        for (int i = 1; i <= groups; i++) {
            String stringArg = m.group(i);
            if(stringArg != null) strings[s++] = m.group(i);
        }
        String[] stringArgs = new String[s];
        System.arraycopy(strings, 0, stringArgs, 0, s);
        // deserialize groups into Objects
        Object[] args = deserializeGroups(stringArgs);
        // initialize instance using the given arguments (now deserialized into Objects)
        return instantiate(args);
    }

    /**
     * Parses multiple strings at the same time.
     *
     * @param strings String instances to be parsed.
     * @return Array of parsed objects. Returns null/empty if is given collection is null/empty respectively.
     * @throws ParsingException if any of the objects could not have been instantiated.
     */
    public T[] parse(Collection<String> strings) throws ParsingException {
        if (strings == null) return null;
        return parse(strings.toArray(String[]::new));
    }

    /**
     * Parses multiple strings at the same time.
     *
     * @param strings String instances to be parsed.
     * @return Array of parsed objects
     * @throws ParsingException if any of the objects could not have been instantiated.
     */
    @SuppressWarnings("unchecked")
    public T[] parse(String... strings) throws ParsingException {
        T[] out = (T[]) Array.newInstance(cls, strings.length);
        for (int i = 0; i < strings.length; i++) {
            out[i] = parse(strings[i]);
        }
        return out;
    }

    /**
     * Creates a new parsing factory of the given class, without caching.
     *
     * @param cls Class to be parsed. Needs to adhere to rules for parsing dataclasses (see documentation).
     * @return Parsing factory of the given type
     */
    public static <U> ParsingFactory<U> createParsingFactoryOf(Class<U> cls) {
        if (cls.isRecord())
            return new ParsingRecordFactory<>(cls);

        // Not a single person is going to casually create a hidden class and pass it to the
        // .of method, and if they did, it's totally on them.
        // argumentsAssert(!cls.isHidden(),
        //         "The given class is hidden.");
        argumentsAssert(!Modifier.isAbstract(cls.getModifiers()),
                "The given class is abstract.");

        ParsingDataclass ann = findParsingDataclassAnnotation(cls);
        Executable executable = findParsingMethod(cls);

        argumentsAssert(ann != null || executable != null,
                "The given class contains neither @dev.olszewski.parsingdataclass.ParsingDataclass annotation " +
                        "nor an executable with @dev.olszewski.parsingdataclass.Parses annotation.");
        argumentsAssert(ann == null || executable == null,
                "The given class contains both @dev.olszewski.parsingdataclass.ParsingDataclass annotation " +
                        "and am executable with @dev.olszewski.parsingdataclass.Parses annotation.");

        if (ann != null)
            return new ParsingDataclassFactory<>(cls, Pattern.compile(ann.value()));
        else if(executable instanceof Constructor)
            //noinspection unchecked
            return new ParsingConstructorFactory<>(cls, (Constructor<U>)executable);
        else
            return new ParsingMethodFactory<>(cls, (Method) executable);
    }

    protected ParsingFactory(Class<? extends T> cls, Pattern pattern, Class<?>[] types) {
        this.cls = cls;
        this.pattern = pattern;
        this.types = types;

        parsingFactories = new ParsingFactory[types.length];
        for (int i = 0; i < parsingFactories.length; i++) {
            Class<?> type = types[i];
            if(builtinDeserializers.containsKey(type))
                continue;
            parsingFactories[i] = ParsingFactory.of(type);
        }
    }

    /**
     * Finds the sole method annotated with @Parses inside the given class.
     *
     * @return the method that is searched for, or null if one does not exist.
     * @throws InvalidDataclassException if there is more than one such method.
     */
    private static Executable findParsingMethod(Class<?> cls) {
        var stream = Stream.concat(
                Arrays.stream(cls.getDeclaredMethods()),
                Arrays.stream(cls.getDeclaredConstructors())
        );
        var methods = stream
                .filter(e -> e.getAnnotation(Parses.class) != null)
                .toArray(Executable[]::new);
        if (methods.length == 0) return null;
        argumentsAssert(methods.length == 1, "The given class " + cls.getCanonicalName()
                + " contains more than one method with annotation @dev.olszewski.parsingdataclass.Parses.");
        return methods[0];
    }

    /**
     * Finds the {@code @ParsingDataclass} annotation of the given class.
     *
     * @return the annotation that is searched for or null if one does not exist.
     */
    private static ParsingDataclass findParsingDataclassAnnotation(Class<?> cls) {
        return cls.getAnnotation(ParsingDataclass.class);
    }

    /**
     * More readable shortcut for {@code if(!b) throw new InvalidDataClassException(s); }.
     * To be used regarding parsing arguments.
     */
    protected static void argumentsAssert(boolean b, String s) throws InvalidDataclassException {
        if (!b) throw new InvalidDataclassException(s);
    }

    /**
     * Deserializes strings representing matches of a group into objects they represent. The
     * order of methods chosen to perform deserialization:
     * <ol>
     *  <li>Built-in deserializers (for primitive types, their boxed versions, and Strings),</li>
     *  <li>ParsingFactory of the given declared type (in a recursive manner).</li>
     * </ol>
     *
     * @param groups groups of a match (excluding the 0th group being the whole match)
     * @return Objects deserialized
     * @throws ParsingException if pattern matched an incorrect number of times
     */
    protected Object[] deserializeGroups(String[] groups) {
        if(groups.length != types.length)
            throw new ParsingException(
                    "The regex matched a different number of times than there "+
                            "are fields in the record: "+groups.length+" matches instead "+
                            "of expected "+types.length+"."
            );
        Object[] args = new Object[groups.length];
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if(builtinDeserializers.containsKey(type))
                args[i] = builtinDeserializers.get(type).apply(groups[i]);
            else
                args[i] = parsingFactories[i].parse(groups[i]);
        }
        return args;
    }

    /**
     * Create instance from method or constructor (depending on type of ParsingFactory).
     *
     * @param args arguments to the method
     * @return new instance
     * @throws ParsingException if failed to initialize object
     */
    protected abstract T instantiate(Object[] args);
}
