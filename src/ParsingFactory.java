import java.util.Collection;
import java.util.List;

public class ParsingFactory<T> {
    Class<? extends T> cls;

    public ParsingFactory(Class <? extends T> cls){
        this.cls = cls;
    }
    public T parse(String string){
        System.out.println("String");
        return null; // todo
    }
    public List<T> parse(Collection<String> strings){
        return parse(strings.toArray(String[]::new));
    }
    public List<T> parse(String... strings){
        System.out.println("String...");
        return null; // todo
    }
}
