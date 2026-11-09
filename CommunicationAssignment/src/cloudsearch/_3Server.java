package cloudsearch;
import java.io.*;

// this is client's code, try to minimize any change
public class _3Server{
    //specify the library index file.
    public static String indexFileLocation = ".." + File.separator + "index";
    public static String indexFileName = "Index.txt";
    //a flag to exit the program
    public static boolean exit=false;

    // main of Server
    public static void main(String[] args) {

        System.out.println("Server Side Running: Waiting for Search Term.....");
        //create communicator, following the abstract class, but creating communicator object specified in _2 file.
        _0CommunicationInterface communicator=_2commonFn.newCommunicator();
        //using _2's specified default port number
        communicator.ServerSetup(_2commonFn.defaultport);
        //while user don't want to exit, keep running
        while(exit!=true){
            String Key="";
            ////////////////////////// Keyword input section
            // input from client application
            //accept connection
            communicator.ServerNewClient();
            //receive keyword
            Key=communicator.receive();
            //search the result
            String result=searchTermInIndex(Key);
            //send the result
            communicator.send(result);
            //terminate the connection
            communicator.closeConnection();
        }
    }

    //just a function to search the keyword in the index file, nothing special, don't need to modify
    public static String searchTermInIndex(String searchKeyword){
        File file = new File(indexFileLocation + File.separator + indexFileName);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {

                String [] spiltWords =  st.split("\\|.\\|");
                if(spiltWords[0].equals(searchKeyword)){
                    System.out.println("Match Find on Index File!");
                    System.out.println("Sending following result to the client side:");
                    System.out.println(st);
                    return st;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}