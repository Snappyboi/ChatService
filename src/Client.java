import java.net.*;
import java.util.*;
import java.io.*;
public class Client extends Thread {
    private static InetAddress host;
    private static final int PORT = 8080; 
    private static Socket link;
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int listenPort;
        int targetPort;
        String ipAddress;
        System.out.println("Enter port to listen: ");
        
        while(!sc.hasNextInt()){
            System.out.println("Invalid input. Enter a valid port number.");
            System.out.println("Enter port: ");
            sc.next();
        }
        listenPort = sc.nextInt();
        sc.nextLine(); //consume empty line

        System.out.println("Enter target IP Address: ");
        ipAddress = sc.nextLine();
        System.out.println("Enter target port: ");
        while(!sc.hasNextInt()){
            System.out.println("Invalid input. Enter a valid port number.");
            System.out.println("Enter port: ");
            sc.next();
        }
        targetPort = sc.nextInt();

        new Thread(() -> startServer(listenPort)).start();
        startClient(ipAddress, targetPort);
    }
    public static void startServer(int listenPort){
        try(ServerSocket serverSocket = new ServerSocket(listenPort)){
            System.out.println("Opening port...");

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to: " + clientSocket.getInetAddress());

                new Thread(() -> handleChat(clientSocket)).start();
            }
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public static void startClient(String targetAddress, int targetPort){
        try(
            Socket targetSocket = new Socket(targetAddress, targetPort);
            BufferedReader input = new BufferedReader(new InputStreamReader(targetSocket.getInputStream()));
            PrintWriter output = new PrintWriter(targetSocket.getOutputStream());
            Scanner keyboard = new Scanner(System.in);
        ){
            System.out.println("Connected to " + targetSocket.getInetAddress() + ". Type ***EXIT*** to exit.");

            new Thread(() -> {
                    String response = "";
                    while(!response.equals(null)){
                        System.out.println(targetSocket.getInetAddress() + ": " + response);
                        if(response.equals("***EXIT***")){
                            disconnect();
                            return;
                        }
                    }
                }).start();

                String messageOut;
                while((messageOut = keyboard.nextLine()) != null){
                    if(messageOut.equals("***EXIT***")){
                        disconnect();
                        return;
                    }
                    output.println(messageOut);
                }
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public static void handleChat(Socket clientSocket){
        try(BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
            String message;
            while((message = input.readLine()) != null){
                System.out.println(clientSocket.getInetAddress() + ": " + message);
            }
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
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
