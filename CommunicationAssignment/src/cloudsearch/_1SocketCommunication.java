package cloudsearch;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//an implementation of CommunicationInterface which Communicate with Socket
//important to extend _0CommunicationInterface
public class _1SocketCommunication extends _0CommunicationInterface{
    //define some stuff required to work
    protected ServerSocket serv;
    protected Socket sock;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    boolean ready=false;

    //empty constructor
    public _1SocketCommunication(){
    }

    //a function to make server ready
    public void ServerSetup(int port){
        try {
            serv = new ServerSocket(port);
            System.out.println("Ready to com");
        } catch (IOException ex) {
            System.err.println("Error opening port");
        }
    }
    //a function for server to accept a new client's connection
    public void ServerNewClient(){
        try {
            sock = serv.accept();
            sock.setKeepAlive(true);
            sock.setSoTimeout(10000);
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());
            ready=true;
        } catch (IOException ex) {
            System.err.println("Error opening connection");
        }
    }
    //a function for client to connect to the server
    public void ClientSetup(String Addr,int port) {
        while (!ready) {
            try {
                sock = new Socket(Addr, port);
                sock.setKeepAlive(true);
                sock.setSoTimeout(10000);
                dis = new DataInputStream(sock.getInputStream());
                dos = new DataOutputStream(sock.getOutputStream());
                ready = true;
            } catch (Exception e) {
                System.err.println("Error seting up client socket");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex1) {
                    System.err.println("also error to sleep..?");
                }
            }
        }
    }
    //a function to terminate the connection, used by both server and client.
    public void closeConnection(){
        try {
            dis.close();
            dos.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //a function to send data
    //bulky function... the actual code are 2 lines in the try, the rest are gibbering
    public void send(String key) {
        if(ready) {
            boolean notdone = true;
            while (notdone) {
                try {
                    //write string to data output stream
                    dos.writeUTF(key);
                    notdone = false;
                } catch (Exception e) {
                    System.err.println("Error receiving data");
                }
            }
        }else{
            System.err.println("try to communicate when not ready");
        }
    }
    //a function to receive data
    //bulky function... the actual code is 1 line in the try, the rest are gibbering
    public String receive() {
        if(ready) {
            while (true) {
                try {
                    //read string from data input stream
                    return dis.readUTF();
                } catch (Exception e) {
                    System.err.println("Error receiving data");
                }
            }
        }else{
            System.err.println("try to communicate when not ready");
            return "";
        }
    }


}