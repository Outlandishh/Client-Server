package Application;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// This file is used to take the data sent to us by the client and store it in the correct directory and
// in the correct format.
public class EchoThread extends Thread {

    protected Socket socket;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {

        // Read in the files that have been sent
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder out = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                out.append(line + "\n");
            }

            String clientString = out.toString();
            bufferedReader.close();

            // Save the file in the correct location (/user/Invoices)
            String path = System.getProperty("user.home") + File.separator + "Invoices";

            // Check if the directory exists, and if it doesn't, make it.
            File directory = new File(path);
            if(!directory.exists()) {
                directory.mkdir();
            }

            // This re-writes the file name to be relative to the date/time that it was uploaded.
            if(clientString.length() > 0) {
                ZonedDateTime zdt = ZonedDateTime.now();
                String time = zdt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                time = time.replaceAll(":", "-");

                String saveAs = null;

                if(clientString.startsWith("{")) {
                    saveAs = ".json";
                } else if (clientString.startsWith("<")) {
                    saveAs = ".xml";
                } else {
                    saveAs = ".txt";
                }

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + File.separator + time + saveAs, true));

                // Create a char array that contains all characters of the invoice
                char[] charArray = clientString.toCharArray();
                // For each char in the array, append it to the file
                for(char c: charArray) {
                    bufferedWriter.append(c);
                    bufferedWriter.flush();
                }

                bufferedWriter.close();

                // If successful, print the updated file name
                System.out.println("File saved as: " + saveAs);

            }

        // Exception handling
        } catch (IOException e) {
            System.out.print(e);
            return;
        }
    }

}