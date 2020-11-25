package nl.kingdev.graphqlclient.subscription.callbacks;

import nl.kingdev.graphqlclient.result.Result;

public interface ISubscriptionDataCallback {
    void onData(Result data);
}
