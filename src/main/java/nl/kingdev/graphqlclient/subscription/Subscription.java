package nl.kingdev.graphqlclient.subscription;


import lombok.Getter;
import nl.kingdev.graphqlclient.subscription.callbacks.ISubscriptionDataCallback;

@Getter
public class Subscription {

    private int id;
    private ISubscriptionDataCallback callback;

    public Subscription(int id, ISubscriptionDataCallback callback) {
        this.id = id;
        this.callback = callback;
    }

}
