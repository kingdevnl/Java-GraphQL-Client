# Java-GraphQL-Client

Simple & Lightweight GraphQL client for Java With Async and subscriptions Support!

[![Build Status](https://ci.kingdev.nl/buildStatus/icon?job=Java-GraphQL-Client%2Fmain)](https://ci.kingdev.nl/job/Java-GraphQL-Client/job/main/) 



The dependencies of this project are:
  -  Google GSON (https://mvnrepository.com/artifact/com.google.code.gson/gson)
  - Apache HttpClient (https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient)
  - Java-WebSocket (https://github.com/TooTallNate/Java-WebSocket)

```java
    
    //Data class
    public class User {
        public String id, name, email;
    }

    //Create a client object
    Client client = new Client("https://api.mocki.io/v1/44cb3920");
    
    //For example we can set global variables and headers
    client.setGlobalVariable("someId", "1");
    client.setHeader("auth", "someAuthToken")

    //Find a user in the GraphQL api
    User user = client.query(
            new Query("query user($id: ID!) { user(id: $id) { id, name, email } }").setVariable("id", "1"))
            .get("user", User.class);
    System.out.println(user)

    //Subscriptions 
    client.setupWebsocket(() -> {
        System.out.println("Ready!");
        client.subscribe(new Query("subscription {commentAdded}"), data -> System.out.println("commentAdded: "+data.getRawJson()));
    }, (code, reason, remote)-> System.out.println("Websocket was close code: "+code + " reason: "+reason));

   //To close the client call
   client.closeClient();

```