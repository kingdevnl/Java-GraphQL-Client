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

package nl.kingdev.graphqlclient.subscription;


import lombok.Getter;
import nl.kingdev.graphqlclient.Client;
import nl.kingdev.graphqlclient.subscription.callbacks.ISubscriptionDataCallback;
import nl.kingdev.graphqlclient.util.JsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;

@Getter
public class Subscription {

    private int id;
    private ISubscriptionDataCallback callback;
    private Client client;

    public Subscription(int id, ISubscriptionDataCallback callback,  Client client) {
        this.id = id;
        this.callback = callback;
        this.client = client;
    }

    public void close() {
        this.client.closeSubscription(this);
    }

}
