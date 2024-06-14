package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InvalidIntWrapper {
    int value;
    @Parses("(\\d+)")
    public InvalidIntWrapper(int value){
        this.value = value;

        throw new RuntimeException();
    }
}

@ParsingDataclass("([.\\d-e]+)")
class InvalidDoubleWrapper {
    double value;
    public InvalidDoubleWrapper () {
        throw new RuntimeException();
    }
}


public class InvalidThrowingConstructorTests {
    @Test
    void throwingConstructor1(){
        var pf = ParsingFactory.of(InvalidIntWrapper.class);

        assertThrows(ParsingException.class, ()->pf.parse("5"));
    }

    @Test
    void throwingConstructor2(){
        var pf = ParsingFactory.of(InvalidDoubleWrapper.class);

        assertThrows(ParsingException.class, ()->pf.parse("5.3"));
    }

}
