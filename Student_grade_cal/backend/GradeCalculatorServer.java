import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class GradeCalculatorServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );


        // Calculate API
        server.createContext(
                "/calculate",
                GradeCalculatorServer::calculate
        );


        // CORS
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
        System.out.println(
                "Running at: http://localhost:" + PORT
        );
        System.out.println(
                "Waiting for frontend requests..."
        );
    }


    // ==========================================
    // CALCULATE
    // ==========================================

    private static void calculate(
            HttpExchange exchange
    ) throws IOException {


        addCorsHeaders(exchange);


        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            return;
        }


        try {

            String query =
                    exchange.getRequestURI()
                            .getQuery();


            // Get marks
            double mark1 =
                    getMark(query, "mark1");

            double mark2 =
                    getMark(query, "mark2");

            double mark3 =
                    getMark(query, "mark3");

            double mark4 =
                    getMark(query, "mark4");

            double mark5 =
                    getMark(query, "mark5");


            // ======================================
            // TOTAL
            // ======================================

            double total =
                    mark1 +
                    mark2 +
                    mark3 +
                    mark4 +
                    mark5;


            // ======================================
            // AVERAGE PERCENTAGE
            // ======================================

            double percentage =
                    total / 5;


            // ======================================
            // GRADE
            // ======================================

            String grade =
                    calculateGrade(percentage);


            // ======================================
            // JSON RESPONSE
            // ======================================

            String json =
                    "{"
                    + "\"total\":" + format(total) + ","
                    + "\"percentage\":" + format(percentage) + ","
                    + "\"grade\":\"" + grade + "\""
                    + "}";


            sendJson(
                    exchange,
                    200,
                    json
            );


            System.out.println(
                    "Marks: "
                    + mark1 + ", "
                    + mark2 + ", "
                    + mark3 + ", "
                    + mark4 + ", "
                    + mark5
            );

            System.out.println(
                    "Total: " + total
            );

            System.out.println(
                    "Percentage: " + percentage + "%"
            );

            System.out.println(
                    "Grade: " + grade
            );

        }

        catch (Exception e) {

            e.printStackTrace();

            sendJson(
                    exchange,
                    400,
                    "{\"message\":\"Invalid marks.\"}"
            );
        }
    }


    // ==========================================
    // GET MARK
    // ==========================================

    private static double getMark(
            String query,
            String name
    ) {

        if (query == null) {
            throw new IllegalArgumentException(
                    "Missing query"
            );
        }


        String[] parameters =
                query.split("&");


        for (String parameter : parameters) {

            String[] pair =
                    parameter.split("=");


            if (pair.length == 2
                    && pair[0].equals(name)) {

                double mark =
                        Double.parseDouble(pair[1]);


                if (mark < 0 || mark > 100) {

                    throw new IllegalArgumentException(
                            "Marks must be 0-100"
                    );
                }


                return mark;
            }
        }


        throw new IllegalArgumentException(
                "Missing " + name
        );
    }


    // ==========================================
    // GRADE CALCULATION
    // ==========================================

    private static String calculateGrade(
            double percentage
    ) {

        if (percentage >= 90) {
            return "A+";
        }

        else if (percentage >= 80) {
            return "A";
        }

        else if (percentage >= 70) {
            return "B";
        }

        else if (percentage >= 60) {
            return "C";
        }

        else if (percentage >= 50) {
            return "D";
        }

        else {
            return "F";
        }
    }


    // ==========================================
    // FORMAT NUMBER
    // ==========================================

    private static String format(
            double number
    ) {

        if (number == (long) number) {

            return String.valueOf(
                    (long) number
            );
        }

        return String.format(
                "%.2f",
                number
        );
    }


    // ==========================================
    // CORS
    // ==========================================

    private static void addCorsHeaders(
            HttpExchange exchange
    ) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }


    // ==========================================
    // OPTIONS
    // ==========================================

    private static void handleOptions(
            HttpExchange exchange
    ) throws IOException {

        addCorsHeaders(exchange);

        exchange.sendResponseHeaders(
                204,
                -1
        );
    }


    // ==========================================
    // SEND JSON
    // ==========================================

    private static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String json
    ) throws IOException {


        byte[] response =
                json.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );


        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );


        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(response);
        }
    }
}