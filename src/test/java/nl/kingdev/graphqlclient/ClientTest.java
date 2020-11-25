package nl.kingdev.graphqlclient;


import lombok.ToString;
import nl.kingdev.graphqlclient.query.Query;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ClientTest {

    private static final String uri = "https://graphqlzero.almansi.me/api";

    @Test
    public void createClient() {
        Client client = makeClient();
        assertNotNull("Client is null", client);
        assertEquals("Uri is not set", uri, client.getUri());
    }

    @Test
    public void headers() {
        Client client = makeClient();
        client.setHeader("auth", "someToken");
        assertEquals("Auth header is not set", "someToken", client.getHeader("auth"));
    }

    @Test
    public void queryOne() {
        Client client = makeClient();
        User user = client.query(new Query("query user($id: ID!) { user(id: $id) { id, name, email } }")
                .setVariable("id", "1"))
                .first("user", User.class);


        System.out.println(user);

        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());

    }

    @Test
    public void testGlobalVariable() {
        Client client = makeClient();
        client.setGlobalVariable("id", "1");
        User user = client.query(new Query("query user($id: ID!) { user(id: $id) { id, name, email } }"))
                .first("user", User.class);
        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());
    }
    

    private class Todo {
        String id, description;
        boolean completed;

        @Override
        public String toString() {
            return "Todo{" +
                    "id='" + id + '\'' +
                    ", description='" + description + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }

    @Test
    public void query() {
        Client client = new Client("https://api.mocki.io/v1/44cb3920");
        List<Todo> getTodos = client.query(new Query("query todos {getTodos {id, description, done}}")).get("getTodos");
        assertNotNull(getTodos);
        assertTrue(getTodos.size() > 1);
    }

    @Test
    public void file() {
        Client client = makeClient();
        User user = client.query(
                Query.fromFile(this, "/users.graphql")
                        .setVariable("id", "1")
        ).first("user", User.class);

        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());

    }

    @ToString
    static class Photo {
        private String id, url;
    }


    private Client makeClient() {
        return new Client(uri);
    }

}