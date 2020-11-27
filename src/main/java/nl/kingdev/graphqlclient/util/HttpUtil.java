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

package nl.kingdev.graphqlclient.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpUtil {

    private CloseableHttpClient client = HttpClients.createDefault();


    public String post(String requestURL, String payload, Map<String, String> headers) throws Exception {

        HttpPost post = new HttpPost(requestURL);
        StringEntity stringEntity = new StringEntity(payload);
        headers.forEach(post::setHeader);
        post.setEntity(stringEntity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        HttpResponse response = client.execute(post);

        byte[] bytes = response.getEntity().getContent().readAllBytes();
        String body = new String(bytes, Charset.defaultCharset());
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            Gson gson = new GsonBuilder().create();
            JsonObject error = gson.fromJson(body, JsonObject.class);
            throw new IOException("Error while doing post request " + requestURL + " "+ payload + "\r\nStatusCode: " + response.getStatusLine().getStatusCode() + " \r\nError: " + error.get("errors"));
        }

        return body;
    }
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
