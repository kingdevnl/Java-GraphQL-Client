package nl.kingdev.graphqlclient.query;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
    public static Query fromFile(Class owner, String file) {
        return fromFile(owner.getResourceAsStream(file));
    }
    public static Query fromFile(Object owner, String file) {
        return fromFile(owner.getClass().getResourceAsStream(file));
    }

    private Map<String, Object> variables = new HashMap<>();

    public Query setVariable(String name, Object value) {
        this.variables.put(name, value);
        return this;
    }

    public Query removeVariable(String name) {
        this.variables.remove(name);
        return this;
    }

}
