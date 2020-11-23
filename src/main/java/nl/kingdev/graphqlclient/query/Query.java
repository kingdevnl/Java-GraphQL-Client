package nl.kingdev.graphqlclient.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Getter
@ToString
public class Query {

    private final String query;

    public Query(String query) {
        this.query = query;
    }

    public Query() {
        this.query = "";
    }


    private JsonObject variables = new JsonObject();

    public Query setVariable(String name, String value) {
        this.variables.addProperty(name, value);
        return this;
    }

    public Query setVariable(String name, JsonObject value) {
        this.variables.add(name, value);
        return this;
    }

    public Query removeVariable(String name) {
        this.variables.remove(name);
        return this;
    }

    public static Query fromFile(InputStream file) {
        byte[] bytes;
        try {
            bytes = file.readAllBytes();
            return new Query(new String(bytes, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Query fromFile(String file) {
        return fromFile(Query.class.getResourceAsStream(file));
    }

    public static Query fromFile(Object owner, String file) {
        return fromFile(owner.getClass().getResourceAsStream(file));
    }
}
