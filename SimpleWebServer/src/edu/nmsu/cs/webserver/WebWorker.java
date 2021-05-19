package edu.nmsu.cs.webserver;


/** pulled from https://github.com/NMSU-SDev/Programs, modified by jsy4
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileReader;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;


public class WebWorker implements Runnable
{

	private Socket socket;

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			String fileName = readHTTPRequest(is);
			boolean goodPage = writeHTTPHeader(os, fileName);
			if(goodPage) 
			{
				writeContent(os, fileName);
			}//end if
			else 
			{ //if 404 not found, display HTML error page
				os.write("<html><head></head><body>\n".getBytes());
				os.write("<h3>Sorry...Error! Page couldn't be found.<br/></h3>".getBytes());
				os.write("</body></html>\n".getBytes());
			}//end else
 
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

	/**
	 * Read the HTTP request header.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String fileName = "";
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if(line.startsWith("GET")) 
				{ // line has /filename or /ico
					String[] spltLine = line.split(" ");
					fileName = spltLine[1];
					fileName = fileName.substring(1);
				}//if
				if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return fileName; //returns file name
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType was changed to fileName
	 *          fileName has string MIME content type (e.g. "text/html")
	 * 
	 **/
	private boolean writeHTTPHeader(OutputStream os, String fileName) throws Exception
	{
		String contentType = "text/html"; //to display error message, type default to HTML
		// as assignment == "properly change the response status AND provide the HTML message"
		boolean goodPage = true; //to display error page
		try 
		{ 
			@SuppressWarnings("unused") FileReader f;
			if (!fileName.equals(""))
				f = new FileReader(fileName); //checks if it's a readable file by f
			os.write("HTTP/1.1 200 OK\n".getBytes());
			//https://stackoverflow.com/questions/51438/getting-a-files-mime-type-in-java
			//contentType= URLConnection.guessContentTypeFromName(fileName);
			if(fileName.endsWith(".ico"))
				contentType = "image/x-icon";
			else if(fileName.endsWith(".gif"))
				contentType = "image/gif";
			else if(fileName.endsWith(".jpg"))
				contentType = "image/jpeg";
			else if(fileName.endsWith(".png"))
				contentType = "image/png";
		}//try
		catch(Exception e)
		{
			os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());
			goodPage = false;
		}//catch


		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: J's first server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return goodPage;
	}//writeHTTPheader

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out. 
	 * Changes all string that matches the <cs371date> <cs371server> in HTML files
	 * 
	 * @param os
	 *          is the OutputStream object to write to, fileName is the file to be written from
	 **/
	private void writeContent(OutputStream os, String fileName) throws Exception
	{
		try
		{   
			if(fileName.equals("")) //readable file or no input
			{ //no fileName, default HTML page on 8080
				//https://superuser.com/questions/532616/grab-favicon-ico-using-google-
				//chrome-dev-tools/532751#:~:text=In%20the%20address%20bar%2C%20enter,%2F%20%2C%20
				//followed%20by%20the%20url.&text=Why%20don't%20you%20just,Just%20click%20on%20the%20link.
				os.write("<html><head><link rel=\"icon\" href=\"/www/favicon.ico\" /></head><body>\n".getBytes());
				os.write("<h3>My web server works! This is plain flavor of port 8080 *^^* </h3>\n".getBytes());
				os.write("</body></html>\n".getBytes());
			}//if
			else if(fileName.endsWith(".html")) //html type
			{ // substitute tags in HTML
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String line;
				String today = new java.util.Date().toString();
				for(line = br.readLine(); line !=null; line = br.readLine()) 
				{
					line = line.replaceAll("<cs371date>", today);
					line = line.replaceAll("<cs371server>", "J's Server");
					os.write(line.getBytes());
				}//end for
				br.close();
			}//else if
			else //other file types
			{	//using ImageIO gif doesn't work
				//image files read by byte: http://tutorials.jenkov.com/java-io/inputstream.html
				InputStream is = new FileInputStream(fileName);
				int data = is.read();
				while(data != -1) 
				{
					os.write(data);
					data = is.read();
				}//while
				is.close();
			}//else

		}catch(Exception e) {
			os.write("Sorry...Error! File couldn't be read correctly. CODE: 404 Not Found.".getBytes());
		}//catch
		

	}//writeContent

} // end class

