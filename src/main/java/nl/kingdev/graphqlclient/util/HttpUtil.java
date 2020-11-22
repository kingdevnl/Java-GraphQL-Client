package nl.kingdev.graphqlclient.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class HttpUtil {

    private static String urlEncode(String inURL) throws Exception {
        URL url = new URL(inURL);
        return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                url.getPath(), url.getQuery(), url.getRef()).toASCIIString();
    }

    public static String post(String requestURL, String payload) throws Exception {
        URL url = new URL(urlEncode(requestURL));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        streamWriter.write(payload);
        streamWriter.close();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        StringBuffer response = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }

        bufferedReader.close();
        connection.disconnect();

        return response.toString();
    }
}
