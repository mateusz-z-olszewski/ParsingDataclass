import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Egg {
    String color;

    @Parses("([a-zA-Z]+)")
    Egg fromString(String s){
        Egg out = new Egg();
        out.color = s;
        return out;
    }
}

public class InvalidNonStaticFactoryMethodTest {
    @Test
    void invalidFactoryMethodTest(){
        assertThrows(InvalidDataclassException.class, ()->ParsingFactory.of(Egg.class));
    }
}
