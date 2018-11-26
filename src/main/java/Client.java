import utils.BuildConfig;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static utils.Utils.getMessageSentTime;
import static utils.Utils.send;


public class Client {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String host, email, password, domain;
    private int port;

    private Client() {
        clientStart();
    }

    private void clientStart() {

        getProperties();

        System.out.println("Reader IP: " + host);
        System.out.println("Reader port: " + port);
        System.out.println("---------------------------------");

        //Setting up configurations for the email connection to the Google SMTP server using SSL
        //properties.put("mail.smtp.host", "smtp.gmail.com");
        //properties.put("mail.smtp.port", port);
         /*System.setProperty("mail.smtp.ssl.enable", "true");
           System.setProperty("mail.smtp.auth", "true");
           System.setProperty("mail.smtp.auth.plain.enable", "true");
           System.setProperty("mail.smtp.socketFactory.fallback", "true");*/

        try {
            //socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
            socket = new Socket(host, port);

            System.out.println("Connected...\n");

            clientService();

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }
        }
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
            port = Integer.parseInt(properties.getProperty("port_smtp"));
            email = properties.getProperty("user_sender");
            password = properties.getProperty("password_sender");
            domain = properties.getProperty("domain");

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

    private void clientService() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(input.readLine() + "\n");

            send(input, output, "HELO  " + host);

            send(input, output, "EHLO  " + domain);

            System.out.println(input.readLine());
            System.out.println(input.readLine());
            System.out.println(input.readLine());

            send(input, output, "AUTH LOGIN");

            send(input, output, email);

            send(input, output, password);

            send(input, output, "MAIL FROM:" + BuildConfig.SENDER);

            send(input, output, "RCPT TO:" + BuildConfig.RECIPIENT);

            send(input, output, "DATA");

            // Construct the information and body of the builder with StringBuilder
            StringBuilder builder = new StringBuilder();
            builder.append("Subject: Networks II").append("\r\n.");
            builder.append("From: Daria Kalashnikova ").append(BuildConfig.SENDER).append("\r\n");
            builder.append("To: Test User ").append(BuildConfig.RECIPIENT).append("\r\n");
            builder.append("Date: ").append(getMessageSentTime()).append("\r\n\n");
            builder.append("Hi, Check the Project I made for Networks class!\n");

            String message = builder.toString();

            output.write(message + "\r\n");
            System.out.println(message);

            send(input, output, ".");

            send(input, output, "QUIT");

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
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
        new Client();
    }
}