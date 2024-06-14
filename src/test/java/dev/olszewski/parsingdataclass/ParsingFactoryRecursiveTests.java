package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ParsingDataclass("#(\\d+)")
class Ordinal {
    int index;

    public Ordinal() {
    }

    public Ordinal(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ordinal ordinal = (Ordinal) o;

        return index == ordinal.index;
    }

    @Override
    public int hashCode() {
        return index;
    }
}

class Sheep {
    String name;
    Ordinal ordinal;

    @Parses("dev.olszewski.parsingdataclass.Sheep called ([^:]+): ?(.+)")
    Sheep(String name, Ordinal ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sheep sheep = (Sheep) o;

        if (!Objects.equals(name, sheep.name)) return false;
        return Objects.equals(ordinal, sheep.ordinal);
    }
}

/**
 * dev.olszewski.parsingdataclass.ParsingDataclass which has valid annotations, but has a field of class which is not parseable.
 */
@ParsingDataclass("Object<(.+)>")
record ObjectWrapper(
        Object o
) {
}

public class ParsingFactoryRecursiveTests {
    final ParsingFactory<Sheep> pf = ParsingFactory.of(Sheep.class);

    @Test
    void simpleRecursiveTest() {
        assertEquals(
                new Sheep("Shaun", new Ordinal(1)),
                pf.parse("dev.olszewski.parsingdataclass.Sheep called Shaun: #1")
        );
    }

    @Test
    void invalidFieldClassTest() {
        assertThrows(InvalidDataclassException.class, () -> ParsingFactory.of(ObjectWrapper.class));
    }
}
