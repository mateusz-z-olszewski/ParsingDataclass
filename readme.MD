# ParsingDataclass

A utility library for parsing classes from strings using regular expressions.

#### Non-technical checklist
- [X] Turn project into Maven project
- [X] Add GitHub Actions for CI/CD
- [X] Reach version 1.0
- [ ] Publish to Maven repository

## Example usages
_For more example usages see test classes named `ExampleUsage\d+Test.java`._

Consider a situation in which info regarding cars is stored as text. This library
allows for parsing it to Java objects, by adding to a static factory method, 
constructor, or class/record itself an annotation with the pattern that allows to 
isolate fields from the input. For more information refer to the Javadoc,especially 
for class `ParsingDataclass`

#### Example 1: cars (annotation on the constructor)
Class to be parsed:
```java
class Car {
    private final String brand;
    private final int year;
    private final String color;

    @Parses("Car\\[brand=(.+?),year=(\\d{4}),color=(.*?)\\]")
    public Car(String brand, int year, String color) {
        this.brand = brand;
        this.year = year;
        this.color = color;
    }
}
```
Parsing that class:
```java
ParsingFactory<Car> pf = ParsingFactory.of(Car.class);
Car car = pf.parse("Car[brand=Volvo,year=1989,color=Graphite]");
```
#### Example 2: museums (annotation on the record itself)

```java
@ParsingDataclass("([-a-zA-Z ']+): ?([\\d.]+) stars\\.?")
record Museum(
        String name,
        float visitorRating
) {}
```
```java
ParsingFactory<Museum> pf = ParsingFactory.of(Museum.class);
Museum museeDorsay = pf.parse("Musee d'Orsay:4.8 stars");
Museum louvre = pf.parse("Louvre: 4.7 stars.");
```

## Versions
### Version 1.0
- documentation
- migration to maven
### Version 0.2
- more parsing options: parsing from static factory methods
- 100% code coverage
### Version 0.1
- proof of concept
- example usage using tests
- parsing of records, classes from fields and constructors
- javadoc for all public methods and most non-public ones
