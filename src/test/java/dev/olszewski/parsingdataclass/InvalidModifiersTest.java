package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ParsingDataclass("(.+)")
abstract class InvalidAbstractDataclass {
    int field;
}


class InvalidModifiersTest {
    @Test
    void invalidAbstractTest() {
        assertThrows(InvalidDataclassException.class,
                () -> ParsingFactory.of(InvalidAbstractDataclass.class));
    }
}
