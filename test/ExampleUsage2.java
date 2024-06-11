import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParsingDataclass("Bike\\(([^,]+),(\\d{4}),([\\d.]+)\\)")
class FixedGearBike {
    String model;
    int year;
    double gearRatio;
    @NotParsed
    String frameNumber;

    FixedGearBike(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixedGearBike that = (FixedGearBike) o;

        if (year != that.year) return false;
        if (Double.compare(gearRatio, that.gearRatio) != 0) return false;
        return Objects.equals(model, that.model);
    }
}

public class ExampleUsage2 {
    @Test
    void exampleUsageBikesTest() throws ParsingException {
        ParsingFactory<FixedGearBike> pf = ParsingFactory.of(FixedGearBike.class);

        FixedGearBike cityBike = new FixedGearBike();
        cityBike.model = "City Bike brand ABC";
        cityBike.year = 2023;
        cityBike.gearRatio = 1.75f;
        cityBike.frameNumber="ABC1234";

        assertEquals(cityBike, pf.parse("Bike(City Bike brand ABC,2023,1.75)"));
        // ^ note: equals ignores FrameNumber.
    }
}
