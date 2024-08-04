package telran.net;
import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread 
{
	
	private static final int WAIT_TIME = 60000;
	Socket socket;
	Protocol protocol;
	boolean running=true;
	TcpServer tcpServer=null;
	
	public TcpClientServerSession(Socket socket, Protocol protocol, TcpServer tcpServer) throws SocketException
	{
		this.socket = socket;
		// TODO using the method setSoTimeout and some solution for getting session to know about shutdown
		// you should stop the thread after shutdown command
		this.protocol = protocol;
		this.tcpServer=tcpServer;	
		this.socket.setSoTimeout(tcpServer.TIMEOUT);
	}
	
	public void run()
	{
		int nowWaitTime=0;
		
		try(BufferedReader receiver=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream sender=new PrintStream(socket.getOutputStream());
			)
		{
				String line="";
				boolean socketClosed=false;
			// FIXME
			// figure out solution for exiting from the thread after shutdown				
				while( tcpServer.running && !socketClosed && line!=null )
				{
					try
					{
						line=receiver.readLine();
						if(line==null) break;
						nowWaitTime=0;
						String responseStr= protocol.getResponseWithJSON(line);
						sender.println(responseStr);
					}
					catch(SocketTimeoutException e)
					{
						nowWaitTime+=tcpServer.TIMEOUT;
						if(!tcpServer.running || nowWaitTime >WAIT_TIME ) 
						{			
							socketClosed=true;
							socket.close();
						}
					}							
			}
			// TODO handling exception SocketTimeoutException for exiting from the thread on two conditions
			// 1. Shutdown has been performed
			// 2. Thread exists in IDLE state more than 1 minutes
			// exiting from the cycle should be by closing connection
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
	

	
}
