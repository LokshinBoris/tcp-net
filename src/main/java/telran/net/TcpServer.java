package telran.net;
import java.net.*;
import java.util.*;


public class TcpServer extends Thread
{
	protected static final int TIMEOUT = 1000;
	Protocol protocol;
	int port;
	boolean running=true;
	

	public TcpServer(Protocol protocol, int port)
	{
		super();
		this.protocol = protocol;
		this.port = port;
	}
	
	public void run()
	{
		try(ServerSocket serverSocket=new ServerSocket(port))
		// TODO using ServerSocket has the method setSoTimeout
		{
			serverSocket.setSoTimeout(TIMEOUT);
			System.out.println("Server is listening on port "+port);
			while(running)
			{
				try
				{
				Socket socket=serverSocket.accept();
				TcpClientServerSession session=new TcpClientServerSession(socket,protocol,this);
				session.start();		
				}
				catch (SocketTimeoutException e)
				{
					
				}
				
			}
			// TODO handling timeout exceptions
			serverSocket.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		System.out.println("Server stoping");
	}
	
	public void shutdown()
	{	
		running=false;
	}
	
	

}
