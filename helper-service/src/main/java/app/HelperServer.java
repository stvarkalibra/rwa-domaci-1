package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HelperServer {

    private static final int PORT = 8081;

    private static final List<Quote> QUOTES = Arrays.asList(
            new Quote("Be yourself; everyone else is already taken.", "Oscar Wilde"),
            new Quote("Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.", "Albert Einstein"),
            new Quote("So many books, so little time.", "Frank Zappa"),
            new Quote("Stay hungry, stay foolish.", "Steve Jobs"),
            new Quote("Knowledge is power.", "Francis Bacon")
    );

    private static final Random RANDOM = new Random();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Helper server running on port " + PORT);

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
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                        true
                )
        ) {
            String request = in.readLine();
            System.out.println("Helper received request: " + request);

            Quote quoteOfTheDay = QUOTES.get(RANDOM.nextInt(QUOTES.size()));
            String json = MAPPER.writeValueAsString(quoteOfTheDay);

            out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}