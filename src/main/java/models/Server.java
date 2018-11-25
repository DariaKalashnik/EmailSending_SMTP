package models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private  int port;
    private ServerSocket ss;
    private Socket s;
    private  boolean online = true;
    BufferedReader in;
    PrintWriter out;
    String inData;
    String outData;

    public Server(){
        serverStart();
    }

    private void serverStart(){

        port = 110;

        try{
            ss = new ServerSocket(port);
        }
        catch(Exception e){
            System.out.println("Error:" + e.getMessage());
        }

        System.out.println("models.Server listening on port "+port+"...");

        while(online){
            try{
                s = ss.accept();
                System.out.println("models.Client connected...");
                service();
            }
            catch(Exception e){
                System.out.println("No connection..., Error:" + e.getMessage());
            }
            finally{
                try{
                    s.close();
                    System.out.println("models.Client connection closed.");
                }
                catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        try{
            ss.close();
            System.out.println("models.Server stopped.");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void service(){
        try{
            in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());

            /*Application layer protocol specific message exchange (SERVER SIDE)*/

            in.close();
            out.close();

        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}