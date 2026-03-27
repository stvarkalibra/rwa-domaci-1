package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainServer {

    private static final int PORT = 8080;
    private static final QuoteRepository repository = new QuoteRepository();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Main server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
                OutputStream out = socket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            System.out.println("Main received: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            if ("GET".equals(method) && "/quotes".equals(path)) {
                handleGetQuotes(out, in);
            } else if ("POST".equals(method) && "/save-quote".equals(path)) {
                handleSaveQuote(out, in);
            } else {
                sendResponse(out, "404 Not Found", "<h1>404 Not Found</h1>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleGetQuotes(OutputStream out, BufferedReader in) throws IOException {
        consumeHeaders(in);

        List<Quote> allQuotes = repository.getAllQuotes();
        Quote qod = repository.getQuoteOfTheDay();

        String html = HTMLRenderer.renderPage(allQuotes, qod);
        sendResponse(out, "200 OK", html);
    }

    private static void handleSaveQuote(OutputStream out, BufferedReader in) throws IOException {
        int contentLength = 0;
        String line;

        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        char[] bodyChars = new char[contentLength];
        in.read(bodyChars);
        String formData = new String(bodyChars);

        String quote = "";
        String author = "";

        for (String pair : formData.split("&")) {
            String[] kv = pair.split("=");
            String key = kv[0];
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";

            if (key.equals("quote")) quote = value;
            if (key.equals("author")) author = value;
        }

        if (!quote.isBlank() && !author.isBlank()) {
            repository.addQuote(new Quote(quote, author));
        }

        String response =
                "HTTP/1.1 302 Found\r\n" +
                        "Location: /quotes\r\n\r\n";

        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    private static void consumeHeaders(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            // samo preskačemo header linije
        }
    }

    private static void sendResponse(OutputStream out, String status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        String headers =
                "HTTP/1.1 " + status + "\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + bytes.length + "\r\n\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
        out.flush();
    }
}