import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * One parsing constructor and one static parsing method
 */
class Parrot {
    String name;
    Integer age; // null if age is unknown.
    String breed;

    @Parses("(\\w+), (\\d+) years?")
    public Parrot(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Parses("(\\w+) ?: ?(\\w+)")
    public static Parrot fromNameBreed(String name, String breed) {
        Parrot out = new Parrot(name, null);
        out.breed = breed;
        return out;
    }
}

public class InvalidManyAnnotations3Test {
    @Test
    void invalidUsageTest(){
        assertThrows(InvalidDataclassException.class, ()->ParsingFactory.of(Parrot.class));
    }
}

