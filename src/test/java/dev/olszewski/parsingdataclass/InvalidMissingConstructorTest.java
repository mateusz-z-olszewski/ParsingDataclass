package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ParsingDataclass("(.+)")
class InvalidMissingConstructor {
    int field;

    public InvalidMissingConstructor(int field) {
        this.field = field;
    }
}

public class InvalidMissingConstructorTest {
    @Test
    void missingConstructorTest(){
        assertThrows(InvalidDataclassException.class,
                ()->ParsingFactory.of(InvalidMissingConstructor.class));
    }
}
