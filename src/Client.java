import java.net.*;
import java.util.*;
import java.io.*;
public class Client {
    private static InetAddress host;
    private static final int PORT = 8080; 
    private static Socket link;
    public static void main(String[] args){
        try{
            host = InetAddress.getLocalHost();
        }
        catch(UnknownHostException uhEx){
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        accessServer();
    }
    public static void accessServer(){
        link = null;
        try{
            link = new Socket(host, PORT);

            Scanner serverMessage = new Scanner(link.getInputStream());
            PrintWriter output = new PrintWriter(link.getOutputStream(), true);
            Scanner userInput = new Scanner(System.in);
            String inMessage = "";
            String outMessage = "";
            System.out.println("Welcome to the chatroom.");

            while(!inMessage.equals("***END***")){
                if(serverMessage.hasNextLine()){
                    inMessage = serverMessage.nextLine();
                    System.out.println("SERVER> " + inMessage);
                }
                //if(userInput.hasNextLine()){
                    outMessage = userInput.nextLine();
                    output.println(outMessage);
                //}
            }
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
        finally{
            disconnect();
        }
    }
    public static void disconnect(){

    }
}
