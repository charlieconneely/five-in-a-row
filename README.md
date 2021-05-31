## Five in a Row Coding challenge 

### Installation 
```
git clone <repo>
cd five-in-a-row
mvn clean install 
```

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


### Dependencies
- [Apache Maven 3.8.1](https://maven.apache.org/)
- [Java HttpServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html)
- [Java HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)
- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/) - Used to create mock methods.
- [wiremock.org](http://wiremock.org/docs/) - An HTTP mock server. 
- [Apache HttpComponents](https://hc.apache.org/) - Used to mimic HTTP requests for WebServer unit tests.

### Testing

### Potential Future Work