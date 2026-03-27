package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HelperClient {

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HelperClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Quote getQuoteOfTheDay() {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                        true
                );
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                )
        ) {
            out.println("GET_QOD");

            String json = in.readLine();
            if (json == null || json.isBlank()) {
                return new Quote("Quote of the day is currently unavailable.", "System");
            }

            return objectMapper.readValue(json, Quote.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Quote("Quote of the day is currently unavailable.", "System");
        }
    }
}