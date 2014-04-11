package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
 public static void main(String[] args)
 {
	 try {
		ServerSocket serverSocket = new ServerSocket(20000);
		Socket socket = serverSocket.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String msg = reader.readLine();
		System.out.println("received" + msg);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		if(msg.equals("QUERY_REQUEST"))
		{
			
 	   
			writer.write("QUERY_RESPONSE");
			writer.flush();
		
		}
		writer.close();
		reader.close();
		socket.close();
		serverSocket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
 }
}
