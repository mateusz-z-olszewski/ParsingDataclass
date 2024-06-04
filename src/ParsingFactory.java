import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public sealed class ParsingFactory<T>
        permits ParsingDataclassFactory, ParsingMethodFactory, ParsingRecordFactory {
    protected final Class<? extends T> cls;
    protected Initializer<T> factory;
    protected Deserializer deserializer;
    protected Pattern pattern;


    private final static HashMap<Class<?>, ParsingFactory<?>> instances
            = new HashMap<>();
    protected final HashMap<Class<?>, Function<String, Object>> builtinDeserializers
            = new HashMap<>();
    {
        initializeBuiltinDeserializers();
    }

    private void initializeBuiltinDeserializers() {
        // initialize resolving for builtins
        builtinDeserializers.put(String.class, x->x);
        builtinDeserializers.put(float.class,   Float::valueOf  );
        builtinDeserializers.put(Float.class,   Float::valueOf  );
        builtinDeserializers.put(double.class,  Double::valueOf );
        builtinDeserializers.put(Double.class,  Double::valueOf );
        builtinDeserializers.put(int.class,     Integer::valueOf);
        builtinDeserializers.put(Integer.class, Integer::valueOf);
        builtinDeserializers.put(short.class,   Short::valueOf  );
        builtinDeserializers.put(Short.class,   Short::valueOf  );
        builtinDeserializers.put(boolean.class, Boolean::valueOf);
        builtinDeserializers.put(Boolean.class, Boolean::valueOf);
    }

    @SuppressWarnings("unchecked")
    public static <U> ParsingFactory<U> of(Class<? extends U> cls){
        if(instances.containsKey(cls))
            return (ParsingFactory<U>) instances.get(cls);
        else
            return createParsingFactory(cls);
    }
    private static <U> ParsingFactory<U> createParsingFactory(Class<? extends U> cls){
        if(cls.isRecord())
            return new ParsingRecordFactory<U>(cls);

        ParsingDataclass ann = findParsingDataclassAnnotation();
        Method method = findParsingMethod();

        argumentsAssert(ann != null || method != null,
                "The given class contains neither @ParsingDataclass annotation " +
                        "nor a method with @Parses annotation.");
        argumentsAssert(ann != null ^ method != null,
                "The given class contains both @ParsingDataclass annotation " +
                        "and a method with @Parses annotation.");
        if(ann != null)
            return new ParsingDataclassFactory<>(cls, Pattern.compile(ann.value()));
        else
            return new ParsingMethodFactory<>(cls, method);
    }



    public T parse(String string) throws ParsingException {
        Matcher m = this.pattern.matcher(string);
        if(!m.matches()) throw new ParsingException("Failed to match " + string);
        int groups = m.groupCount();
        String[] stringArgs = new String[groups];
        for (int i = 0; i < stringArgs.length; i++) {
            stringArgs[i] = m.group(i + 1);
        }
        Object[] args = deserializer.deserialize(stringArgs);
        return factory.initialize(args);
    }
    public T[] parse(Collection<String> strings){
        return parse(strings.toArray(String[]::new));
    }
    public T[] parse(String... strings){
        System.out.println("String...");
        return null; // todo
    }


    ParsingFactory(Class <? extends T> cls){
        this.cls = cls;
    }


    private <U> Object deserialize(String arg, Class<U> type) throws ParsingException{
        return null; // todo
    }

    private static Method findParsingMethod() {
        return null; // todo
    }

    private static ParsingDataclass findParsingDataclassAnnotation() {
        return null; // todo
    }




    protected static void argumentsAssert(boolean b, String s) throws InvalidDataclassException {
        if(!b) throw new InvalidDataclassException(s);
    }
}
