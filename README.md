[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![MavenCentral](https://img.shields.io/maven-central/v/de.kkuehlem/where)](#)
[![Build](https://github.com/KKuehlem/Where/actions/workflows/maven.yml/badge.svg)](#)


# Where - A simple Reflection-Based Query Language for Java

## Include with Maven
```xml
<dependency>
    <groupId>de.kkuehlem</groupId>
    <artifactId>where</artifactId>
    <version>1.0.2</version>
</dependency>
```

## Example
```java
List<Person> persons = List.of(
    new Person("Anna", 32),
    new Person("Bert", 39),
    new Person("Peter", 42)
);
        
String input = "age <= 40";
        
List<Person> filtered = persons.stream()
    .filter(Where.filter(input))
    .toList(); // [Anna, Bert]
```

## Supported Types and Operators
* String
    * `=`, `!=`
* Numbers (Byte, Short, Integer, Long, Float, Double, AtomicInteger, AtomicLong, BigInteger, BigDecimal)
    * `=`, `!=`, `<`, `<=`, `>`, `>=`
* Dates (LocalDate, LocalDateTime)
    * `=`, `!=`, `<`, `<=`, `>`, `>=`
    * Literal date format: `yyyy-MM-dd` (ISO 8601)
* Booleans
    * As operands in expressions, e.g. `isActive OR isVerified`
* Custom Types
    * ...explanation coming soon...
* Concatenating Expressions
    * `OR`, `AND`, `NOT`, braces
    * e.g. `(age < 40 OR name = "Anna") AND NOT (age > 30 AND name != "Bert")`

## To Do
- [ ] Add support for lists / arrays (e.g. `IN`, `NOT IN`)
- [ ] Add support for enums
→ Special handler `SpecialWhereTypeDefinition` for list/array and enum types