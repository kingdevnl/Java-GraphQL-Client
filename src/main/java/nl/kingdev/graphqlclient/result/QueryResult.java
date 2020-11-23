package nl.kingdev.graphqlclient.result;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nl.kingdev.graphqlclient.Client;

import java.util.List;

public class QueryResult {

    private JsonObject result;

    public QueryResult(JsonObject result) {
        this.result = result;
    }

    public <T> T first(String name, Class<T> type) {
        return Client.getGson().fromJson(result.get(name), type);
    }
    public <T> List<T> get(String name) {
        JsonArray array = result.get(name).getAsJsonArray();
        return Client.getGson().fromJson(array, new TypeToken<T>() {}.getType());
    }
}
