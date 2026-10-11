import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GradeCalculatorServer {

    private static final int PORT = 8082;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT), 0
        );

        server.createContext(
                "/calculate",
                GradeCalculatorServer::calculate
        );

        server.createContext(
                "/",
                GradeCalculatorServer::handleOptions
        );

        server.setExecutor(null);
        server.start();

        System.out.println("======================================");
        System.out.println("     STUDENT GRADE CALCULATOR");
        System.out.println("======================================");
        System.out.println("Backend started successfully!");
        System.out.println("Running at: http://localhost:" + PORT);
        System.out.println("Waiting for frontend requests...");
    }

    // CALCULATE RESULT
    private static void calculate(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405,
                    "{\"message\":\"Method not allowed.\"}");
            return;
        }

        try {
            String query = exchange.getRequestURI().getRawQuery();

            double mark1 = getMark(query, "mark1");
            double mark2 = getMark(query, "mark2");
            double mark3 = getMark(query, "mark3");
            double mark4 = getMark(query, "mark4");
            double mark5 = getMark(query, "mark5");

            double total = mark1 + mark2 + mark3 + mark4 + mark5;
            double percentage = total / 5.0;

            String grade = calculateGrade(percentage);

            String json = "{"
                    + "\"total\":" + format(total) + ","
                    + "\"percentage\":" + format(percentage) + ","
                    + "\"grade\":\"" + grade + "\""
                    + "}";

            sendJson(exchange, 200, json);

            System.out.println("Total: " + total);
            System.out.println("Percentage: " + percentage + "%");
            System.out.println("Grade: " + grade);

        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400,
                    "{\"message\":\"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            e.printStackTrace();

            sendJson(exchange, 500,
                    "{\"message\":\"Internal server error.\"}");
        }
    }

    // GET AND VALIDATE MARKS
    private static double getMark(String query, String name) {

        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing marks. Please enter all five subjects.");
        }

        String[] parameters = query.split("&");

        for (String parameter : parameters) {
            String[] pair = parameter.split("=", 2);

            if (pair.length == 2 && pair[0].equals(name)) {
                try {
                    double mark = Double.parseDouble(pair[1]);

                    if (!Double.isFinite(mark) || mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                                "Marks must be between 0 and 100.");
                    }

                    return mark;

                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Please enter valid numeric marks.");
                }
            }
        }

        throw new IllegalArgumentException(
                "Missing marks for " + name + ".");
    }

    // GRADE CALCULATION
    private static String calculateGrade(double percentage) {

        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B";
        } else if (percentage >= 60) {
            return "C";
        } else if (percentage >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    // FORMAT NUMBERS
    private static String format(double number) {
        return String.format(Locale.US, "%.2f", number);
    }

    // CORS HEADERS
    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, OPTIONS");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");
    }

    // HANDLE OPTIONS REQUEST
    private static void handleOptions(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
        } else {
            sendJson(exchange, 404,
                    "{\"message\":\"Endpoint not found.\"}");
        }

        exchange.close();
    }

    // SEND JSON RESPONSE
    private static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String json
    ) throws IOException {

        addCorsHeaders(exchange);

        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}