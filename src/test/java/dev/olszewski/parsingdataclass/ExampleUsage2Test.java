package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParsingDataclass("Bike\\(([^,]+),(\\d{4}),([\\d.]+)\\)")
class FixedGearBike {
    String model;
    int year;
    private float gearRatio;
    @NotParsed
    String frameNumber;

    static int someStaticField;

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

    public float getGearRatio() {
        return gearRatio;
    }

    public void setGearRatio(float gearRatio) {
        this.gearRatio = gearRatio;
    }
}

class ExampleUsage2Test {
    static FixedGearBike cityBike = new FixedGearBike();
    static FixedGearBike roadBike = new FixedGearBike();
    static {
        cityBike.model = "City Bike brand ABC";
        cityBike.year = 2023;
        cityBike.setGearRatio(1.75f);
        cityBike.frameNumber="ABC1234";

        roadBike.model = "Road Bikes 101";
        roadBike.year = 2010;
        roadBike.setGearRatio(2.45f);
    }
    @Test
    void exampleUsageBikesTest() throws ParsingException {
        ParsingFactory<FixedGearBike> pf = ParsingFactory.of(FixedGearBike.class);
        assertEquals(cityBike, pf.parse("Bike(City Bike brand ABC,2023,1.75)"));
        // ^ note: equals ignores FrameNumber.
        List<String> bikeDescriptions = List.of(
                "Bike(City Bike brand ABC,2023,1.75)",
                "Bike(Road Bikes 101,2010,2.45)"
        );
        FixedGearBike[] bikes = pf.parse(bikeDescriptions);
        assertEquals(cityBike, bikes[0]);
        assertEquals(roadBike, bikes[1]);
    }
}
