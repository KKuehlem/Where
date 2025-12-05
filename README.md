[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![Build](https://github.com/KKuehlem/Where/actions/workflows/maven.yml/badge.svg)](#)
[![MavenCentral](https://img.shields.io/maven-central/v/de.kkuehlem/where)](https://central.sonatype.com/artifact/de.kkuehlem/where)

# Where - A simple Reflection-Based Query Language for Java

## Include with...

### Maven
```xml
<dependency>
    <groupId>de.kkuehlem</groupId>
    <artifactId>where</artifactId>
    <version>1.0.6</version>
</dependency>
```

### Gradle
```groovy
implementation group: 'de.kkuehlem', name: 'where', version: '1.0.6'
```

## Example
```java
List<Person> persons = List.of(
    new Person("Anna", 32),
    new Person("Bert", 39),
    new Person("Peter", 42)
);
        
List<Person> filtered = persons.stream()
    .filter(Where.filter("age <= 40"))
    .toList(); // [Anna, Bert]
```

## Supported Types and Operators
* String (anything which extends `java.lang.CharSequence`)
    * `=`, `!=`
    * `CONTAINS` to test if a string contains another string literally
    * `MATCHES` to test if a string matches a Regex
* Numbers (anything which extends `java.lang.Number`)
    * `=`, `!=`, `<`, `<=`, `>`, `>=`
* Dates (LocalDate, LocalDateTime)
    * `=`, `!=`, `<`, `<=`, `>`, `>=`
    * Literal date format: `yyyy-MM-dd` (ISO 8601)
* Booleans
    * As operands in expressions, e.g. `isActive OR isVerified`
* Enums
    * Will be treated as strings via their `toString()` method
* Custom Types
    * ...explanation coming soon...
* Concatenating Expressions
    * `OR`, `AND`, `NOT`, braces
    * e.g. `(age < 40 OR name = "Anna") AND NOT (age > 30 AND name != "Bert")`

## Features
* Values can be `null` and `null` can also be used literally in a query
* Expressions with `null` will only be `true`, if a equals operator (`=`, `<=`, `>=`) is used and both operands are `null`
    * This means that e.g. `null > null` is `false` and `null >= null` is `true`
* Identifiers can refer to sub-objects, e.g. `book.author` (see `WhereObjectTest.java`)
    * If a property of a `null` objects is referenced, no exception is throw - it just evaluates to `null` (e.g. `book.author = null`, if `book` or `author` are `null`)

## To Do
- [ ] Add support for lists / arrays (e.g. `IN`, `NOT IN`)
- [ ] Support for more date types like Zoned or Offset Date(Time)
- [ ] Special properties for some types like `length` for Strings and `year`, `month`,... for dates
- [ ] More examples and documentation