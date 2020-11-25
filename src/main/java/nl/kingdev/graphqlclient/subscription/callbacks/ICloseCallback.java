package nl.kingdev.graphqlclient.subscription.callbacks;

public interface ICloseCallback {

    void close(int code, String reason, boolean remote);
}
