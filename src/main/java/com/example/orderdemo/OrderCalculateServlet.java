package com.example.orderdemo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servlet handling order total calculation requests at /api/orders/calculate.
 */
public class OrderCalculateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderCalculator calculator = new OrderCalculator();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String body = readBody(req);
        if (body.trim().isEmpty()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is required.");
            return;
        }

        try {
            JsonElement parsed = JsonParser.parseString(body);
            if (!parsed.isJsonObject()) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body must be a JSON object.");
                return;
            }

            JsonObject json = parsed.getAsJsonObject();
            BigDecimal price = parseRequiredDecimal(json, "price", "Product price");
            int quantity = parseRequiredInteger(json, "quantity", "Quantity");
            BigDecimal discount = parseOptionalDecimal(json, "discount", BigDecimal.ZERO, "Discount percentage");

            OrderCalculationResult result = calculator.calculate(price, quantity, discount);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(result));
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (JsonParseException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON payload.");
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private BigDecimal parseRequiredDecimal(JsonObject json, String key, String label) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return toBigDecimal(json.get(key), label);
    }

    private BigDecimal parseOptionalDecimal(JsonObject json, String key, BigDecimal defaultValue, String label) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return defaultValue;
        }
        return toBigDecimal(json.get(key), label);
    }

    private BigDecimal toBigDecimal(JsonElement element, String label) {
        if (!element.isJsonPrimitive() || element.getAsJsonPrimitive().isBoolean()) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }
        try {
            return new BigDecimal(element.getAsString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }
    }

    private int parseRequiredInteger(JsonObject json, String key, String label) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        JsonElement element = json.get(key);
        if (!element.isJsonPrimitive() || element.getAsJsonPrimitive().isBoolean()) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
        try {
            BigDecimal decimal = new BigDecimal(element.getAsString().trim());
            return decimal.intValueExact();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        Map<String, String> error = new LinkedHashMap<String, String>();
        error.put("error", message);
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(error));
    }
}
