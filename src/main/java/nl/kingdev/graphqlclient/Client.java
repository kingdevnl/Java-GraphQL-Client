package nl.kingdev.graphqlclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nl.kingdev.graphqlclient.query.Query;
import nl.kingdev.graphqlclient.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Client {

    private String uri;
    private Map<String, String> headers;
    private static Gson gson = new GsonBuilder().create();

    public Client(String uri) {
        this.uri = uri;
        this.headers = new HashMap<>();
    }


    private JsonObject makeQueryJson(Query query) {
        JsonObject request = new JsonObject();
        request.addProperty("query", query.getQuery());

        JsonObject variables = new JsonObject();
        query.getVariables().forEach((k, v) -> variables.addProperty(k, v.toString()));
        request.add("variables", variables);

        return request;
    }

    public <T> T first(Query query, String name, Class<T> type) {

        try {
            JsonObject request = makeQueryJson(query);
            JsonObject result = gson.fromJson(HttpUtil.post(this.uri, request.toString()), JsonObject.class);
            return gson.fromJson(result.get("data").getAsJsonObject().get(name), type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> List<T> query(Query query, String name, Class<T> type) {

        try {
            JsonObject request = makeQueryJson(query);
            JsonObject result = gson.fromJson(HttpUtil.post(this.uri, request.toString()), JsonObject.class);
            JsonArray array = result.get("data").getAsJsonObject().get(name).getAsJsonArray();
            return gson.fromJson(array, new TypeToken<T>(){}.getType());
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

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

}
