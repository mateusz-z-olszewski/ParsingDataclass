package dev.olszewski.parsingdataclass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

/**
 * class invariant: cls.isAssignableFrom(method.getReturnType())
 */
final class ParsingMethodFactory<T> extends ParsingFactory<T> {
    private final Method method;

    /**
     * Constructor for this subtype ParsingFactory from a static method following the factory pattern.
     * @param cls class.
     * @param method method annotated with @{link Parses}. Precondition:
     *  {@code method.getAnnotation(Parses.class) != null}
     */
    ParsingMethodFactory(Class<? extends T> cls, Method method) {
        super(
                cls,
                Pattern.compile(method.getAnnotation(Parses.class).value()),
                method.getParameterTypes()
        );

        argumentsAssert(Modifier.isStatic(method.getModifiers()),
                "The given method does not follow static factory pattern, as it " +
                    "is not static.");
        argumentsAssert(cls.isAssignableFrom(method.getReturnType()),
                "The method "+method.getName()+" of class "+cls.getCanonicalName()+
                " does not return an instance of that class.");

        this.method = method;
    }

    @Override
    protected T instantiate(Object[] args) {
        try {
            //noinspection unchecked
            return (T) method.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ParsingException("Could not use method "+method.getName()+" of class "+cls.getCanonicalName()
                    +" to create a new instance.");
        }
    }
}
