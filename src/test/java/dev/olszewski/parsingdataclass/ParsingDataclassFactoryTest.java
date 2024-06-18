package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ParsingDataclass("(.+)")
class StringContainer {
    private final String content;

    StringContainer() {
        content = "";
    }

    StringContainer(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

public class ParsingDataclassFactoryTest {
    @Test
    void finalFieldTest() {
        var pf = ParsingFactory.of(StringContainer.class);
        assertEquals("abc", pf.parse("abc").getContent());
    }
}
