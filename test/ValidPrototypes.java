class Car {
    private final String brand;
    private final int year;
    private final String color;


    @Parses("Car\\[brand=(.+?),year=(\\d{4}),color=(.*?)\\]")
    public Car(String brand, int year, String color){
        this.brand = brand;
        this.year = year;
        this.color = color;
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


@ParsingDataclass("([-a-zA-Z ']+):([\\d.]+) stars\\.?")
record Museum(
        String name,
        float visitorRating
) {}