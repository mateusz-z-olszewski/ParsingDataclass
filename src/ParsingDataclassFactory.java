import java.lang.reflect.*;
import java.util.Arrays;
import java.util.regex.Pattern;

final class ParsingDataclassFactory<T> extends ParsingFactory<T> {
    private final Constructor<T> noArgumentConstructor;
    private final Field[] fields;
    public ParsingDataclassFactory(Class<T> cls, Pattern pattern) {
        this(cls, pattern, findConstructor(cls), findFields(cls));
    }



    public ParsingDataclassFactory(Class<T> cls, Pattern pattern, Constructor<T> constructor, Field[] fields){
        super(
                cls,
                Arrays.stream(fields).map(Field::getType).toArray(Class<?>[]::new)
        );

        this.noArgumentConstructor = constructor;
        this.pattern = pattern;
        this.fields = fields;

        for (var f : fields){
            try{
                f.setAccessible(true);
            } catch (InaccessibleObjectException | SecurityException e){
                throw new InvalidDataclassException(
                        "Could not allow for setting field "+f.getName()+" of class "+cls.getCanonicalName()+".");
            }
        }

        argumentsAssert(fields.length == types.length,
                "Class "+cls.getName()+" has an incorrect number of parsed fields: "+fields.length
                +" instead of expected "+types.length+".");
    }

    private static boolean fieldNotStatic(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    @Override
    protected T instantiate(Object[] args) {
        T instance;
        try {
            instance = noArgumentConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ParsingException("Could not create an instance of class "+cls.getName());
        }
        setFields(instance, args);
        return instance;
    }

    /**
     * Sets fields of a given new instance.
     * @param instance freshly created new instance of T.
     * @param args fields to be set, in order they appear in the class. Precondition: args.length==types.length
     */
    private void setFields(T instance, Object[] args) {
        for (int i = 0; i < fields.length; i++) {
            var field = fields[i];
            try {
                field.set(instance, args[i]);
            } catch (IllegalAccessException e) {
                // Should never throw. Any issues should be caught earlier.
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Finds the no arguments constructor of the given class.
     */
    private static <U> Constructor<U> findConstructor(Class<U> cls) {
        try {
            return cls.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new InvalidDataclassException(
                    "The given class "+cls.getCanonicalName()+" does not have a constructor taking no arguments");
        }
    }

    /**
     * Finds non-static fields of the class which are not annotated with @{@code NotParsed}
     */
    private static <T> Field[] findFields(Class<T> cls) {
        return Arrays.stream(cls.getDeclaredFields())
                .filter(f -> !f.isAnnotationPresent(NotParsed.class))
                .filter(ParsingDataclassFactory::fieldNotStatic)
                .toArray(Field[]::new);
    }
}
