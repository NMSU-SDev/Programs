/**
	 * cd C:\Users\gorel\Desktop\SchoolWork\Programs\SimpleWebServer\src
	 * 
	 * javac edu/nmsu/cs/webserver/*.java -d ../bin
	 * 
	 * cd C:\Users\gorel\Desktop\SchoolWork\Programs\SimpleWebServer
	 * 
	 * java -cp bin edu.nmsu.cs.webserver.WebServer
**/
package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 * 
 * @author Jon Cook, Ph.D.
 *
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WebWorker implements Runnable
{

	private Socket socket;
	private File page;
	private String server ="Luis's Kawaii Server" ;

	public WebWorker(Socket s)
	{
		socket = s;
		page = new File("");//setting an empty to change it later;
	}

	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			readHTTPRequest(is);
			writeHTTPHeader(os, "text/html");
			writeContent(os);
			os.flush();
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	private void readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");

				if (line.length() == 0)
					break;
				if(line.substring(0, 3).equals("GET")) //after the "GET" comes " (page)" so split it up, then set that to the path we need;
				{
					String[] parts = line.split(" ");
					String path = "." + parts[1];
					System.out.println(path);
					if(path.equals("./")) 
					{
						System.out.println("Success!");
						path = "./test.html"; // assigns the path to the .html file
					}
					page = new File(path);
				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
	}

	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
	{
		
	    Date d = new Date();
	    DateFormat df = DateFormat.getDateTimeInstance();
	    df.setTimeZone(TimeZone.getTimeZone("GMT"));	
	    if(page.exists() && page.isFile())// if the file exist and is a file, then return 200 ok. otherwise 404
	    {
	        os.write("HTTP/1.1 200 OK\n".getBytes());
	    }
	    else
	    {
	        os.write("HTTP/1.1 404 Not Found\n".getBytes());
	    }
	    os.write("Date: ".getBytes());
	    os.write((df.format(d)).getBytes());
	    os.write("\n".getBytes());
	    os.write("Server: Luis's Kawaii server :3\n".getBytes());
	    os.write("Connection: close\n".getBytes());
	    os.write("Content-Type: ".getBytes());
	    os.write(contentType.getBytes());
	    os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
	    return;
	}

	private void writeContent(OutputStream os) throws Exception
	{
		if(!page.exists() || !page.isFile())//if the file does not exist or is not a file then 404 page, otherwise (Exists) then go.
		{
			os.write("<html><head></head>".getBytes());
			os.write("<body><h1><center> Error 404</h1> <h1>Page Not Found</center></h1></body></html>".getBytes());
			return;
		}
		else 
		{
			BufferedReader bufReader = new BufferedReader(new FileReader(page)); // reading contents of html file
			String string;
			Date  d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			while ((string = bufReader.readLine()) != null) // reading line by line
			{
				string = string.replaceAll("<cs371date>", df.format(d)); 
				string = string.replaceAll("<cs371server>", server);
				os.write(string.getBytes());
			}
			bufReader.close();
		}
		
	}

} // end class