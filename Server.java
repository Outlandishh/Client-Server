package Application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

// Create the server class
public class Server {

    public static void main (String args[]) throws IOException {

        // Create the socket and assign a port number for it to listen on
        ServerSocket serverSocket = new ServerSocket(6789);
        System.out.println("Listening on Port 6789\n");

        // Start the menu which calls the MenuThread.java file
        new MenuThread().start();

        // The server must accept incoming connections on the port specified above (6789)
        while(true) {
            Socket socket = serverSocket.accept();

            // New thread for client
            new EchoThread(socket).start();

            // Identify the client attempting to connect via IP address
            InetAddress ip = socket.getInetAddress();
            int port = socket.getPort();

            // Print to the console which client is connected (IP) and on which port
            System.out.print("New client connected from: " + ip + ":" + port + "\n");
        }
    }
}