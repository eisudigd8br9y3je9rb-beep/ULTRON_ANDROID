package com.ultron.assistant.drone;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DroneBridge {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8765;

    public static String sendCommand(String command) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), 1500);
            socket.setSoTimeout(2500);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            out.write(command);
            out.newLine();
            out.flush();

            String response = in.readLine();
            return response == null ? "No response from drone simulator." : response;
        } catch (Exception e) {
            return "Drone simulator is offline. Start drone_controller.py first.";
        }
    }
}
