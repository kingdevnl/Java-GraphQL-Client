/*
 * MIT License
 *
 * Copyright (c) 2020 KingdevNL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nl.kingdev.graphqlclient;


import nl.kingdev.graphqlclient.query.Query;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClientTest {

    private static final String uri = "https://graphqlzero.almansi.me/api";

    @Test
    public void createClient() {
        Client client = makeClient();
        assertNotNull("Client is null", client);
        assertEquals("Uri is not set", uri, client.getUri());
        client.closeClient();
    }

    @Test
    public void headers() {
        Client client = makeClient();
        client.setHeader("auth", "someToken");
        assertEquals("Auth header is not set", "someToken", client.getHeader("auth"));
        client.closeClient();
    }

    @Test
    public void queryOne() {
        Client client = makeClient();
        User user = client.query(new Query("query user($id: ID!) { user(id: $id) { id, name, email } }")
                .setVariable("id", "1"))
                .get("user", User.class);


        System.out.println(user);

        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());
        client.closeClient();
    }

    @Test
    public void testGlobalVariable() {
        Client client = makeClient();
        client.setGlobalVariable("id", "1");
        User user = client.query(new Query("query user($id: ID!) { user(id: $id) { id, name, email } }"))
                .get("user", User.class);
        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());
        client.closeClient();
    }


    @Test
    public void query() {
        Client client = new Client("https://api.mocki.io/v1/44cb3920");
        List<Todo> getTodos = client.query(new Query("query todos {getTodos {id, description, done}}")).get("getTodos");
        assertNotNull(getTodos);
        assertTrue(getTodos.size() > 1);
        client.closeClient();
    }

    @Test
    public void file() {
        Client client = makeClient();
        User user = client.query(
                Query.fromFile(this, "/users.graphql")
                        .setVariable("id", "1")
        ).get("user", User.class);

        assertNotNull("User is null", user);
        assertEquals("1", user.getId());
        assertEquals("Leanne Graham", user.getName());
        assertEquals("Sincere@april.biz", user.getEmail());
        client.closeClient();
    }


    private Client makeClient() {
        return new Client(uri);
    }

}