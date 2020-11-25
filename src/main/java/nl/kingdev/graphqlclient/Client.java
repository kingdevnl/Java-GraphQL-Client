package nl.kingdev.graphqlclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import nl.kingdev.graphqlclient.query.Query;
import nl.kingdev.graphqlclient.result.Result;
import nl.kingdev.graphqlclient.subscription.*;
import nl.kingdev.graphqlclient.subscription.callbacks.ICloseCallback;
import nl.kingdev.graphqlclient.subscription.callbacks.IReadyCallback;
import nl.kingdev.graphqlclient.subscription.callbacks.ISubscriptionDataCallback;
import nl.kingdev.graphqlclient.util.HttpUtil;
import nl.kingdev.graphqlclient.util.JsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {

    private String uri;
    private Map<String, String> headers;
    private JsonObject globalVariables;
    private ExecutorService executorService;
    private WebSocketClient webSocketClient;
    private int subscriptionID = 1;
    private List<Subscription> subscriptions = new ArrayList<>();
    private static Gson gson = new GsonBuilder().create();


    public Client(String uri) {
        this.uri = uri;
        this.headers = new HashMap<>();
        this.globalVariables = new JsonObject();
    }


    private JsonObject makeQueryJson(Query query) {
        JsonObject request = new JsonObject();
        request.addProperty("query", query.getQuery());

        JsonObject variables = new JsonObject();

        query.getVariables().entrySet().forEach(e -> {
            variables.add(e.getKey(), e.getValue());
        });


        globalVariables.entrySet().forEach(e -> {
            variables.add(e.getKey(), e.getValue());
        });

        request.add("variables", variables);

        return request;
    }

    /**
     * queries the graphql server with the provided
     *
     * @param query The query to execute
     * @return Result
     */
    public Result query(Query query) {
        try {
            JsonObject request = makeQueryJson(query);
            return new Result(gson.fromJson(HttpUtil.post(this.uri, request.toString(), headers), JsonObject.class).get("data").getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * async queries the graphql server with the provided
     *
     * @param query The query to execute
     * @return Result
     */
    public CompletableFuture<Result> queryAsync(Query query) {
        if (this.executorService != null) {
            CompletableFuture<Result> future = new CompletableFuture<>();

            this.executorService.submit(() -> {
                future.complete(this.query(query));
            });

            return future;
        }
        System.err.println("Failed to submit async query, Be sure to call Client#setupMultithreading first!");
        return null;
    }

    public void setupMultithreading(int numOfThreads) {
        this.executorService = Executors.newFixedThreadPool(numOfThreads);
    }
    public void closeMultithreading() {
        this.executorService.shutdown();
    }

    /**
     * Creates a websocket to be used for subscriptions (Live data)
     */
    public void setupWebsocket(IReadyCallback readyCallback, ICloseCallback closeCallback) {
        try {
            this.webSocketClient = new WebSocketClient(new URI(this.uri), new GraphQLDraft()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    send(new JsonBuilder().append("type", "connection_init").toString());
                    readyCallback.ready();
                }

                @Override
                public void onMessage(String dataString) {
                    JsonObject data = gson.fromJson(dataString, JsonObject.class);

                    if (data.get("type").getAsString().equals("data")) {
                        int id = data.get("id").getAsInt();
                        Subscription subscription = subscriptions.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
                        if (subscription != null) {
                            JsonObject payload = data.get("payload").getAsJsonObject();
                            subscription.getCallback().onData(new Result(payload.get("data").getAsJsonObject()));
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    closeCallback.close(code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.webSocketClient.connect();
    }

    /**
     * Subscribe to a live data feed with the provided query and callback
     *
     * @param query    The query for the subscription
     * @param callback The callback to be called for incoming data
     */
    public void subscribe(Query query, ISubscriptionDataCallback callback) {
        if (this.webSocketClient != null) {
            this.webSocketClient.send(new JsonBuilder()
                    .append("id", subscriptionID)
                    .append("type", "start")
                    .append("payload", makeQueryJson(query))
                    .toString());
            subscriptions.add(new Subscription(subscriptionID, callback));
            subscriptionID++;
        } else {
            System.err.println("Failed to subscribe, There was no websocket setup, Be sure to call Client#setupWebsocket");

        }
    }

    /**
     * Closes all the Subscriptions
     */
    public void closeSubscriptions() {
        if (this.webSocketClient != null) {
            subscriptions.forEach(subscription -> {
                this.webSocketClient.send(new JsonBuilder()
                        .append("id", subscription.getId())
                        .append("type", "stop")
                        .toString());
            });
        } else {
            System.err.println("Failed to closeSubscriptions, There was no websocket setup, Be sure to call Client#setupWebsocket");
        }
    }

    /**
     * Closes the websocket
     */
    public void closeWebsocket() {
        closeSubscriptions();
        if (this.webSocketClient != null) {
            this.webSocketClient.close();
        }
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
        this.globalVariables.addProperty(name, value);
        return this;
    }

    public Client setGlobalVariable(String name, JsonObject value) {
        this.globalVariables.add(name, value);
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
