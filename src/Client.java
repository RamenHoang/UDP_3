import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.event.*;
import java.awt.*;
import java.net.*;

public class Client extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nickname;
    private JLabel nicknameL;
    private JTextArea enteredText;
    private JTextField typedText;
    private JScrollPane scrollPane;
    private DatagramSocket clientSocket;

    public Client() {
        this.initLayout();
        try {
        	clientSocket = new DatagramSocket();
        	new MessageTask().start();
        	byte[] data = "joined!".trim().getBytes();
        	DatagramPacket sendData = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), 8080);
        	clientSocket.send(sendData);
        } catch (Exception e) {
        	log(e.getMessage());
        	log(e.getStackTrace());
        }
    }

    private void initLayout() {
    	this.nickname = randomName();
        this.enteredText = new JTextArea(25, 32);
        this.typedText = new JTextField(32);
        this.scrollPane = new JScrollPane(this.enteredText);
        this.nicknameL = new JLabel(this.nickname);

        this.enteredText.setEditable(false);
        this.enteredText.setBackground(Color.WHITE);
        this.enteredText.setLineWrap(true);

        this.typedText.addActionListener(this);

        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.setTitle("Chat with Server");
        this.add(this.nicknameL);
        this.add(this.scrollPane);
        this.add(this.typedText);
        this.setSize(420, 500);
        this.setResizable(false);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setVisible(true);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JFrame login = new JFrame();
        login.setBounds(400, 400, 200, 150);
        login.setLayout(new FlowLayout(FlowLayout.CENTER));
        login.add(new JLabel("Your name"));
        login.setResizable(false);
        JTextField input = new JTextField(15);
        JButton submit = new JButton("Let's chat!");
        login.add(input);
        login.add(submit);
        login.setVisible(true);
        submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String name = input.getText();
                if (name.length() > 0) {
                    try {
                        nickname = name;
                        nicknameL.setText(nickname);
                        login.setVisible(false);
                        login.dispose();
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(login, exc.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(login, "Please complete all field!");
                    input.requestFocus();
                }
            }

        });
    }

    private void log(Object s) {
        System.out.println(s);
    }

    private String randomName() {
        String s = "Stranger-";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            s += random.nextInt(9);
        }
        return s;
    }
    
    public void alert(String message) {
    	JOptionPane.showMessageDialog(this, message);
    }
    
    public static void main(String[] args) throws Exception {
        new Client();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            InetAddress IPAddress = InetAddress.getByName("localhost"); 
            byte[] sendData = ("[" + nickname + "]: " + typedText.getText()).trim().getBytes();
            this.enteredText.insert("[You]: " + typedText.getText().trim() + "\n", this.enteredText.getText().length());
            this.typedText.setText("");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8080);
            clientSocket.send(sendPacket);
        } catch (Exception ex) {
            log(ex.getMessage());
            log(ex.getStackTrace());
        }
    }
    
    public class MessageTask extends Thread {
    	public void run() {
    		while(true) {
    			try {
//    				DatagramSocket socket = new DatagramSocket();
    				byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String str = new String(receivePacket.getData());
                    enteredText.insert(str + "\n", enteredText.getText().length());
    			} catch(Exception e) {
    				alert(e.getMessage());
    			}
    		}
    	}
    }
}