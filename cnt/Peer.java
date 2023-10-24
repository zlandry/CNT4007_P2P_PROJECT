package cnt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import cnt.Server;

import java.net.*;

public class Peer {

    // client vars
    Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE; 


    // peer vars
    int id;
    String hostname;
    int port;
    int hasFile;

    public Peer() {
        id = 0000;
        hostname = "lin114-00.cise.ufl.edu";
        port = 8000;
    }
    
    public Peer(int id, String hostname, int port) {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
    }

	public int get_id() {
		return id;
	}

	public String get_hostname() {
		return hostname;
	}

	public int get_port() {
		return port;
	}

    public void run()
	{
		try{
			Thread serverThread = new Thread(() -> {
				try {
					Server server = new Server(port);
					server.main(new String[] {});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			serverThread.start();
			
			//create a socket to connect to the server
			requestSocket = new Socket(hostname, port);
			System.out.println("Connected to " + hostname + " in port " + port);
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			
            
            while(true)
			{
				System.out.print("Please input a sentence: ");
				//read a sentence from the standard input
				message = bufferedReader.readLine();
				//Send the sentence to the server
				sendMessage(message);
				//Receive the upperCase sentence from the server
				MESSAGE = (String)in.readObject();
				//show the message to the user
				System.out.println("Receive message: " + MESSAGE);
			}


		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//main method
	public static void main(String args[])
	{
        String hostname = "localhost";
        int id = 0000;
        int port = 8000;

        if (args.length != 3) {
			System.out.println("Usage: java Peer [Peer ID] [Connection URI] [Port]");
            return;
        }
        
        if (!args[1].isEmpty()) {
            hostname = args[1];
        }
        if (!args[0].isEmpty()) {
            id = Integer.valueOf(args[0]);
        }
        if (!args[2].isEmpty()) {
            port = Integer.valueOf(args[2]);
        }
        

        // fix args here to actually pass into Peer correctly (i want to make them pass contextually, for now this will work)
        
		Peer peer = new Peer(id, hostname, port);
        peer.run();
	}

    
}
