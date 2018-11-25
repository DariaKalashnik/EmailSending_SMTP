package models;

import utils.BuildConfig;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static utils.Utils.getMessageSentTime;
import static utils.Utils.send;


public class Client {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String hostName, email, password, domain;
    private int portNumber;

    private Client() {
        clientStart();
    }

    private void clientStart() {

        getProperties();

        System.out.println("models.Server IP: " + hostName);
        System.out.println("models.Server port: " + portNumber);
        System.out.println("---------------------------------");

        //Setting up configurations for the email connection to the Google SMTP server using SSL
        //properties.put("mail.smtp.host", "smtp.gmail.com");
        //properties.put("mail.smtp.port", portNumber);
         /*System.setProperty("mail.smtp.ssl.enable", "true");
           System.setProperty("mail.smtp.auth", "true");
           System.setProperty("mail.smtp.auth.plain.enable", "true");
           System.setProperty("mail.smtp.socketFactory.fallback", "true");*/

        try {
            //socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(hostName, portNumber);
            socket = new Socket(hostName, portNumber);

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
      Method to extract values stored in the config.properties file
     */
    private void getProperties() {

        Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);

            // Get the properties values and store them in the Strings
            hostName = properties.getProperty("host");
            portNumber = Integer.parseInt(properties.getProperty("port_smtp"));
            email = properties.getProperty("user");
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
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(reader.readLine() + "\n");

            send(reader, writer, "HELO  " + hostName);

            send(reader, writer, "EHLO  " + domain);

            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());

            send(reader, writer, "AUTH LOGIN");

            send(reader, writer, email);

            send(reader, writer, password);

            send(reader, writer, "MAIL FROM:" + BuildConfig.SENDER);

            send(reader, writer, "RCPT TO:" + BuildConfig.RECIPIENT);

            send(reader, writer, "DATA");

            String message = "Subject: Networks II\n" +
                    "From: Daria Kalashnikova - " + BuildConfig.SENDER +
                    "Hi, Check the Project I made for Networks class!\n" +
                    "Message sent at: " + getMessageSentTime() + "\n";

            writer.write(message);
            System.out.println(message);
            //reader.readLine();

            send(reader, writer, ".");

            send(reader, writer, "QUIT");

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}