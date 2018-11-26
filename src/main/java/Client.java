import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import static javax.xml.crypto.dsig.Transform.BASE64;

public class Client {

    private SSLSocket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String hostName;
    private int portNumber;
    private String emailPassword;


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

        try {
            System.setProperty("mail.smtp.ssl.enable", "true");
            System.setProperty("mail.smtp.auth", "true");
            System.setProperty("mail.smtp.starttls.enable","true");

            socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(hostName, portNumber);
            //System.setProperty("mail.smtp.auth.mechanisms", "CRAM-MD5");
            //System.setProperty("mail.smtp.sasl.enable", "true");

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
            emailPassword = properties.getProperty("password");

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
            writer = new PrintWriter(socket.getOutputStream());

            send(reader, writer, "EHLO " + InetAddress.getLocalHost().getHostName());
            //send(reader, writer,"AUTH LOGIN" + BuildConfig.SENDER + ", " + emailPassword);
            //send(reader, writer,"AUTH CRAM-MD5");
            //send(reader, writer,"AUTH PLAIN " + BuildConfig.SENDER + emailPassword + "=*");


            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", hostName);
            props.put("mail.smtp.port", portNumber);

            Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(BuildConfig.SENDER, emailPassword);
                        }
                    });


            send(reader, writer, "MAIL FROM:<" + BuildConfig.SENDER + ">");

            send(reader, writer, "RCPT TO:<" + BuildConfig.RECIPIENT + ">");
            send(reader, writer, "DATA");

            send(reader, writer, "Message sent at: " + getMessageSentTime());
            send(reader, writer, "Subject: Networks II");
            send(reader, writer, "From: Daria Kalashnikova - " + BuildConfig.SENDER);
            send(reader, writer, "Hi, Check the Project I made for Networks class!");
            send(reader, writer, "\n.\n");
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

    private String getMessageSentTime(){
        String dateAndTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        dateAndTime = dateFormat.format(date);

        return dateAndTime;
    }

    private void send(BufferedReader in, PrintWriter out, String string) {
        try {
            out.write(string + "\n");
            out.flush();

            System.out.println(BuildConfig.CLIENT_ARROW + string + "\n");

            string = in.readLine();
            System.out.println(BuildConfig.SERVER_ARROW + string + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



/*    private void sendEmail(){

        String from = BuildConfig.SENDER;
        String pass = BuildConfig.EMAIL_PASSWORD;
        String to = BuildConfig.RECIPIENT;

        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", BuildConfig.HOST);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", BuildConfig.PORT);
        properties.put("mail.smtp.auth", true);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
            message.setText("This is actual message");

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(BuildConfig.HOST, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }*/

    public static void main(String[] args) {

        new Client();
    }
}