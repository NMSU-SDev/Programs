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
import java.io.FileInputStream;

public class WebWorker implements Runnable
{

	private Socket socket;
	private File file;


	public WebWorker(Socket s)
	{
		socket = s;
		file = new File("");
	}

	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			readHTTPRequest(is);
			writeHTTPHeader(os);
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
	
//	private Image readImageFile(String fileName) {
//		InputStream is = new BinaryStream();
//	}

	String contentType = "text/html"; // referring to the content type, not the file
	
	private void readHTTPRequest(InputStream is) // changes the content type when we're expected to deliver an image
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
				if(line.substring(0, 3).equals("GET"))
				{
					String[] parts = line.split(" ");
					String path = "." + parts[1];
					System.out.println(path);
					
					path = path.toLowerCase();
							
					if(path.endsWith("gif"))
					{
						contentType = "image/gif";
					}
					
					if(path.endsWith("jpeg"))
					{
						contentType = "image/jpeg";
					}
					
					if(path.endsWith("png"))
					{
						contentType = "image/png";
					}
					
					if(path.equals("./")) 
					{
						System.out.println("Success!");
						path = "./test.html"; // assigns the path to the html file
					}
					
					file = new File(path);
				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
	}

	private void writeHTTPHeader(OutputStream os) throws Exception
	{
		
	    Date d = new Date();
	    DateFormat df = DateFormat.getDateTimeInstance();
	    df.setTimeZone(TimeZone.getTimeZone("GMT"));	
	    if(file.exists() && file.isFile())
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
	    os.write("Server: abc's server\n".getBytes());
	    os.write("Connection: close\n".getBytes());
	    os.write("Content-Type: ".getBytes());
	    os.write(contentType.getBytes());
	    os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
	    return;
	}

	private void writeContent(OutputStream os) throws Exception
	{
		
		if(!file.exists() || !file.isFile())
		{
			os.write("<html><head></head>".getBytes());
			os.write("<body><h1><center>404 Error Page Not Found</center></h1></body></html>".getBytes());
			return;
		}
		else if (contentType == "text/html") // the file exists
		{
			BufferedReader b = new BufferedReader(new FileReader(file)); // reading contents of html file
			String s;
			Date  d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			while ((s = b.readLine()) != null) // reading line by line
			{
				s = s.replaceAll("<cs371date>", df.format(d)); 
				s = s.replaceAll("<cs371server>", "abc's server");
				os.write(s.getBytes());
			}
			b.close();
		}
		
		else
		{
			FileInputStream newStream = new FileInputStream(file);
			int bytesRead;
			int off = 0; // start writing into the buffer at position 0 (always)
			int len = 4000; // we want this to be as big as the buffer
			byte[] byteArray = new byte[4000];
			
			while ((bytesRead = newStream.read(byteArray, off, len)) != -1) // reading line by line
			{
				os.write(byteArray, off, bytesRead); // we already have an array of bites, so we don't need getBytes()
			}
			newStream.close();
		}
		
	}

} // end class