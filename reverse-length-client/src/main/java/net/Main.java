package net;
import view.*;
import java.util.Arrays;
import java.util.HashSet;

public class Main {
    static ReverseLengthClient echoClient;
    private static final HashSet<String> types = new HashSet<>(Arrays.asList("normal", "reverse", "length"));

    public static void main(String[] args) {
        Item[] items = {
                Item.of("start session", Main::startSession),
                Item.of("exit", Main::Exit, true)
        };
        Menu menu = new Menu("Reverse-Length Client Application", items);
        menu.perform(new StandardInputOutput());
    }

    static void startSession(InputOutput io) {
        String host = io.readString("Enter hostname");
        int port = io.readNumberRange("Enter port", "Wrong port", 3000, 50000).intValue();
        if (echoClient != null) {
            echoClient.close();
        }
        echoClient = new ReverseLengthClient(host, port);

        Menu menu = new Menu("Run Session",
                Item.of("Send request", Main::stringProcessing),
                Item.ofExit());
        menu.perform(io);
    }

    static void Exit(InputOutput io) {
        if (echoClient != null) {
            echoClient.close();
        }
    }

    static void stringProcessing(InputOutput io) {
        String typeOptions = String.join(", ", types);
        String type = io.readStringOptions("Enter request type (" + typeOptions + ")",
                "Invalid type, please choose again", types);
        String input = io.readString("Enter string for processing");
        String request = type + ":" + input;
        String response = echoClient.sendAndReceive(request);
        io.writeLine("Response: " + response);
    }
}