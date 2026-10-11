import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;


public class QuizServer {

    private static final int PORT = 8080;


    // ==============================
    // QUESTION CLASS
    // ==============================

    static class Question {

        String question;

        String[] options;

        int correctAnswer;


        Question(
                String question,
                String[] options,
                int correctAnswer) {

            this.question = question;

            this.options = options;

            this.correctAnswer = correctAnswer;

        }

    }


    // ==============================
    // QUESTIONS
    // ==============================

    static List<Question> questions =
            new ArrayList<>();


    public static void main(String[] args)
            throws Exception {


        createQuestions();


        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );


        // QUESTIONS API

        server.createContext(
                "/questions",
                QuizServer::handleQuestions
        );


        // CHECK ANSWER API

        server.createContext(
                "/check",
                QuizServer::handleCheck
        );


        // OPTIONS / CORS

        server.createContext(
                "/",
                QuizServer::handleRoot
        );


        server.setExecutor(null);


        server.start();


        System.out.println(
                "======================================"
        );

        System.out.println(
                "       QUIZ APPLICATION SERVER"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Java Backend Started Successfully!"
        );

        System.out.println(
                "Running at: http://localhost:8080"
        );

        System.out.println(
                "Questions: " + questions.size()
        );

        System.out.println(
                "Time per question: 10 seconds"
        );

        System.out.println(
                "Waiting for frontend requests..."
        );

    }


    // ==============================
    // CREATE QUESTIONS
    // ==============================

    private static void createQuestions() {


        questions.add(
                new Question(

                        "Which language is primarily used to style web pages?",

                        new String[]{
                                "HTML",
                                "CSS",
                                "Java",
                                "SQL"
                        },

                        1
                )
        );


        questions.add(
                new Question(

                        "Which keyword is used to create a class in Java?",

                        new String[]{
                                "function",
                                "define",
                                "class",
                                "object"
                        },

                        2
                )
        );


        questions.add(
                new Question(

                        "Which SQL clause is used to filter groups?",

                        new String[]{
                                "WHERE",
                                "ORDER BY",
                                "HAVING",
                                "SELECT"
                        },

                        2
                )
        );


        questions.add(
                new Question(

                        "Which data structure follows FIFO?",

                        new String[]{
                                "Stack",
                                "Queue",
                                "Tree",
                                "Graph"
                        },

                        1
                )
        );


        questions.add(
                new Question(

                        "Which symbol starts a single-line comment in Java?",

                        new String[]{
                                "#",
                                "//",
                                "<!--",
                                "--"
                        },

                        1
                )
        );

    }


    // ==============================
    // QUESTIONS HANDLER
    // ==============================

    private static void handleQuestions(
            HttpExchange exchange)
            throws IOException {


        addCorsHeaders(exchange);


        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(204, -1);

            return;

        }


        StringBuilder json =
                new StringBuilder();

        json.append("[");


        for (int i = 0;
             i < questions.size();
             i++) {


            Question q =
                    questions.get(i);


            json.append("{");


            json.append("\"question\":\"")
                    .append(escapeJson(q.question))
                    .append("\",");


            json.append("\"options\":[");


            for (int j = 0;
                 j < q.options.length;
                 j++) {


                json.append("\"")
                        .append(
                                escapeJson(q.options[j])
                        )
                        .append("\"");


                if (j < q.options.length - 1) {

                    json.append(",");

                }

            }


            json.append("]");


            json.append("}");


            if (i < questions.size() - 1) {

                json.append(",");

            }

        }


        json.append("]");


        sendResponse(
                exchange,
                json.toString()
        );

    }


    // ==============================
    // CHECK ANSWER
    // ==============================

    private static void handleCheck(
            HttpExchange exchange)
            throws IOException {


        addCorsHeaders(exchange);


        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(204, -1);

            return;

        }


        try {


            String query =
                    exchange.getRequestURI()
                            .getRawQuery();


            int questionIndex =
                    getParameter(
                            query,
                            "question"
                    );


            int answerIndex =
                    getParameter(
                            query,
                            "answer"
                    );


            if (
                    questionIndex < 0 ||
                    questionIndex >= questions.size()
            ) {


                sendResponse(
                        exchange,
                        "{\"error\":\"Invalid question\"}"
                );

                return;

            }


            Question question =
                    questions.get(questionIndex);


            boolean correct =
                    answerIndex ==
                    question.correctAnswer;


            String json =
                    "{"
                    + "\"correct\":"
                    + correct
                    + ","
                    + "\"correctAnswer\":"
                    + question.correctAnswer
                    + "}";


            sendResponse(
                    exchange,
                    json
            );

        }

        catch (Exception e) {


            sendResponse(
                    exchange,
                    "{\"error\":\"Invalid request\"}"
            );

        }

    }


    // ==============================
    // GET PARAMETER
    // ==============================

    private static int getParameter(
            String query,
            String name) {


        if (query == null) {

            return -1;

        }


        String[] params =
                query.split("&");


        for (String param : params) {


            String[] pair =
                    param.split("=");


            if (
                    pair.length == 2 &&
                    pair[0].equals(name)
            ) {


                try {

                    return Integer.parseInt(
                            URLDecoder.decode(
                                    pair[1],
                                    StandardCharsets.UTF_8
                            )
                    );

                }

                catch (Exception e) {

                    return -1;

                }

            }

        }


        return -1;

    }


    // ==============================
    // ROOT HANDLER
    // ==============================

    private static void handleRoot(
            HttpExchange exchange)
            throws IOException {


        addCorsHeaders(exchange);


        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            return;

        }


        sendResponse(
                exchange,
                "{\"status\":\"Quiz Backend Running\"}"
        );

    }


    // ==============================
    // SEND RESPONSE
    // ==============================

    private static void sendResponse(
            HttpExchange exchange,
            String response)
            throws IOException {


        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "*"
                );


        exchange.sendResponseHeaders(
                200,
                bytes.length
        );


        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(bytes);

        }

    }


    // ==============================
    // CORS
    // ==============================

    private static void addCorsHeaders(
            HttpExchange exchange) {


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "*"
                );


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Methods",
                        "GET, OPTIONS"
                );


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );

    }


    // ==============================
    // JSON ESCAPE
    // ==============================

    private static String escapeJson(
            String text) {


        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");

    }

}