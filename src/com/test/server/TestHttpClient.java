package com.test.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class TestHttpClient {

    public static void askServer(String uri) throws Exception{

        String url = "http://localhost:8080/"+uri;
        int numberOfRequests = 100;
        int numberOfThreads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> sendRequest(url));
        }

        executor.shutdown();

    }

    private static void sendRequest(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");


            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();

            System.out.println("Response (" + status + "): " + content.toString());
        } catch (Exception e) {
            System.out.println("❌ Exception during request: " + e.getMessage());
        }

    }
}
