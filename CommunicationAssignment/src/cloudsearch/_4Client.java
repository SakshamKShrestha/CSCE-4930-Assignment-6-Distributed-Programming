package cloudsearch;
import java.util.*;

// this is client's code, try to minimize any change
public class _4Client{
    // main of Client
    public static void main(String[] args) {

        String KEY="";
        while(!KEY.equals("\\0")) { //loop while key is not \0
            //get input from Console
            KEY = _2commonFn.getInputFromConsole();

            //only send signal to the server if our Key is not \0 (exit code),
            // if we send \0 to the server, it may shut down too.
            if(!KEY.equals("\\0")) {
                String result=query(KEY);
                System.out.println(result);
            }
        }
    }

    public static String query(String KEY)  {
        //normalize the word
        String key= KEY.toLowerCase();
        //seting up the client communication
        //create communicator, following the abstract class, but creating communicator object specified in _2 file.
        _0CommunicationInterface communicator=_2commonFn.newCommunicator();
        //set up client, with server's default IP and port specifed in _2
        communicator.ClientSetup(_2commonFn.defaultaddress,_2commonFn.defaultport);

        //send the keyword
        communicator.send(key);
        //accept the result
        String result=communicator.receive();
        //close communication
        communicator.closeConnection();
        return result;
    }
}