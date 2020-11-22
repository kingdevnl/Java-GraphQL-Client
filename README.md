# Java-GraphQL-Client

Simple GraphQL client for Java
The only dependecy is google gson!

[![Build Status](https://ci.kingdev.nl/buildStatus/icon?job=Java-GraphQL-Client%2Fmain)](https://ci.kingdev.nl/job/Java-GraphQL-Client/job/main/) 


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
    User user = client.first(
            new Query("query user($id: ID!) { user(id: $id) { id, name, email } }").setVariable("id", "1"),
               "user",
               User.class
        );
    System.out.println(user)

```