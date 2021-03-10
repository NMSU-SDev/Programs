//Modified by : Dylan Lassard
//Date : 2-19-21
//Course : CS 371 Software Development : Program1

package edu.nmsu.cs.webserver;

/**
 * A simple web server: it creates a new WebWorker for each new client connection, so all the
 * WebServer object does is listen on the port for incoming client connection requests.
 *
 * This class contains the application "main()" (see below). At startup, main() creates an object of
 * this class (WebServer) and invokes its start() method. Since servers run continually, the start()
 * method never returns. It uses socket programming to listen for client network connection
 * requests. When one happens, it creates a new object of the WebWorker class and hands that client
 * connection off to the WebWorker object. The WebServer object then just keeps listening for new
 * client connections. See the WebWorker source for more information about it.
 * 
 * @author Jon Cook, Ph.D.
 * modified by Dylan Lassard
 **/
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.net.UnknownHostException;

public class WebServer
{

private ServerSocket socket;
private boolean	 running;

/**
 * Constructor
 **/
private WebServer()
{
	running = false;
}

/**
 * Web server starting point. This method does not return until the server is finished, so perhaps
 * it should be named "runServer" or something like that.
 * 
 * @param port
 *          is the TCP port number to accept connections on
 **/
private boolean start(int port)
{
	Socket workerSocket;
	WebWorker worker;
	try
	{
		socket = new ServerSocket(port);
	}
	catch (Exception e)
	{
		System.err.println("Error binding to port " + port + ": " + e);
		return false;
	}
	while (true)
	{
		try
		{
			// wait and listen for new client connection
			workerSocket = socket.accept();
		}
		catch (Exception e)
		{
			System.err.println("No longer accepting: " + e);
			break;
		}
		// have new client connection, so fire off a worker on it
		worker = new WebWorker(workerSocket);
		new Thread(worker).start();
	}
	return true;
} // end start

/**
 * Does not do anything, since start() never returns.
 **/
private boolean stop()
{
	return true;
}

/**
 * Application main: process command line and start web server; default port number is 8080 if not
 * given on command line.
 **/
public static void main(String args[])
{
  File file = new File("C:\\Users\\dylan\\Programs\\SimpleWebServer\\www\\Nature.PNG");
    
    BufferedImage image = null;
     
    try
    {
        image = ImageIO.read(file);
         
        ImageIO.write(image, "JPG", new File("C:\\Users\\dylan\\Programs\\SimpleWebServer\\www\\Sunset.JPG"));
         
        ImageIO.write(image, "PNG", new File("C:\\Users\\dylan\\Programs\\SimpleWebServer\\www\\Nature.PNG"));
         
        ImageIO.write(image, "GIF", new File("C:\\Users\\dylan\\Programs\\SimpleWebServer\\www\\kermit.GIF"));
    } 
    catch (IOException e) 
    {
        e.printStackTrace();
    } //end catch
	
	int port = 8080;
	if (args.length > 1)
	{
		System.err.println("Usage: java Webserver <portNumber>");
		return;
	}
	else if (args.length == 1)
	{
		try
		{
			port = Integer.parseInt(args[0]);
		}
		catch (Exception e)
		{
			System.err.println("Argument must be an int (" + e + ")");
			return;
		}
	}
	WebServer server = new WebServer();
	if (!server.start(port))
	{
		System.err.println("Execution failed!");
	}
	
	System.out.print("Hello World, the current date and time is" + " ");
	String pattern = "MM-dd-yyyy HH:mm:ss.SSSZ";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	System.out.println(date);
	
	try {
	System.out.println("Dylan L Server:");
	System.out.println("Server machine details : " + InetAddress.getLocalHost().getCanonicalHostName());
	System.out.println("Port number : " + port);
	System.out.println();
	} //end try
	   catch (UnknownHostException e1) {
	 	   e1.printStackTrace();
	   } //end catch
	} // end main
} // end class
