package dev.olszewski.parsingdataclass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

public final class ParsingConstructorFactory<T> extends ParsingFactory<T> {
    private final Constructor<T> constructor;

    /**
     * Constructor for this subtype ParsingFactory from a constructor.
     * @param cls class.
     * @param constructor constructor annotated with @{link Parses}. Precondition:
     *  {@code constructor.getAnnotation(Parses.class) != null}
     */
    ParsingConstructorFactory(Class<? extends T> cls, Constructor<T> constructor) {
        super(
                cls,
                Pattern.compile(constructor.getAnnotation(Parses.class).value()),
                constructor.getParameterTypes()
        );

        this.constructor = constructor;
    }

    @Override
    protected T instantiate(Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ParsingException("Unknown error occurred when trying to create " +
                    "instance of class "+cls.getCanonicalName()+".");
        }
    }
}
