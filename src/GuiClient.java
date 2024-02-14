import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GuiClient extends JFrame implements ActionListener{  //need to make all connetion stuff available to disconnect method and add disconnect method to actionEvent and Windowclosing
    private JTextField targetPortInput;
    private JTextField targetIPInput;
    private JTextField listenPortInput;
    private JTextField messageOutInput;
    private JTextArea display;
    private JButton sendButton;
    private JButton exitButton;
    private JButton connectButton;
    private JPanel buttonPanel;

    private ServerSocket serverSocket;
    private Socket targetSocket;
    private BufferedReader input;
    private PrintWriter output;

     public static void main(String[] args){
        GuiClient frame = new GuiClient();
        frame.setSize(400,300);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event){
                System.exit(0);
            }
        });
        
    }
    public GuiClient(){
        targetPortInput = new JTextField(20);
        add(targetPortInput, BorderLayout.NORTH);
        
        display = new JTextArea(10,15);

        display.setWrapStyleWord(true);
        display.setLineWrap(true);

        add(new JScrollPane(display), BorderLayout.CENTER);

        buttonPanel = new JPanel();

        sendButton = new JButton("Send message ");
        sendButton.addActionListener(this);
        buttonPanel.add(sendButton);

        exitButton = new JButton("Exit ");
        exitButton.addActionListener(this);
        buttonPanel.add(exitButton);

        connectButton = new JButton("Connect ");
        connectButton.addActionListener(this);
        buttonPanel.add(connectButton);

        add(buttonPanel, BorderLayout.SOUTH);


    }
    public void ActionPerformed(ActionEvent event){
        int listenPort;
        int targetPort;
        String targetIP;
        if(event.getSource() == exitButton){
            disconnect();
            System.exit(0);
        }
        if(event.getSource() == connectButton){
            if(listenPortInput.getText() == ""){
                display.append("Enter a valid listen port.");
            }
            else if(targetPortInput.getText() == ""){
                display.append("Enter a valid target port.");
            }
            else if(targetIPInput.getText() == ""){
                display.append("Enter a valid target IP address.");
            }
            else{
                listenPort = Integer.parseInt(listenPortInput.getText());
                targetPort = Integer.parseInt(targetPortInput.getText());
                targetIP = targetIPInput.getText();
                new Thread(() -> startServer(listenPort)).start();
                startClient(targetIP, targetPort);
            }
        }
        if(event.getSource() == sendButton){
            new Thread(() -> handleChat(targetSocket)).start();
        }

        
    }
    public void startServer(int listenPort){
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
    public void startClient(String targetAddress, int targetPort){ //this isnt static, we'll see how that goes
        try{
            targetSocket = new Socket(targetAddress, targetPort);
            input = new BufferedReader(new InputStreamReader(targetSocket.getInputStream()));
            output = new PrintWriter(targetSocket.getOutputStream());
            System.out.println("Connected to " + targetSocket.getInetAddress() + ". Type ***EXIT*** to exit.");

            new Thread(() -> { 
                try{
                    String response;
                    while((response = input.readLine()) != null){ 
                        System.out.println(targetSocket.getInetAddress() + ": " + response);
                    }
                }
                catch(IOException ioEx){
                    ioEx.printStackTrace();
                }
                }).start();
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public void handleChat(Socket clientSocket){
        try(BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
            String message;
            while((message = messageOutInput.getText()) != null){
                System.out.println(clientSocket.getInetAddress() + ": " + message);
            }
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public void disconnect(){
        try{
            input.close();
            output.close();
            targetSocket.close();
            System.out.println("Disconnected.");
            System.exit(0);
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
}
