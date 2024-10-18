package usine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

public class Delivery {
    private String stockNode;
    private int port;

    public Delivery(String stockNode, int port) {
        this.stockNode = stockNode;
        this.port = port;
    }

    private void deliver_a_cartone() {
        try (Socket socket = new Socket(stockNode, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            out.write("DELIVER");
            out.newLine();
            out.flush();
            System.out.println("A cartone is being delivered \n");

        } catch (IOException exp) {
            System.err.println("Failed to deliver a cartone: " + exp.getMessage());
        }
    }

    public static void main(String[] args) {
        Delivery delivery = new Delivery("stock_node", 1010);  // stock_node is the Docker node name
        int[] seconds = {1, 2, 3, 4, 5, 6};
        Random random = new Random();

        while (true) {
            delivery.deliver_a_cartone();
            try {
                // Sleep for a random number of seconds between deliveries (converted to milliseconds)
                Thread.sleep(seconds[random.nextInt(seconds.length)] * 1000);
            } catch (InterruptedException exp) {
                System.err.println("Delivery has been interrupted: " + exp.getMessage());
            }
        }
    }
}
