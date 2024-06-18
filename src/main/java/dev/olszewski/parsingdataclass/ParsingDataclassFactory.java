package dev.olszewski.parsingdataclass;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.regex.Pattern;

import static dev.olszewski.parsingdataclass.Utils.setFields;

final class ParsingDataclassFactory<T> extends ParsingFactory<T> {
    private final Constructor<T> noArgumentConstructor;
    private final Field[] fields;
    ParsingDataclassFactory(Class<T> cls, Pattern pattern) {
        this(cls, pattern, findConstructor(cls), findFields(cls));
    }



    ParsingDataclassFactory(Class<T> cls, Pattern pattern, Constructor<T> constructor, Field[] fields){
        super(
                cls,
                pattern,
                Arrays.stream(fields).map(Field::getType).toArray(Class<?>[]::new)
        );

        this.noArgumentConstructor = constructor;
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
            throw new ParsingException("Could not create an instance of class "+cls.getName()+".");
        }
        setFields(instance, fields, args);
        return instance;
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
