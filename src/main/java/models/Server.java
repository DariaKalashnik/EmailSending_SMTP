package models;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static utils.Utils.send;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String host, user, password;
    private int port;

    public Server() {
        serverStart();
    }

    private void serverStart() {

        getProperties();
        try {
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), port);
            service();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }

        System.out.println("models.Server listening on port " + port + "...");

        /*try {
            serverSocket = new  ServerSocket(port);
            serverSocket.accept();
            System.out.println("models.Client connected...");
            service();
        } catch (Exception e) {
            System.out.println("No connection..., Error:" + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("models.Client connection closed.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
        try {
            serverSocket.close();
            System.out.println("models.Server stopped.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }*/
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
            user = properties.getProperty("user_receiver");
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

    public void service() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            System.out.println(input.readLine() + "\n");

            send(input, output, "USER test@network2.hu");

            send(input, output, "PASS 123");

            send(input, output, "STAT");

            send(input, output, "RETR 1");
            //System.out.println(input.read);

            String line;
            TimeLimiter timeLimiter = new SimpleTimeLimiter();

            try {
                while ((line = timeLimiter.callWithTimeout(input::readLine, 2, TimeUnit.SECONDS, false)) != null) {
                    if (line.isEmpty()) {
                        break;
                    }
                    System.out.println(line+"\r\n");
                }
            } catch (UncheckedTimeoutException e) {
                //System.out.println("End of email.");
            }

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
        new Server();
    }
}