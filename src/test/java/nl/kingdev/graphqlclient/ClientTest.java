package nl.kingdev.graphqlclient;

import nl.kingdev.graphqlclient.query.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void query() {
        Client client = makeClient();
        User user = client.query(
                new Query("query user($id: ID!) { user(id: $id) { id, name, email } }")
                .setVariable("id", "1"),
                "user",
                User.class
        );
        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());

    }
    private Client makeClient() {
        return new Client(uri);
    }

}