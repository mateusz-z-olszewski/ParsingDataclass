import java.util.regex.Pattern;

final class ParsingDataclassFactory<T> extends ParsingFactory<T> {
    public ParsingDataclassFactory(Class cls, Pattern value) {
        super(cls);
    }
}
