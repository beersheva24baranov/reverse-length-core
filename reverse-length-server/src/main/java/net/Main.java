package net;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
public class Main {
    private static final int PORT = 8081;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            runSession(socket);
        }
    }

    private static void runSession(Socket socket) {
        Map<String, Function<String, String>> handlers = getHandlers();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream())) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    writer.println("Invalid request format. Use: <type>:<string>");
                } else {
                    String type = parts[0].trim();
                    String data = parts[1];

                    Function<String, String> handler = handlers.get(type);
                    if (handler != null) {
                        writer.println(handler.apply(data));
                    } else {
                        writer.println("Unknown request type: " + type);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Client closed connection abnormally.");
        }
    }

    private static Map<String, Function<String, String>> getHandlers() {
        Map<String, Function<String, String>> handlers = new HashMap<>();
        handlers.put("reverse", data -> new StringBuilder(data).reverse().toString());
        handlers.put("length", data -> String.valueOf(data.length()));
        handlers.put("normal", data -> data);
        return handlers;
    }
}