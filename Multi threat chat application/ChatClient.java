import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             Scanner scanner = new Scanner(System.in)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            new Thread(new ServerListener(socket)).start();

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.println("You can start chatting now.");

            while (true) {
                String message = scanner.nextLine();
                writer.println(name + ": " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ServerListener implements Runnable {
        private Socket socket;

        public ServerListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String serverMessage;
                while ((serverMessage = reader.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (Exception e) {
                System.out.println("Disconnected from server.");
            }
        }
    }
}