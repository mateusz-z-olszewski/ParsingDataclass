import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParsingFactoryScheme {
    @Test
    void exampleUsageCarsTest() throws ParsingException {
        ParsingFactory<Car> pf = ParsingFactory.of(Car.class);

        Car volvo = new Car("Volvo", 1989, "Graphite");
        Car ford  = new Car("Ford" , 2010, "White");
        Car saab  = new Car("Saab" , 2001, "Navy blue");

        Car first = pf.parse("Car[brand=Volvo,year=1989,color=Graphite]");
        assertEquals(volvo, first);

        Car[] parkingLot = pf.parse(new String[]{
                "Car[brand=Volvo,year=1989,color=Graphite]",
                "Car[brand=Ford,year=2010,color=White]",
                "Car[vin=1234567890SE123456]"
        });
        assertArrayEquals(new Car[]{volvo, ford, saab}, parkingLot);
    }

    @Test
    void exampleUsageBikesTest() throws ParsingException {
        ParsingFactory<FixedGearBike> pf = ParsingFactory.of(FixedGearBike.class);

        FixedGearBike cityBike = new FixedGearBike();
        cityBike.model = "City Bike brand ABC";
        cityBike.year = 2023;
        cityBike.gearRatio = 1.75f;
        cityBike.frameNumber="ABC1234";


        assertEquals(cityBike, pf.parse("Bike(City Bike brand ABC,1234,Black)"));
    }

    @Test
    void exampleUsageMuseumsTest() throws ParsingException {
        ParsingFactory<Museum> pf = ParsingFactory.of(Museum.class);

        Museum m1 = new Museum("Musee d'Orsay", 4.7f);
        assertEquals(m1, pf.parse("Musee d'Orsay:4.7 stars"));
    }

    @Test
    void invalidUsagesDogTest(){
        assertThrows(InvalidDataclassException.class, ()->{
            var pf = ParsingFactory.of(Dog.class);
        });
    }

    @Test
    void invalidUsagesCatTest(){
        assertThrows(InvalidDataclassException.class, ()->{
            var pf = ParsingFactory.of(Cat.class);
        });
    }

    @Test
    void invalidUsagesParrotTest(){
        assertThrows(InvalidDataclassException.class, ()->{
            var pf = ParsingFactory.of(Parrot.class);
        });
    }
}
