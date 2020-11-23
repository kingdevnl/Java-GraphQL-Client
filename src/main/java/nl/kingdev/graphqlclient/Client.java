package nl.kingdev.graphqlclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import nl.kingdev.graphqlclient.query.Query;
import nl.kingdev.graphqlclient.result.QueryResult;
import nl.kingdev.graphqlclient.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;


public class Client {

    private String uri;
    private Map<String, String> headers;
    private Map<String, String> globalVariables;
    private static Gson gson = new GsonBuilder().create();

    public Client(String uri) {
        this.uri = uri;
        this.headers = new HashMap<>();
        this.globalVariables = new HashMap<>();
    }


    private JsonObject makeQueryJson(Query query) {
        JsonObject request = new JsonObject();
        request.addProperty("query", query.getQuery());

        JsonObject variables = new JsonObject();
        query.getVariables().forEach((k, v) -> variables.addProperty(k, v.toString()));
        request.add("variables", variables);

        return request;
    }

    public QueryResult query(Query query) {
        try {
            query.getVariables().putAll(globalVariables);
            JsonObject request = makeQueryJson(query);
            return new QueryResult(gson.fromJson(HttpUtil.post(this.uri, request.toString(), headers), JsonObject.class).get("data").getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public String getUri() {
        return uri;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Client setHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public Client setGlobalVariable(String name, String value) {
        this.globalVariables.put(name, value);
        return this;
    }

    public Client removeHeader(String name) {
        this.headers.remove(name);
        return this;
    }

    public Client removeGlobalVariable(String name) {
        this.globalVariables.remove(name);
        return this;
    }

    public static Gson getGson() {
        return gson;
    }
}
