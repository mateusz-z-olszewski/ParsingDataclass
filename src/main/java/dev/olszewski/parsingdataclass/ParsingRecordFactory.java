package dev.olszewski.parsingdataclass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

final class ParsingRecordFactory<T> extends ParsingFactory<T> {
    private final Constructor<T> constructor;

    ParsingRecordFactory(Class<T> cls) {
        this(cls, Utils.findConstructor(cls));
    }

    ParsingRecordFactory(Class<T> cls, Constructor<T> recordConstructor) {
        super(
                cls,
                findPatternAnnotation(cls),
                recordConstructor.getParameterTypes()
        );

        this.constructor = recordConstructor;
    }

    /**
     * Finds the @{@link ParsingDataclass} in the record. Makes sure there are no @{@link Parses} present.
     *
     * @throws InvalidDataclassException if there is an incorrect combination of annotations.
     */
    private static Pattern findPatternAnnotation(Class<?> cls) {
        Arrays.stream(cls.getDeclaredMethods())
                .map(m -> m.getAnnotation(Parses.class))
                .filter(Objects::nonNull)
                .findFirst().ifPresent(x -> {
                    throw new InvalidDataclassException(
                            "Records may not contain @dev.olszewski.parsingdataclass.Parses annotations on methods.");
                });

        var ann = cls.getAnnotation(ParsingDataclass.class);
        argumentsAssert(ann != null,
                "The given record contains no @dev.olszewski.parsingdataclass.ParsingDataclass annotation.");

        return Pattern.compile(ann.value());
    }

    @Override
    protected T instantiate(Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ParsingException("Could not create instance of record " + cls.getCanonicalName() + ".");
        }
    }
}
