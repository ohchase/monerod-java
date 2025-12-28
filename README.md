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

example
```bash
java -cp target/monerod-0.1-SNAPSHOT.jar org.ohchase.monerod.bin.Main /home/chase/Downloads/monero-linux-x86_64/monerod /home/chase/Downloads


Monerod Binary: /home/chase/Downloads/monero-linux-x86_64/monerod
Data Directory: /home/chase/Downloads
Daemon Settings:
  Network Type: STAGE_NET
  Data Directory: /home/chase/Downloads
P2P is ready.
RPC is ready.
Daemon is started.
New top block candidate: 2021414 (current: 377720)
Sync progress: 377740 / 2021414
Sync progress: 377760 / 2021414
Sync progress: 377780 / 2021414
Sync progress: 377800 / 2021414
```

Or use the Maven Exec plugin:

```bash
mvn -Dexec.mainClass="org.ohchase.monerod.bin.Main" exec:java
```

## Notes

- Lombok is declared in `pom.xml`; enable annotation processing in IntelliJ IDEA (`Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors`).
- Project source/target is set to Java 17 in `pom.xml`.


