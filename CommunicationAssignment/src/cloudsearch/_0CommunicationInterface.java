package cloudsearch;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// this is abstract class of communicationInterface, try not to change it.
public abstract class _0CommunicationInterface {

    //a function to make server ready
    public abstract void ServerSetup(int port);
    //a function for server to accept a new client's connection
    public abstract void ServerNewClient();
    //a function for client to connect to the server
    public abstract void ClientSetup(String Addr, int port) ;
    //a function to terminate the connection, used by both server and client.
    public abstract void closeConnection() ;
    //a function to send data
    public abstract void send(String key);
    //a function to receive data
    public abstract String receive() ;
}