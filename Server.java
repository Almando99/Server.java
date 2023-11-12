import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static List<Socket> clientSockets = new ArrayList<>();
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // Membuat ServerSocket yang mendengarkan koneksi pada port 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server berjalan, menunggu koneksi...");

            while (true) {
                // Menerima koneksi dari klien
                Socket clientSocket = serverSocket.accept();

                // Menerima nama klien saat terhubung
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientName = reader.readLine();
                System.out.println(clientName +" " + "Terhubung ke server.");
                System.out.println("Alamat IP " + clientName + " : " + clientSocket.getInetAddress().getHostAddress());

                if (isNameUnique(clientName)) {
                    clientSockets.add(clientSocket);
                    // Menambahkan socket klien dan handler ke daftar
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientName);
                    clientHandlers.add(clientHandler);

                    // Memulai thread untuk menangani klien
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                } else {
                    // Jika nama klien tidak unik, beri tahu klien dan tutup koneksi
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    writer.println("Nama klien telah digunakan. Silakan coba dengan nama lain.");

                    // Menutup socket client
                    clientSocket.close();
                }

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
            // Mengirim pesan ke semua klien yang terhubung
            for (ClientHandler handler : clientHandlers) {
                handler.sendMessage(message);
            }
        }
    }

    private static boolean isNameUnique(String name) {
        synchronized (clientHandlers) {
            // Memeriksa apakah nama klien unik
            for (ClientHandler handler : clientHandlers) {
                if (handler.getClientName().equals(name)) {
                    return false;
                }
            }
        }
        return true;
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    
    //untuk menangani klien
    public ClientHandler(Socket socket, String clientName) {
        this.clientSocket = socket;
        this.clientName = clientName;
    }

    @Override
    public void run() {
        try {
            //untuk membuka dan menutup stream
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            //untuk menampikan pesan dari klien
            while ((message = in.readLine()) != null) {
                System.out.println("Pesan dari " + clientName + ": " + message);

                // Mengirim pesan ke semua klien
                Server.broadcastMessage("Pesan dari " + clientName + ": " + message);
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Client " + clientName + ": Disconnect");
        }
    }

    public void sendMessage(String message) {
        //untuk mengirim pesan kepada klien
        out.println(message);
    }

    public String getClientName() {
        //untuk mendapatkan nama klien
        return clientName;
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
