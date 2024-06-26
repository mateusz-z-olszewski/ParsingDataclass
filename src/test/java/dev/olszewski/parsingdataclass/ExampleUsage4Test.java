package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Boat {
    String name;
    int year;
    @NotParsed
    long id;


    public Boat(String name, int year) {
        this.name = name;
        this.year = year;
    }

    // observe usage of non-capturing groups (using ?:). This is crucial to
    // make sure that the class is parsed correctly.
    @Parses("(?:Vessel|Ship) named ([a-zA-Z ]+)")
    public static Boat brandNewBoatFromName(String name){
        return new Boat(name, 2024);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Boat boat = (Boat) o;

        if (year != boat.year) return false;
        return Objects.equals(name, boat.name);
    }
}

public class ExampleUsage4Test {
    static ParsingFactory<Boat> pf = ParsingFactory.of(Boat.class);
    @Test
    void exampleUsageBoatTest(){
        assertEquals(new Boat("Mary Celeste", 2024),
                pf.parse("Ship named Mary Celeste"));
        assertEquals(new Boat("Titanic", 2024),
                pf.parse("Vessel named Titanic"));
    }
}
