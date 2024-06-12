import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class because of its pattern may allow for incorrect matching, if two digits are supplied.
 */
@ParsingDataclass("(\\d)(\\d)?")
class Digit {
    int digit;
}

/**
 * This record's pattern allows for empty second group. However, it should be deserialized to an
 * empty string.
 */
@ParsingDataclass("(\\d+) is an? ([a-zA-Z]*)")
record NamedNumber(
        int value,
        String name
) {
}

public class ParsingFactoryTests {
    @Test
    void repeatedPFsTest() {
        ParsingFactory<Digit> pf1 = ParsingFactory.of(Digit.class);
        ParsingFactory<Digit> pf2 = ParsingFactory.of(Digit.class);

        assertSame(pf1, pf2);
    }

    @Test
    void digitUsageTest() {
        ParsingFactory<Digit> pf = ParsingFactory.of(Digit.class);

        Digit nine = pf.parse("9");
        assertEquals(9, nine.digit);

        Digit none = pf.parse("");
        assertNull(none);

        Digit gone = pf.parse((String) null);
        assertNull(gone);

        Digit more = pf.parse("123");
        assertNull(more);
    }

    @Test
    void emptyArraysTest() {
        ParsingFactory<Digit> pf = ParsingFactory.of(Digit.class);

        Digit[] emptyArray = new Digit[0];

        assertNull(pf.parse((List<String>) null));
        assertArrayEquals(emptyArray, pf.parse());
        assertArrayEquals(emptyArray, pf.parse(List.of()));
    }

    @Test
    void invalidInputMatchSizeTest() {
        ParsingFactory<Digit> pf = ParsingFactory.of(Digit.class);

        assertThrows(ParsingException.class, () -> pf.parse("33"));
    }

    @Test
    void emptyGroupTest() {
        var pf = ParsingFactory.of(NamedNumber.class);

        NamedNumber num = pf.parse("8 is an eight");
        assertEquals(8, num.value());
        assertEquals("eight", num.name());

        assertEquals("", pf.parse("123 is a ").name());
    }
}
