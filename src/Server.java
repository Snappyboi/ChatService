import java.net.*;
import java.util.*;
import java.io.*;
public class Server{
    private static final int PORT = 8080;
    private static ServerSocket serverSocket;
    private static Socket link = null;
    public static void main(String[] args){
        System.out.println("Opening port.");
        try{
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException ioEx){
            System.out.println("Could not attach to port.");
            System.exit(1);
        }
        do{
            handleClient();
        }while(true);
    }
    public static void handleClient(){
        try{
            link = serverSocket.accept();
            Scanner userInput = new Scanner(System.in);
            Scanner clientStream = new Scanner(link.getInputStream());
            PrintWriter output = new PrintWriter(link.getOutputStream(), true);
            String inMessage = "";
            String outMessage = "";

            while(!inMessage.equals("***END***")){
                if(clientStream.hasNextLine()){
                    inMessage = clientStream.nextLine();
                    System.out.println("CLIENT> " + inMessage);
                }
                if(userInput.hasNextLine()){
                    outMessage = userInput.nextLine();
                    output.println(outMessage);
                }
            }
        }
        catch(IOException ioEx){
            System.out.println("Could not create socket.");
        }
        finally{
            disconnect();
        }
    }

    public static void disconnect(){
        try{
            if(link != null && !link.isClosed()){
                link.close();
            }
        }
        catch(IOException ioEx){
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }
    }
}