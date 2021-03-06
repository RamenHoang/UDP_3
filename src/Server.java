import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.net.*;

public class Server {
    private DatagramSocket serverSocket;
    private HashMap<InetAddress, java.util.ArrayList<Integer>> clients;
    
    public Server() {
        try {
            this.serverSocket = new DatagramSocket(8080);
            	this.clients = new HashMap<InetAddress, java.util.ArrayList<Integer>>();
            System.out.println("Server is running ...");
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                new MyTask(receivePacket);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }
    
    public static void main(String[] args) throws IOException {
        new Server();
    }

    public class MyTask extends Thread {
        private DatagramPacket receivePacket;
        
        public MyTask(DatagramPacket receivePacket) {
            this.receivePacket = receivePacket;
            this.start();
        }

        @Override
        public void run() {
            this.doMission();
        }

        private void doMission() {
            try {
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                System.out.println("Connect - " + port);
                	addClient(IPAddress, port);
                String instr = new String(receivePacket.getData(), "UTF-8");
                if (!instr.equals("joined!                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ")) {
                		String res = instr.trim();
                    byte[] _sendData = res.getBytes();
                    	broadCast(_sendData, clients, port);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }
        
        public void broadCast(byte[] data, HashMap<InetAddress, ArrayList<Integer>> clients, int senderPort) throws IOException {
        	for (InetAddress host : clients.keySet()) {
        		for (Integer port : clients.get(host)) {
        			if (port != senderPort) {
        				DatagramPacket sendData = new DatagramPacket(data, data.length, host, port);
	            			DatagramSocket tempSocket = new DatagramSocket();
	            			tempSocket.send(sendData);
	            			tempSocket.close();
        			}
        		}
        	}
        }
        
        private void print(HashMap<InetAddress, ArrayList<Integer>> map) {
        	for (InetAddress ip : map.keySet()) {
        		System.out.println(ip.getHostName() + " - " + map.get(ip).toString());
        	}
        }
        
        private void addClient(InetAddress host, int port) {
        	if (clients.containsKey(host)) {
        		for (int i = 0; i < clients.get(host).size(); i++) {
        			if (clients.get(host).get(i) == port) 
        				return;
        		}
        		clients.get(host).add(port);
        	} else {
        		ArrayList<Integer> ports = new ArrayList<Integer>();
        		ports.add(port);
        		clients.put(host, ports);
        	}
        }
        
        private String removeEndline(String s) {
        	return s.replaceAll("\0", "");
        }
    }
}
