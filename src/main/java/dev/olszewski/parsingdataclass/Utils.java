package dev.olszewski.parsingdataclass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

public class Utils {
    /**
     * Finds the default implicit constructor for this record. Partially sourced from documentation of
     * Class::getRecordComponents.
     * @param cls Precondition: {@code cls.isRecord() == true}
     * @return default constructor
     * @param <U> although this is not enforced due to making code much more complicated,
     *           U must extend class Record.
     */
    public static <U> Constructor<U> findConstructor(Class<U> cls) {
        Class<?>[] paramTypes = Arrays.stream(cls.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);
        try {
            return cls.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected error encountered.");
        }
    }

    /**
     * Sets fields of a given new instance.
     * @param instance freshly created new instance of T.
     * @param args fields to be set, in order they appear in the class. Precondition: args.length==types.length
     */
    public static <T> void setFields(T instance, Field[] fields, Object[] args) {
        for (int i = 0; i < fields.length; i++) {
            var field = fields[i];
            try {
                field.set(instance, args[i]);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unexpected error encountered.");
            }
        }
    }
}
