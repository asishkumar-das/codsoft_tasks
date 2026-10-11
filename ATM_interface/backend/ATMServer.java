import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


// ============================================
// ATM SERVER CLASS
// ============================================

public class ATMServer {

    private static final int PORT = 8080;


    // User's bank account
    private static final BankAccount account =
            new BankAccount(10000);


    // ==========================================
    // MAIN
    // ==========================================

    public static void main(String[] args)
            throws Exception {


        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );


        // API endpoints

        server.createContext(
                "/balance",
                ATMServer::checkBalance
        );


        server.createContext(
                "/deposit",
                ATMServer::deposit
        );


        server.createContext(
                "/withdraw",
                ATMServer::withdraw
        );


        server.createContext(
                "/reset",
                ATMServer::reset
        );


        server.createContext(
                "/",
                ATMServer::handleOptions
        );


        server.setExecutor(null);


        server.start();


        System.out.println(
                "======================================"
        );

        System.out.println(
                "          ATM INTERFACE"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "ATM Backend Started Successfully!"
        );

        System.out.println(
                "Running at: http://localhost:"
                        + PORT
        );

        System.out.println(
                "Initial Balance: ₹"
                        + account.checkBalance()
        );

        System.out.println(
                "Waiting for frontend requests..."
        );
    }


    // ==========================================
    // CHECK BALANCE
    // ==========================================

    private static void checkBalance(
            HttpExchange exchange
    ) throws IOException {


        addCorsHeaders(exchange);


        double balance =
                account.checkBalance();


        String json =
                "{"
                + "\"success\":true,"
                + "\"balance\":" + balance
                + "}";


        sendJson(
                exchange,
                200,
                json
        );
    }


    // ==========================================
    // DEPOSIT
    // ==========================================

    private static void deposit(
            HttpExchange exchange
    ) throws IOException {


        addCorsHeaders(exchange);


        try {

            double amount =
                    getAmount(exchange);


            if (amount <= 0) {

                sendResult(
                        exchange,
                        false,
                        "Deposit amount must be greater than zero."
                );

                return;
            }


            boolean success =
                    account.deposit(amount);


            if (success) {

                sendResult(
                        exchange,
                        true,
                        "₹" +
                        String.format(
                                "%.2f",
                                amount
                        ) +
                        " deposited successfully."
                );

            } else {

                sendResult(
                        exchange,
                        false,
                        "Invalid deposit amount."
                );
            }


        } catch (Exception e) {

            sendResult(
                    exchange,
                    false,
                    "Invalid amount."
            );
        }
    }


    // ==========================================
    // WITHDRAW
    // ==========================================

    private static void withdraw(
            HttpExchange exchange
    ) throws IOException {


        addCorsHeaders(exchange);


        try {

            double amount =
                    getAmount(exchange);


            if (amount <= 0) {

                sendResult(
                        exchange,
                        false,
                        "Withdrawal amount must be greater than zero."
                );

                return;
            }


            // Insufficient balance
            if (amount >
                    account.checkBalance()) {

                sendResult(
                        exchange,
                        false,
                        "Insufficient balance."
                );

                return;
            }


            boolean success =
                    account.withdraw(amount);


            if (success) {

                sendResult(
                        exchange,
                        true,
                        "₹" +
                        String.format(
                                "%.2f",
                                amount
                        ) +
                        " withdrawn successfully."
                );

            } else {

                sendResult(
                        exchange,
                        false,
                        "Withdrawal failed."
                );
            }


        } catch (Exception e) {

            sendResult(
                    exchange,
                    false,
                    "Invalid amount."
            );
        }
    }


    // ==========================================
    // RESET
    // ==========================================

    private static void reset(
            HttpExchange exchange
    ) throws IOException {


        addCorsHeaders(exchange);


        account.reset();


        sendResult(
                exchange,
                true,
                "Account reset successfully."
        );
    }


    // ==========================================
    // GET AMOUNT
    // ==========================================

    private static double getAmount(
            HttpExchange exchange
    ) {

        String query =
                exchange
                    .getRequestURI()
                    .getQuery();


        if (query == null) {

            throw new IllegalArgumentException(
                    "Amount missing"
            );
        }


        String[] parts =
                query.split("=");


        if (parts.length != 2) {

            throw new IllegalArgumentException(
                    "Invalid amount"
            );
        }


        return Double.parseDouble(
                parts[1]
        );
    }


    // ==========================================
    // SEND RESULT
    // ==========================================

    private static void sendResult(
            HttpExchange exchange,
            boolean success,
            String message
    ) throws IOException {


        double balance =
                account.checkBalance();


        String json =
                "{"
                + "\"success\":" + success + ","
                + "\"message\":\""
                + message
                + "\","
                + "\"balance\":"
                + balance
                + "}";


        sendJson(
                exchange,
                200,
                json
        );
    }


    // ==========================================
    // CORS
    // ==========================================

    private static void addCorsHeaders(
            HttpExchange exchange
    ) {

        exchange
            .getResponseHeaders()
            .set(
                "Access-Control-Allow-Origin",
                "*"
            );


        exchange
            .getResponseHeaders()
            .set(
                "Access-Control-Allow-Methods",
                "GET, OPTIONS"
            );


        exchange
            .getResponseHeaders()
            .set(
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


        exchange
            .getResponseHeaders()
            .set(
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