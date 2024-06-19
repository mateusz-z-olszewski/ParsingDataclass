package dev.olszewski.parsingdataclass;

import dev.olszewski.parsingdataclass.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Pattern;

final class ParsingDataclassFactory<T> extends ParsingFactory<T> {
    private final Constructor<T> noArgumentConstructor;
    private final Field[] fields;

    ParsingDataclassFactory(Class<T> cls, Pattern pattern) {
        this(cls, pattern, findConstructor(cls), findFields(cls));
    }


    ParsingDataclassFactory(Class<T> cls, Pattern pattern, Constructor<T> constructor, Field[] fields) {
        super(
                cls,
                pattern,
                Arrays.stream(fields).map(Field::getType).toArray(Class<?>[]::new)
        );

        this.noArgumentConstructor = constructor;
        this.fields = fields;

        for (var f : fields) {
            Utils.setAccessible(f);
        }
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
            throw new ParsingException("Could not create an instance of class " + cls.getName() + ".");
        }
        Utils.setFields(instance, fields, args);
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
                    "The given class " + cls.getCanonicalName() + " does not have a constructor taking no arguments");
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
