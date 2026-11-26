package com.example.orderdemo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderEndpointsTest {

    private static Server server;
    private static int port;

    @BeforeClass
    public static void startServer() throws Exception {
        server = OrderDemoApplication.createServer(0);
        server.start();
        port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void healthEndpointReturnsUp() throws IOException {
        HttpResponse response = sendGet("/health");
        assertEquals(200, response.status);

        JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
        assertEquals("UP", json.get("status").getAsString());
        assertEquals("legacy-order-demo", json.get("service").getAsString());
    }

    @Test
    public void indexPageIsServed() throws IOException {
        HttpResponse response = sendGet("/");
        assertEquals(200, response.status);
        assertTrue(response.body.contains("Order Calculator"));
    }

    @Test
    public void calculateEndpointReturnsBreakdown() throws IOException {
        HttpResponse response = sendPost(
                "/api/orders/calculate",
                "{\"price\": 25.00, \"quantity\": 2, \"discount\": 10}"
        );
        assertEquals(200, response.status);

        JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
        assertEquals(new BigDecimal("50.00"), json.get("subtotal").getAsBigDecimal());
        assertEquals(new BigDecimal("5.00"), json.get("discountAmount").getAsBigDecimal());
        assertEquals(new BigDecimal("45.00"), json.get("total").getAsBigDecimal());
    }

    @Test
    public void calculateEndpointHandlesZeroQuantity() throws IOException {
        HttpResponse response = sendPost(
                "/api/orders/calculate",
                "{\"price\": 19.99, \"quantity\": 0, \"discount\": 15}"
        );
        assertEquals(200, response.status);

        JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
        assertEquals(0, json.get("quantity").getAsInt());
        assertEquals(new BigDecimal("0.00"), json.get("total").getAsBigDecimal());
    }

    @Test
    public void calculateEndpointRejectsInvalidInputs() throws IOException {
        assertBadRequest("{\"price\": -5, \"quantity\": 2, \"discount\": 0}");
        assertBadRequest("{\"price\": 10, \"quantity\": 1.5, \"discount\": 0}");
        assertBadRequest("{\"price\": 10, \"quantity\": 2, \"discount\": 120}");
        assertBadRequest("not-json");
        assertBadRequest("");
    }

    private void assertBadRequest(String body) throws IOException {
        HttpResponse response = sendPost("/api/orders/calculate", body);
        assertEquals(400, response.status);
        JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
        assertTrue(json.has("error"));
    }

    private HttpResponse sendGet(String path) throws IOException {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    private HttpResponse sendPost(String path, String jsonBody) throws IOException {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(conn);
    }

    private HttpResponse readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStream stream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
        StringBuilder sb = new StringBuilder();
        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        }
        return new HttpResponse(status, sb.toString());
    }

    private static final class HttpResponse {
        final int status;
        final String body;

        HttpResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }
}
