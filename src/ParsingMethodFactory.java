import java.lang.reflect.Method;

final class ParsingMethodFactory<T> extends ParsingFactory<T> {
    public ParsingMethodFactory(Class<? extends T> cls, Method method) {
        super(cls);
    }
}
