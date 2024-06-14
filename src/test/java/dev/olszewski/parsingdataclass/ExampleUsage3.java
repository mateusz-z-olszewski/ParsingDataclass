package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParsingDataclass("([-a-zA-Z ']+):([\\d.]+) stars\\.?")
record Museum(
        String name,
        float visitorRating
) {}

public class ExampleUsage3 {
    @Test
    void exampleUsageMuseumsTest() throws ParsingException {
        ParsingFactory<Museum> pf = ParsingFactory.of(Museum.class);

        Museum m1 = new Museum("Musee d'Orsay", 4.7f);
        assertEquals(m1, pf.parse("Musee d'Orsay:4.7 stars"));
    }
}
