package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This record has an annotation on constructor
 * @param value
 */
record Record1 (int value){
    @Parses("([\\d.f]+)")
    Record1(float value){
        this((int) value);
    }
}

public class InvalidManyAnnotations4Test {
    @Test
    void invalidManyAnnotations1Test(){
        assertThrows(InvalidDataclassException.class, ()->ParsingFactory.of(Record1.class));
    }
}
