import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GuiClient extends JFrame implements ActionListener{
    private JTextField targetPortInput;
    private JTextField targetIPInput;
    private JTextField listenPortInput;
    private JTextField messageOutInput;
    private JTextArea display;
    private JButton sendButton;
    private JButton exitButton;
    private JButton connectButton;
    private JPanel buttonPanel;
    private JLabel listenPortLabel;
    private JLabel targetPortLabel;
    private JLabel targetIPLabel;
    private JLabel messageOutLabel;


    private ServerSocket serverSocket;
    private Socket targetSocket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean running = true;

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
        setLayout(new GridLayout(0, 2));

        targetPortLabel = new JLabel("Target Port ");
        targetPortInput = new JTextField(10);
        add(targetPortLabel);
        add(targetPortInput);
        
        listenPortLabel = new JLabel("Listen Port ");
        listenPortInput = new JTextField(10);
        add(listenPortLabel);
        add(listenPortInput);

        targetIPLabel = new JLabel("Target IP ");
        targetIPInput = new JTextField(10);
        add(targetIPLabel);
        add(targetIPInput);

        messageOutLabel = new JLabel("Message out: ");
        messageOutInput = new JTextField(10);
        add(messageOutLabel);
        add(messageOutInput);
        
        
        display = new JTextArea(25,25);
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
    public void actionPerformed(ActionEvent event){
        int listenPort;
        int targetPort;
        String targetIP;
        if(event.getSource() == exitButton){
            running = false;
            disconnect();
        }
        if(event.getSource() == connectButton){
            if(listenPortInput.getText().equals("")){
                display.append("Enter a valid listen port.");
            }
            else if(targetPortInput.getText().equals("")){
                display.append("Enter a valid target port.");
            }
            else if(targetIPInput.getText().equals("")){
                display.append("Enter a valid target IP address.");
            }
            else{
                listenPort = Integer.parseInt(listenPortInput.getText());
                targetPort = Integer.parseInt(targetPortInput.getText());
                targetIP = targetIPInput.getText();
                Thread serverThread = new Thread(() -> startServer(listenPort));
                serverThread.setDaemon(true);
                serverThread.start();
                startClient(targetIP, targetPort);
            }
        }
        if(event.getSource() == sendButton){
            if(!messageOutInput.getText().isEmpty()){
                sendMessage(messageOutInput.getText());
                messageOutInput.setText("");
            }
                
        }

        
    }
    public void startServer(int listenPort){
        try(ServerSocket serverSocket = new ServerSocket(listenPort)){
            System.out.println("Opening port...");

            while(running){
                Socket clientSocket = serverSocket.accept();

                Thread chatThread = new Thread(() -> handleChat(clientSocket));
                chatThread.setDaemon(true); //I know we haven't covered these but these threads aren't dying and are ruining my disconnect method
                chatThread.start();
            }
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public void startClient(String targetAddress, int targetPort){
        try{
            targetSocket = new Socket(targetAddress, targetPort);
            input = new BufferedReader(new InputStreamReader(targetSocket.getInputStream()));
            output = new PrintWriter(targetSocket.getOutputStream(), true);
            display.append("Connected to: " + targetSocket.getInetAddress() + "\n");

            new Thread(() -> { 
                try{
                    String response;
                    while(running && (response = input.readLine()) != null){ 
                        display.append(targetSocket.getInetAddress() + ": " + response + "\n");
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
            while(running && (message = input.readLine()) != null){
                display.append(clientSocket.getInetAddress() + ": " + message + "\n");
            }
        }
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    public void disconnect() {
        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (targetSocket != null)
                targetSocket.close();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                running = false;
                setVisible(false);
                System.exit(0);
            });
        }
    }
    
    public void sendMessage(String message){
        if(output != null){
            display.append("You: " + message + "\n");
            output.println(message);
            output.flush();
        }
    }
}
