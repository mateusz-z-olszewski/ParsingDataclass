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

/**
 * Parsing dataclass containing an additional parsing constructor
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