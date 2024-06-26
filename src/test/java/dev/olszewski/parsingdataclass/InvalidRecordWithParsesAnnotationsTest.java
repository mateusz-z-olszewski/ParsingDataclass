package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ParsingDataclass("([a-zA-Z])")
record InvalidCharWrapper(
        char c
){
    @Parses("([a-zA-Z]+)")
    static InvalidCharWrapper fromString(String s){
        return new InvalidCharWrapper(s.charAt(0));
    }
}

public class InvalidRecordWithParsesAnnotationsTest {
    @Test
    void invalidRecordTest(){
        assertThrows(InvalidDataclassException.class, ()-> ParsingFactory.of(InvalidCharWrapper.class));
    }
}
