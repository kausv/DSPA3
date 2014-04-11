package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

	public static void main(String[] args)
	{

  		try 
  		{
  			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}), 20000);
  	  		String queryRequest = "QUERY_REQUEST\n";		
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(queryRequest);
			writer.flush();
			
			
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = reader.readLine();
			System.out.println("received" + line);
			writer.close();
			reader.close();
			socket.close();
  		}
  		catch(Exception ex)
  		{ex.printStackTrace();}
}
}