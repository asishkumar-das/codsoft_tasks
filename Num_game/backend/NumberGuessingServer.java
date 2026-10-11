import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class NumberGuessingServer {

    // Server settings
    private static final int PORT = 8080;
    private static final int MAX_ATTEMPTS = 7;

    // Game variables
    private static int secretNumber;
    private static int attempts = 0;
    private static int score = 0;

    private static final Random random = new Random();

    public static void main(String[] args) throws Exception {

        // Start first round
        generateNewNumber();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT), 0
        );

        // API endpoints
        server.createContext("/guess", NumberGuessingServer::handleGuess);
        server.createContext("/new-round", NumberGuessingServer::handleNewRound);
        server.createContext("/reset", NumberGuessingServer::handleReset);

        server.setExecutor(null);

        server.start();

        System.out.println("=================================");
        System.out.println(" Number Guessing Game Backend");
        System.out.println("=================================");
        System.out.println("Server started successfully!");
        System.out.println("Running at: http://localhost:" + PORT);
        System.out.println("Waiting for frontend requests...");
    }

    // ==========================================
    // GENERATE RANDOM NUMBER
    // ==========================================

    private static void generateNewNumber() {
        secretNumber = random.nextInt(100) + 1;
        attempts = 0;

        System.out.println("New secret number generated.");
    }

    // ==========================================
    // GUESS API
    // ==========================================

    private static void handleGuess(HttpExchange exchange) throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("number=")) {
            sendJson(exchange, 400,
                    "{\"message\":\"Invalid guess.\"}");
            return;
        }

        try {

            int guess = Integer.parseInt(
                    query.substring("number=".length())
            );

            if (guess < 1 || guess > 100) {
                sendJson(exchange, 400,
                        "{\"message\":\"Guess must be between 1 and 100.\"}");
                return;
            }

            // If attempts already finished
            if (attempts >= MAX_ATTEMPTS) {

                String json =
                        "{"
                        + "\"attempts\":" + attempts + ","
                        + "\"score\":" + score + ","
                        + "\"correct\":false,"
                        + "\"result\":\"GAME_OVER\","
                        + "\"message\":\"No attempts left. Start a new round!\","
                        + "\"gameOver\":true"
                        + "}";

                sendJson(exchange, 200, json);
                return;
            }

            attempts++;

            // ======================================
            // CORRECT GUESS
            // ======================================

            if (guess == secretNumber) {

                int roundScore =
                        Math.max(10, 100 - ((attempts - 1) * 10));

                score += roundScore;

                String json =
                        "{"
                        + "\"attempts\":" + attempts + ","
                        + "\"score\":" + score + ","
                        + "\"correct\":true,"
                        + "\"result\":\"CORRECT\","
                        + "\"message\":\"Correct! You found the number! +"
                        + roundScore + " points\","
                        + "\"gameOver\":true"
                        + "}";

                sendJson(exchange, 200, json);

            }

            // ======================================
            // GUESS TOO LOW
            // ======================================

            else if (guess < secretNumber) {

                boolean gameOver = attempts >= MAX_ATTEMPTS;

                String message;

                if (gameOver) {
                    message = "Too low! No attempts left. "
                            + "The number was " + secretNumber;
                } else {
                    message = "Too low! Try a higher number.";
                }

                String json =
                        "{"
                        + "\"attempts\":" + attempts + ","
                        + "\"score\":" + score + ","
                        + "\"correct\":false,"
                        + "\"result\":\"LOW\","
                        + "\"message\":\"" + message + "\","
                        + "\"gameOver\":" + gameOver
                        + "}";

                sendJson(exchange, 200, json);
            }

            // ======================================
            // GUESS TOO HIGH
            // ======================================

            else {

                boolean gameOver = attempts >= MAX_ATTEMPTS;

                String message;

                if (gameOver) {
                    message = "Too high! No attempts left. "
                            + "The number was " + secretNumber;
                } else {
                    message = "Too high! Try a lower number.";
                }

                String json =
                        "{"
                        + "\"attempts\":" + attempts + ","
                        + "\"score\":" + score + ","
                        + "\"correct\":false,"
                        + "\"result\":\"HIGH\","
                        + "\"message\":\"" + message + "\","
                        + "\"gameOver\":" + gameOver
                        + "}";

                sendJson(exchange, 200, json);
            }

        } catch (NumberFormatException e) {

            sendJson(exchange, 400,
                    "{\"message\":\"Please enter a valid number.\"}");
        }
    }

    // ==========================================
    // NEW ROUND
    // ==========================================

    private static void handleNewRound(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        generateNewNumber();

        String json =
                "{"
                + "\"message\":\"New round started!\","
                + "\"attempts\":0,"
                + "\"score\":" + score
                + "}";

        sendJson(exchange, 200, json);
    }

    // ==========================================
    // RESET GAME
    // ==========================================

    private static void handleReset(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        score = 0;

        generateNewNumber();

        String json =
                "{"
                + "\"message\":\"Game reset successfully!\","
                + "\"attempts\":0,"
                + "\"score\":0"
                + "}";

        sendJson(exchange, 200, json);
    }

    // ==========================================
    // CORS
    // ==========================================

    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }

    // ==========================================
    // SEND JSON RESPONSE
    // ==========================================

    private static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String json
    ) throws IOException {

        byte[] response =
                json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(response);
        }
    }
}