import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

final class ParsingRecordFactory<T> extends ParsingFactory<T> {
    private final Constructor<T> constructor;


    ParsingRecordFactory(Class<? extends T> cls){
        this(cls, findConstructor(cls));
    }

    ParsingRecordFactory(Class<? extends T> cls, Constructor<T> recordConstructor) {
        super(cls, recordConstructor.getParameterTypes());
        this.constructor = recordConstructor;

        findPatternAnnotation();


    }

    /**
     * Finds the @{@link ParsingDataclass} in the record. Makes sure there are no @{@link Parses} present.
     * @throws InvalidDataclassException if there is an incorrect combination of
     * annotations.
     */
    private void findPatternAnnotation() {
        Arrays.stream(cls.getDeclaredMethods())
                .map(m -> m.getAnnotation(Parses.class))
                .filter(Objects::nonNull)
                .findFirst().ifPresent(x -> {
                    throw new InvalidDataclassException(
                            "Records may not contain @Parses annotations on methods.");
                });

        var ann = cls.getAnnotation(ParsingDataclass.class);
        argumentsAssert(ann != null,
                "The given record contains no @ParsingDataclass annotation.");

        this.pattern = Pattern.compile(ann.value());
    }
    @Override
    protected T instantiate(Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ParsingException("Could not instantiate instance of record " + cls.getCanonicalName() + ".");
        }
    }

    private static <T> Constructor<T> findConstructor(Class<? extends T> cls) {
        var constructors = cls.getDeclaredConstructors();
        argumentsAssert(constructors.length == 1,
                "The given record contains more than one constructor.");
        //noinspection unchecked
        return (Constructor<T>) constructors[0];
    }
}
