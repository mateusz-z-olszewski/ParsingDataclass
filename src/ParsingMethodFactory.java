import java.lang.reflect.Method;
import java.util.regex.Pattern;

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

        argumentsAssert(cls.isAssignableFrom(method.getReturnType()),
                "The method "+method.getName()+" of class "+cls.getCanonicalName()+
                " does not return an instance of that class."
        );

        this.method = method;
    }

    @Override
    protected T instantiate(Object[] args) {
        return null;

        // todo
    }

}
