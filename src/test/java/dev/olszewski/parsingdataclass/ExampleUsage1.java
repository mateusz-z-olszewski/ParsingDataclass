package dev.olszewski.parsingdataclass;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Car {
    private final String brand;
    private final int year;
    private final String color;


    @Parses("dev.olszewski.parsingdataclass.Car\\[brand=(.+?),year=(\\d{4}),color=(.*?)\\]")
    public Car(String brand, int year, String color) {
        this.brand = brand;
        this.year = year;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (year != car.year) return false;
        if (!Objects.equals(brand, car.brand)) return false;
        return Objects.equals(color, car.color);
    }

    @Override
    public int hashCode() {
        int result = brand != null ? brand.hashCode() : 0;
        result = 31 * result + year;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}

public class ExampleUsage1 {
    @Test
    void exampleUsageCarsTest() throws ParsingException {
        ParsingFactory<Car> pf = ParsingFactory.of(Car.class);

        Car volvo = new Car("Volvo", 1989, "Graphite");
        Car ford = new Car("Ford", 2010, "White");

        Car first = pf.parse("dev.olszewski.parsingdataclass.Car[brand=Volvo,year=1989,color=Graphite]");
        assertEquals(volvo, first);

        var cars = new String[]{
                "dev.olszewski.parsingdataclass.Car[brand=Volvo,year=1989,color=Graphite]",
                "dev.olszewski.parsingdataclass.Car[brand=Ford,year=2010,color=White]"
        };
        Car[] parkingLot = pf.parse(cars);
        var expected = new Car[]{volvo, ford};
        assertArrayEquals(expected, parkingLot);
        assertArrayEquals(expected, pf.parse(List.of(
                "dev.olszewski.parsingdataclass.Car[brand=Volvo,year=1989,color=Graphite]",
                "dev.olszewski.parsingdataclass.Car[brand=Ford,year=2010,color=White]"
        )));
    }
}
