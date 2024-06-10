import java.util.Objects;

// todo: refactor the scheme into each example having its own class located in the same file as test class
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


@ParsingDataclass("Bike\\(([^,]+),(\\d{4}),([\\d.]+)\\)")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixedGearBike that = (FixedGearBike) o;

        if (year != that.year) return false;
        if (Double.compare(gearRatio, that.gearRatio) != 0) return false;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = model != null ? model.hashCode() : 0;
        result = 31 * result + year;
        temp = Double.doubleToLongBits(gearRatio);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (frameNumber != null ? frameNumber.hashCode() : 0);
        return result;
    }
}


@ParsingDataclass("([-a-zA-Z ']+):([\\d.]+) stars\\.?")
record Museum(
        String name,
        float visitorRating
) {}