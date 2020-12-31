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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import nl.kingdev.graphqlclient.query.Query;
import nl.kingdev.graphqlclient.result.Result;
import nl.kingdev.graphqlclient.subscription.Subscription;
import nl.kingdev.graphqlclient.subscription.WebsocketClient;
import nl.kingdev.graphqlclient.subscription.callbacks.ICloseCallback;
import nl.kingdev.graphqlclient.subscription.callbacks.IReadyCallback;
import nl.kingdev.graphqlclient.subscription.callbacks.ISubscriptionDataCallback;
import nl.kingdev.graphqlclient.util.Cache;
import nl.kingdev.graphqlclient.util.HttpUtil;
import nl.kingdev.graphqlclient.util.JsonBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {

    private static final Gson gson = new GsonBuilder().create();
    private final String uri;
    private final Map<String, String> headers;
    private final JsonObject globalVariables;
    private ExecutorService executorService;
    private WebsocketClient websocketClient;
    private int subscriptionID = 1;
    @Getter()
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final HttpUtil httpUtil = new HttpUtil();

    private Cache<Result> resultCache;

    public Client(String uri) {
        this.uri = uri;
        this.headers = new HashMap<>();
        this.globalVariables = new JsonObject();
    }

    public static Gson getGson() {
        return gson;
    }

    private JsonObject makeQueryJson(Query query) {
        JsonObject request = new JsonObject();
        request.addProperty("query", query.getQuery());

        JsonObject variables = new JsonObject();

        query.getVariables().entrySet().forEach(e -> variables.add(e.getKey(), e.getValue()));

        globalVariables.entrySet().forEach(e -> variables.add(e.getKey(), e.getValue()));

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
            if(resultCache != null) {
                return resultCache.remember(query.toString(), () -> {
                    JsonObject request = makeQueryJson(query);
                    try {
                        return new Result(this.httpUtil.post(this.uri, request.toString(), headers).get("data").getAsJsonObject());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
            }
            JsonObject request = makeQueryJson(query);
            try {
                return new Result(this.httpUtil.post(this.uri, request.toString(), headers).get("data").getAsJsonObject());
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    /**
     * setups the client-side cache
     * @param ttl time to live
     */
    public void useCache(long ttl) {
        this.resultCache = new Cache<>(ttl);
    }

    /**
     * Creates a websocket to be used for subscriptions (Live data)
     *
     * @param readyCallback Called when the websocket is ready
     * @param closeCallback Called when the websocket closes
     */
    public void setupWebsocket(IReadyCallback readyCallback, ICloseCallback closeCallback) {
        try {
            this.websocketClient = new WebsocketClient(new URI(uri), readyCallback, closeCallback, this);
            this.websocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to a live data feed with the provided query and callback
     *
     * @param query    The query for the subscription
     * @param callback The callback to be called for incoming data
     * @return Subscription
     */
    public Subscription subscribe(Query query, ISubscriptionDataCallback callback) {
        if (this.websocketClient != null) {
            this.websocketClient.send(new JsonBuilder()
                    .append("id", subscriptionID)
                    .append("type", "start")
                    .append("payload", makeQueryJson(query))
                    .toString());
            Subscription subscription = new Subscription(subscriptionID, callback, this);
            subscriptions.add(subscription);
            subscriptionID++;
            return subscription;
        }
        System.err.println("Failed to subscribe, There was no websocket setup, Be sure to call Client#setupWebsocket");

        return null;
    }

    /**
     * Closes all the Subscriptions
     */
    public void closeSubscriptions() {
        if (this.websocketClient != null) {
            Iterator<Subscription> iterator = subscriptions.iterator();

            while (iterator.hasNext()) {
                iterator.next().close();
                iterator.remove();
            }
        } else {
            System.err.println("Failed to closeSubscriptions, There was no websocket setup, Be sure to call Client#setupWebsocket");
        }
    }

    /**
     * Closes the client
     */
    public void closeClient() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
        if (this.websocketClient != null) {
            closeSubscriptions();
            this.websocketClient.close();
        }
        this.httpUtil.close();
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

    public void closeSubscription(Subscription subscription) {
        this.websocketClient.send(new JsonBuilder()
                .append("id", subscription.getId())
                .append("type", "stop")
                .toString());
        this.subscriptions.remove(subscription);
    }
}
