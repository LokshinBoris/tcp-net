package telran.net;
import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread 
{
	Socket socket;
	Protocol protocol;
	boolean running=true;
	boolean notRun=false;
	
	public TcpClientServerSession(Socket socket, Protocol protocol) throws SocketException
	{
		this.socket = socket;
		// TODO using the method setSoTimeout and some solution for getting session to know about shutdown
		// you should stop the thread after shutdown command
		//socket.setSoTimeout(MAX_PRIORITY);
		socket.setSoTimeout(1000);
		this.protocol = protocol;
	}
	
	public void run()
	{
		try(BufferedReader receiver=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream sender=new PrintStream(socket.getOutputStream());
			)
		{
			String line=null;
			// FIXME
			// figure out solution for exiting from the thread after shutdown
			try
			{
				while( running && (line=receiver.readLine())!=null )
				{
					String responseStr= protocol.getResponseWithJSON(line);
					sender.println(responseStr);
				}
			}
			catch(SocketTimeoutException e)
			{
				if(notRun) shutdown();
			}
			// TODO handling exception SocketTimeoutException for exiting from the thread on two conditions
			// 1. Shutdown has been performed
			// 2. Thread exists in IDLE state more than 1 minutes
			// exiting from the cycle should be by closing connection
			socket.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	}
	
	public void shutdown()
	{
		running=false;
	}
	
	public void makeExc() 
	{
		notRun=true;
	}
	
}
