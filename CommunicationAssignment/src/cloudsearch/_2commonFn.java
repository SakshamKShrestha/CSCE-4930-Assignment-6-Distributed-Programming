package cloudsearch;
import java.util.*;

public class _2commonFn{
    public static int defaultport=1350;
    public static String defaultaddress="localhost";
    static Scanner scanner=new Scanner(System.in);

    public static String getInputFromConsole(){

        System.out.print("insert keyword to search (\\0 to exit):");
        return scanner.next();
    }
    //change the communicator here
    public static _0CommunicationInterface newCommunicator(){
        //return the communication class, in this case we use socket
        //modify here to use other communication class
        return new _1SocketCommunication();
        // if you have another communicator, replace what you return here, exaple:
        //return new _yourCommunicator();
    }

}