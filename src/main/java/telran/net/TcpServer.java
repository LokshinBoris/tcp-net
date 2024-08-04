package telran.net;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class TcpServer extends Thread
{
	protected static final int TIMEOUT = 1000;
	Protocol protocol;
	int port;
	boolean running=true;
	ExecutorService executor;

	public TcpServer(Protocol protocol, int port)
	{
		super();
		this.protocol = protocol;
		this.port = port;
		executor = Executors.newFixedThreadPool(getNumberOfThreads());
	}
	
	private int getNumberOfThreads() 
	{
		Runtime runtime = Runtime.getRuntime();
		return runtime.availableProcessors();
	}
	
	public ExecutorService getExecutor()
	{
		return executor;
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
				executor.execute(session);
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
		executor.shutdown();
		try 
		{
			executor.awaitTermination(1, TimeUnit.HOURS);
		}
		catch (InterruptedException e) 
		{
			//no interrupts
		}
		running=false;
	}
	
	

}
