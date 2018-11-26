package utils;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static void send(BufferedReader in, PrintWriter out, String string) {
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

    public static String getMessageSentTime() {
        String dateAndTime;
        //Thu, 21 May 2008 05:33:29 -0700
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZ");
        Date date = new Date();

        dateAndTime = dateFormat.format(date);

        return dateAndTime;
    }

}
