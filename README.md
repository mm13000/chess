# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Server Sequence Diagram ](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkwBzKBACu2GAGI0wKgE8YAJRRakEsFEFIIaYwHcAFkjAdEqUgBaAD4WakoALhgAbQAFAHkyABUAXRgAej0VKAAdNABvLMpTAFsUABoYXCl3aBlKlBLgJAQAX0wKcNgQsLZxKKhbe18oAAoiqFKKquUJWqh6mEbmhABKDtZ2GB6BIVFxKSitFDAAVWzx7Kn13ZExSQlt0PUosgBRABk3uCSYCamYAAzXQlP7ZTC3fYPbY9Tp9FBRNB6BAIDbULY7eRQw4wECDQQoc6US7FYBlSrVOZ1G5Y+5SJ5qBRRACSADl3lZfv8ydNKfNFssWjA2Ul4mDaHCMaFIXSJFE8SgCcI9GBPCTJjyaXtZQyXsL2W9OeKNeSYMAVZ4khAANbofWis0WiG0g6PQKwzb9R2qq22tBo+Ew0JwyLey029AByhB+DIdBgKIAJgADMm8vlzT6I2h2ugZJodPpDEZoLxjjAPhA7G4jF4fH440Fg6xQ3FEqkMiopC40Onuaa+XV2iHus30V6EFWkGh1VMKbN+etJeIGTLXUcTkSxv2UFq7q7dUzyJ9vlyrjygSDjc7tQf3WP4VEtwGpTA1w8N2BYgu6rPNTf92hHo9XeL4fhmGo6kvCBQQmADsTdD1xwRGBv0ghYXxXTFbw-XF8V8ZVVT-Mo9wQw8WQNI0d0qTNwz9e0xVo+CdXvXp2CiWjfUjZdVFYkcOItLj-RHGN-HjJNU3TTjs1zNB820XQDGMHQUDtSstH0Zha28XxMDEptelbPgTySN40nSLsJB7PJpL9KNRzYr11M00ZbPQJdPV46UXVw44wEItU3LQUiWOeI9QNPMNmLvJDHyinjVx8nEZBQBAThQALXME7MQrvMKomMr5TKi996T4zzEWRVEEvKrpKpRezRMbBMYBTZNMDzAtFOLQYZArYYYAAcR5R5tPrPTmoZfiYkGt4O3SLQeRs7K7JE2L2P6hxhrKCQsqzP0POQxKcJxPzMqC3KgPy48wN+JjSsQh8NqRBqau8k65RgZAtpG0ZLrK66rDeYQ+DNFEYEWnboqA9aJ2GaJtpQW4Ukw3jYtDb6wARnlkca1j9Jatr00hyQomiGAACISeZPgKaiABGRMAGYABZKgAOk5lJZPkwslKMbA9CgbA0vgfDVERjwdIbAJmHRqAyYSZJzJJ5b9vQYmeVZHlhxbByeKiOBxcRva6PQSoSe1kjUeOwDTpOc6Vvc6GAcZV4T3A+6ksexyUJe6rPNthD5WNnlRkt-8HvI-UOV+EmXZ9g2IZ5GmbdqmgFeTspU7W0ICYktMCkp6naZgBnmZZmAea6otjHMVLJ3cGAACkIGnIaLyMBQEFAa0Jtlqa9cV055tV0wnd7AoCbgCBJygC2U74XWukH5Colb6cTaCyoQGame54X7O+EOwNsLtz6zotU2hP+t1roiz2nSj9PnqqtP3vPz8TZL2-o4fuOeQJxjEneOb03zeyiAYOQvhv6Lx3nvWe0Bf7ASPKcWIfBhDFXjs-WGfs341XlvVAOK98bNQLh1OSNd+Y6GAJYRAipYDAGwCLQgzhXBS3GgTVeGcyaFTmmZDI6g8ZPS9PQvAf137gI+vKNK4jkHXT4cVFAGdzCqmnFoIBL8vT+zToQmAOi1o9Hzq1VMFD8xAA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
