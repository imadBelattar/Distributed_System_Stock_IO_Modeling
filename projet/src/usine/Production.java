package usine;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Production {

    private Socket socket;
    private int port;

    public Production(int port) {
        try {
            this.socket = new Socket("stock_node", port);
        } catch (IOException e) {
            System.out.println("The production has failed to connect to stock");
            throw new RuntimeException(e);
        }
        this.port = port;
    }

    // Method to send a PRODUCE message to stock
    private void send_a_cartone() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            out.write("PRODUCE");
            out.newLine();  // Add newline character to mark end of message
            out.flush();    // Ensure message is sent immediately
            System.out.println("Sent: PRODUCE (Carton produced)");
        } catch (IOException e) {
            System.out.println("Failed to send PRODUCE message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int[] seconds = {1, 2, 3, 4, 5, 6};  // Array of seconds to randomly choose from
        Random random = new Random();
        Production production = new Production(1010);

        while (true) {
            production.send_a_cartone();
            try {
                Thread.sleep(seconds[random.nextInt(seconds.length)] * 1000);  // Random sleep interval
            } catch (InterruptedException e) {
                System.err.println("Production interrupted: " + e.getMessage());
            }
        }
    }
}
