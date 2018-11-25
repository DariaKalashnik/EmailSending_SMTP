import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String hostName;
    private int portNumber;
    private String email;
    private String password;
    private String domain;

    private Client() {
        clientStart();
    }


    private void clientStart() {

        getProperties();

        System.out.println("Server IP: " + hostName);
        System.out.println("Server port: " + portNumber);
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
            portNumber = Integer.parseInt(properties.getProperty("port"));
            email = properties.getProperty("user");
            password = properties.getProperty("password");
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
                    "Message sent at: " + getMessageSentTime();


            writer.write(message);
            System.out.println(message);

            send(reader, writer, ".");

            send(reader, writer, "QUIT");

            socket.close();

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

    private String getMessageSentTime() {
        String dateAndTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        dateAndTime = dateFormat.format(date);

        return dateAndTime;
    }


    private void send(BufferedReader in, PrintWriter out, String string) {
        try {
            out.write(string + "\r\n");
            out.flush();

            System.out.println(BuildConfig.CLIENT_ARROW + string + "\r\n");

            string = in.readLine();
            System.out.println(BuildConfig.SERVER_ARROW + string + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Client();
    }
}