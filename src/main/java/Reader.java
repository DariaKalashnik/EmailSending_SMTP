import utils.BuildConfig;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static utils.Utils.send;

public class Reader {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String host, password;
    private int port;

    private Reader() {
        serverStart();
    }

    private void serverStart() {

        getProperties();
        try {
            socket = new Socket(host, port);
            service();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }

        System.out.println("Reader listening on port " + port + "...");
    }

    /*
    Method to extract values stored input the config.properties file
     */
    private void getProperties() {

        Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);

            // Get the properties values and store them input the Strings
            host = properties.getProperty("host");
            port = Integer.parseInt(properties.getProperty("port_pop3"));
            password = properties.getProperty("password_receiver");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Close the InputStream after the operation is completed
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void service() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            System.out.println(input.readLine() + "\n");

            send(input, output, "USER " + BuildConfig.RECIPIENT);

            send(input, output, "PASS " + password);

            send(input, output, "STAT");

            send(input, output, "RETR 1");

            String reply;

            do {
                if ((reply = input.readLine()) != null
                        && reply.length() > 0)
                    if (reply.charAt(0) == '.')
                        break;

                System.out.println(reply);

            } while (true);

            send(input, output, "DELE 1");

            send(input, output, "RSET");

            send(input, output, "QUIT");


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Reader();
    }
}