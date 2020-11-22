package nl.kingdev.graphqlclient.query;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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
