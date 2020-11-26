package nl.kingdev.graphqlclient.subscription;


import lombok.Getter;
import nl.kingdev.graphqlclient.Client;
import nl.kingdev.graphqlclient.subscription.callbacks.ISubscriptionDataCallback;
import nl.kingdev.graphqlclient.util.JsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;

@Getter
public class Subscription {

    private WebSocketClient websocket;
    private int id;
    private ISubscriptionDataCallback callback;

    public Subscription(WebSocketClient websocket, int id, ISubscriptionDataCallback callback) {
        this.id = id;
        this.callback = callback;
        this.websocket = websocket;
    }

    public void close() {
        this.websocket.send(new JsonBuilder()
                .append("id", getId())
                .append("type", "stop")
                .toString());
    }

}
