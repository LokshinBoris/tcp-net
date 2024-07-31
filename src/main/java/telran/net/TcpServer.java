package telran.net;
import java.net.*;
import java.util.*;


public class TcpServer extends Thread
{
	Protocol protocol;
	int port;
	boolean running=true;
	boolean notRun=false;
	List<TcpClientServerSession> sessions=new LinkedList<TcpClientServerSession>();

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
			serverSocket.setSoTimeout(3000);
			System.out.println("Server is listening on port "+port);
			while(running)
			{
				try
				{
				Socket socket=serverSocket.accept();
				TcpClientServerSession session=new TcpClientServerSession(socket,protocol);
				sessions.add(session);
				session.start();
			
				}
				catch (SocketTimeoutException e)
				{
					if(notRun) shutdown();
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
		
		for(TcpClientServerSession ses:sessions)
		{
			ses.makeExc();
		}
		running=false;
	}
	public void makeExc() 
	{
		notRun=true;
	}
}
