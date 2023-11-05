import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static List<Socket> clientSockets = new ArrayList<>();
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server berjalan, menunggu koneksi...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client terhubung: " + clientSocket.getInetAddress());
                clientSockets.add(clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

                // Mulai thread untuk mengirim pesan ke semua klien
                Thread senderThread = new Thread(new MessageSender());
                senderThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                handler.sendMessage(message);
            }
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Pesan dari " + clientSocket.getInetAddress() + ": " + message);

                // Mengirim pesan ke semua klien
                Server.broadcastMessage("Pesan dari " + clientSocket.getInetAddress() + ": " + message);
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Client : Disconnect");
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}

class MessageSender implements Runnable {
    @Override
    public void run() {
        while (true) {
            // Baca pesan dari input (console) server
            Scanner scanner = new Scanner(System.in);
            System.out.print("");
            String message = scanner.nextLine();

            // Mengirim pesan ke semua klien
            Server.broadcastMessage("Server: " + message);
        }
    }
}
