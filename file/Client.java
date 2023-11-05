import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        while (true) {
            try {
                Socket socket = new Socket("localhost", 12345);
                System.out.println("Terhubung ke server.");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in);

                Thread receiveThread = new Thread(() -> {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println(" " + message);
                        }
                    } catch (IOException e) {
                        System.out.println("Server : Disconnect");
                        System.out.println(e.getMessage());

                    }
                });
                receiveThread.start();

                while (true) {
                    System.out.print("");
                    String message = scanner.nextLine();
                    out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Gagal terhubung ke server. Akan mencoba lagi dalam beberapa detik.");
                try {
                    Thread.sleep(5000); // Tunggu 5 detik sebelum mencoba menghubungkan ulang
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

