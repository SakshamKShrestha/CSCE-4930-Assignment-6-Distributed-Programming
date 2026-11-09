There are 5 source files and 1 index file
==========================
src/cloudsearch/_0CommunicationInterface.java is the abstract class of communication interface

src/cloudsearch/_1SocketCommunication.java is an implementation of communication class (with Java Socket)

src/cloudsearch/_2commonFn.java is a common utility, setting used by both client and server

src/cloudsearch/_3Server.java is a specific code for server

src/cloudsearch/_4Client.java is a specific code for client

index/Index.txt is the index file that server code will try to read

To compile without IDE
========================
go to src folder (not in cloudsearch folder)

javac ./cloudsearch/_3Server.java 

javac ./cloudsearch/_4Client.java 

To run without IDE
====
stay in src folder (not in cloudsearch folder)

%% run server 

java cloudsearch._3Server

%% run client 

java cloudsearch._4Client

Compilation NOTE
====
1) Delete all .class files in src/cloudsearch before recompile with modified source code. Otherwise, Java may use some of the original .class rather than creating a new modified one.

2) Currently, _2commonFn.java specify the socket address as localhost:1350. This only work if you run client and server on the same machine. You need to change the IP address when running it on two computers.

Assignment Hint
====
To create a new communication scheme, create a new _1XXXCommunication.java and mimic the functionality of _1SocketCommunication.java (which implement abstract functions of _0CommunicationInterface.java). In the new file, develop the code to switch from using socket to another communication scheme. Register your new _1XXXCommunication.java in _2commonFn.java's newCommunicator function. Try to limit the modification to other files. Some functions required in _0CommunicationInterface can be left empty if it make sense to do so.


