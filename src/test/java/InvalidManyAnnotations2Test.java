import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Two parsing constructors.
 */
class Dog {
    String name;
    int age;
    String breed;

    @Parses("(\\w+), (\\d+) years?")
    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Parses("(\\w+) ?: ?(\\w+)")
    public Dog(String name, String breed) {
        this.name = name;
        this.breed = breed;
    }
}

public class InvalidManyAnnotations2Test {
    @Test
    void invalidUsageTest(){
        assertThrows(InvalidDataclassException.class, ()->ParsingFactory.of(Dog.class));
    }
}
