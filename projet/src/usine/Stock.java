package usine;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Stock {
    private final int NB_PLACES;
    private int current_Cartones;
    private final int port;
    private ExecutorService executorService;

    public Stock(int nb_places, int port) {
        this.NB_PLACES = nb_places;
        this.current_Cartones = 0;
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Stock Server is running on port " + port);

            // Create a fixed thread pool with 10 threads for handling client requests
            executorService = Executors.newFixedThreadPool(10);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Submit the client handling task to the thread pool
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message = in.readLine();  // Read one message
                if (message != null) {
                    System.out.println("Received: " + message);
                    if (message.equals("PRODUCE")) {
                        receive_a_new_cartone();
                    } else if (message.equals("DELIVER")) {
                        deliver_a_cartone();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close(); // Ensure the socket is closed after processing
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method called when Production produces a new cartone.
    public synchronized boolean receive_a_new_cartone() {
        if (this.current_Cartones < NB_PLACES) {
            current_Cartones++;
            System.out.println("Cartone added. Current Cartones in stock: " + current_Cartones + "/" + NB_PLACES);
            return true;
        }
        System.out.println("Stock is full. Current Cartones in stock: " + current_Cartones + "/" + NB_PLACES);
        return false;
    }

    // Method called when Delivery delivers a cartone.
    public synchronized boolean deliver_a_cartone() {
        if (this.current_Cartones > 0) {
            current_Cartones--;
            System.out.println("Cartone delivered. Current Cartones in stock: " + current_Cartones + "/" + NB_PLACES);
            return true;
        }
        System.out.println("No cartone to deliver. Current Cartones in stock: " + current_Cartones + "/" + NB_PLACES);
        return false;
    }

    public static void main(String[] args) {
        Stock stock = new Stock(10, 8080); // 10 places in stock, listening on port 8080
        stock.start();
    }
}
