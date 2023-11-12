import java.io.*;
import java.net.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        while (true) {
            try {
                 // Mencoba membuat koneksi ke server dengan alamat "localhost" dan port 12345
                Socket socket = new Socket("192.168.43.226", 12345);
                System.out.println("Terhubung ke server. ");

                // Membuat aliran untuk menerima pesan dari server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Membuat aliran untuk mengirim pesan ke server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Membaca memasukkan nama client
                Scanner scanner = new Scanner(System.in);
                System.out.print("Masukkan nama anda : ");

                // Thread untuk menerima pesan dari server
                Thread receiveThread = new Thread(() -> {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {

                            // Menampilkan pesan dari server
                            System.out.println(" " + message);
                        }
                    } catch (IOException e) { 

                        // Menampilkan pesan jika koneksi dengan server terputus
                        System.out.println("Server : Disconnect");
                        System.out.println(e.getMessage());

                    }
                });
                receiveThread.start();

                while (true) {
                    System.out.print("");

                    // Membaca pesan dari pengguna dan mengirimkannya ke server
                    String message = scanner.nextLine();
                    out.println(message);
                }
            } catch (IOException e) {
                // Menampilkan pesan jika gagal terhubung ke server
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

