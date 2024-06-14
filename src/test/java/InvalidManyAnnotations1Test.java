import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Parsing dataclass containing both @ParsingDataclass and a parsing constructor
 */
@ParsingDataclass("(\\w+), (\\d+) years?")
class Cat {
    String name;
    int age;
    @NotParsed
    String breed;

    @Parses("(\\w+) ?: ?(\\w+)")
    public Cat(String name, String breed) {
        this.name = name;
        this.breed = breed;
    }
}

public class InvalidManyAnnotations1Test {
    @Test
    void invalidUsageTest(){
        assertThrows(InvalidDataclassException.class, ()->ParsingFactory.of(Cat.class));
    }
}
