import java.util.HashMap;
class Car {
    private final String brand;
    private final int year;
    private final String color;

    private static HashMap<String, Car> carDatabase = new HashMap<>();
    {
        carDatabase.put(
                "1234567890SE123456",
                new Car("Saab", 2001, "Navy blue")
        );
    }


    @Parses("Car\\[brand=(.+?),year=(\\d{4}),color=(.*?)\\]")
    public Car(String brand, int year, String color){
        this.brand = brand;
        this.year = year;
        this.color = color;
    }

    @Parses("Car\\[vin=(\\d{10}\\w{2}\\d{6}\\]")
    public static Car fromVIN(String x){
        return carDatabase.get(x);
    }
}


@ParsingDataclass("Bike\\(([^,]+),(\\d{4}),([\\d.eE]+)\\)")
class FixedGearBike {
    String model;
    int year;
    double gearRatio;
    @NotParsed
    String frameNumber;

    FixedGearBike(){}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FixedGearBike{");
        sb.append("model='").append(model).append('\'');
        sb.append(", year=").append(year);
        sb.append(", gearRatio=").append(gearRatio);
        if(frameNumber != null) sb.append(", frameNumber='").append(frameNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}