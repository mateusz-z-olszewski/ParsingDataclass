import java.lang.reflect.Constructor;
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
}
