## Five-in-a-Row Coding Challenge 
**Author: Charlie Conneely** 

This project was written in Java, using Maven, and JUnit for testing. 
***

5-in-a-Row, a variation of the famous Connect Four game, is a two-player connection game
in which the players take turns dropping an **x** or an **o** from the
top into a nine-column, six-row, vertically suspended grid. The pieces fall straight down,
occupying the next available space within the column. The objective of the game is to be the
first to form a horizontal, vertical, or diagonal line of five of one's own letter.

#### Gameplay Instructions
On the running the client application and connecting to the server, the user will
be asked to enter their name. <br>
When the server is running on port _p_, two clients can connect and join the game on 
that port. If two clients are already connected, "Sorry, game is full" will display and
the app will close.<br>
When two players have connected, they will take turns entering a column (1-9) or 'Q' to quit. The 
first player to reach five in a row wins. <br>
If one client quits in the middle of a game, the other will see "Waiting for
opponent to join...". If another client joins, the game will restart. 

A short of clip of the gameplay can be found [here](https://youtu.be/OXCla1QqVjg).

***
### Installation 
```
git clone <repo>
cd five-in-a-row
mvn clean install 
```
***
### Running the program from CLI (on Windows)
From inside `/five-in-a-row`:

*First, create the jar executables*:
```
mvn package
```

*Running the server*:
```
java -jar .\target\FiveInARow-server-jar-with-dependencies.jar <PORT>
```

*Running a client*:
```
java -jar .\target\FiveInARow-client-jar-with-dependencies.jar <PORT>
```
If you omit the port argument the programs will run on the default
port 8082.  

To test the application from an IDE, run the main methods inside 
`Server/networking/WebServer.java` and `Client/Application.java`.
***
### Testing
All unit tests are stored inside `src/test/java`. These cover:
- Server/BoardGrid.java
- Server/GameManager.java
- Server/networking/WebServer.java
- Client/networking/WebClient.java
***
### Future Work
- Increase test coverage on the client side.
- Implement Behaviour/Integration testing. 
***
### Dependencies
- [Apache Maven 3.8.1](https://maven.apache.org/)
- [Java HttpServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html)
- [Java HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)
- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/) - Used to create mock methods.
- [wiremock.org](http://wiremock.org/docs/) - An HTTP mock server. 
- [Apache HttpComponents](https://hc.apache.org/) - Used to mimic HTTP requests for WebServer unit tests.

