package nl.kingdev.graphqlclient.query;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Query {

    private String query;


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

    @Override
    public String toString() {
        return "Query{" +
                "query='" + query + '\'' +
                ", variables=" + variables +
                '}';
    }
}
