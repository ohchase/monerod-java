# monerod-java

Minimal Java (Maven) project for the monerod core used in this repo.

## Prerequisites

- Java 17 (JDK)
- Maven 3.6+
- Enable annotation processing in your IDE (Lombok is used)

## Build

Run a normal Maven build from the project root:

```bash
mvn -B package
```

This produces `target/monerod-0.1-SNAPSHOT.jar`.

## Run

Run the main class included in this project:

```bash
java -cp target/monerod-0.1-SNAPSHOT.jar org.ohchase.monerod.bin.Main
```

Or use the Maven Exec plugin:

```bash
mvn -Dexec.mainClass="org.ohchase.monerod.bin.Main" exec:java
```

## Notes

- Lombok is declared in `pom.xml`; enable annotation processing in IntelliJ IDEA (`Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors`).
- Project source/target is set to Java 17 in `pom.xml`.


