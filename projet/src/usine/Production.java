package usine;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Production {

    private String stockNode;
    private int port;

    public Production(String stockNode, int port) {
        this.stockNode = stockNode;
        this.port = port;
    }

    // Method to send a PRODUCE message to stock
    private void send_a_cartone() {
        try (Socket socket = new Socket(stockNode, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            out.write("PRODUCE");
            out.newLine();  // Add newline character to mark end of message
            out.flush();    // Ensure message is sent immediately
            System.out.println("Sent: PRODUCE (Carton produced)");

        } catch (IOException e) {
            System.err.println("Failed to produce a cartone: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int[] seconds = {1, 2, 3, 4, 5, 6};  // Array of seconds to randomly choose from
        Random random = new Random();

        // Update: Create Production object with stock node and port
        Production production = new Production("stock", 8080);  // stock_node is the Docker node name

        while (true) {
            production.send_a_cartone();
            try {
                // Sleep for a random number of seconds between productions (converted to milliseconds)
                Thread.sleep(seconds[random.nextInt(seconds.length)] * 1000);
            } catch (InterruptedException e) {
                System.err.println("Production has been interrupted: " + e.getMessage());
            }
        }
    }
}
