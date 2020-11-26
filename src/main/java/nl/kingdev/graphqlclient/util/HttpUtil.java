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
