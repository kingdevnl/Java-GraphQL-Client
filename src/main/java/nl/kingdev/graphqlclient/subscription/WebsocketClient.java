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

import com.google.gson.JsonObject;
import nl.kingdev.graphqlclient.Client;
import nl.kingdev.graphqlclient.result.Result;
import nl.kingdev.graphqlclient.subscription.callbacks.ICloseCallback;
import nl.kingdev.graphqlclient.subscription.callbacks.IReadyCallback;
import nl.kingdev.graphqlclient.util.JsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebsocketClient extends WebSocketClient {
    private final IReadyCallback readyCallback;
    private final ICloseCallback closeCallback;
    private final Client client;

    public WebsocketClient(URI serverUri, IReadyCallback readyCallback, ICloseCallback closeCallback, Client client) {
        super(serverUri);
        this.readyCallback = readyCallback;
        this.closeCallback = closeCallback;
        this.client = client;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send(new JsonBuilder().append("type", "connection_init").toString());
        readyCallback.ready();
    }


    @Override
    public void onMessage(String dataString) {
        JsonObject data = Client.getGson().fromJson(dataString, JsonObject.class);

        if (data.get("type").getAsString().equals("data")) {
            int id = data.get("id").getAsInt();
            Subscription subscription = client.getSubscriptions().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
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
}
