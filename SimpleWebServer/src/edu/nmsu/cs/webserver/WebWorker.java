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
 **/

import java.io.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

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

			String dirL = locRetrieval(is);
			String dirLPlus = "";

			File doc = new File(System.getProperty("user.dir") + dirL);

			if(doc.exists())
			{
		       dirLPlus = System.getProperty("user.dir") + dirL;

			   if(dirLPlus.substring(dirLPlus.length() - 4).compareTo(".png") == 0)
			   {
			      writeHTTPHeader(os, "image/png", dirLPlus);
			      writeContent(os, dirLPlus);
			   }
			   else if(dirLPlus.substring(dirLPlus.length() - 4).compareTo(".jpg") == 0)
			   {
			      writeHTTPHeader(os, "image/jpg", dirLPlus);
			      writeContent(os, dirLPlus);
			   }
			   else if(dirLPlus.substring(dirLPlus.length() - 4).compareTo(".gif") == 0)
			   {
			      writeHTTPHeader(os, "image/gif", dirLPlus);
		          writeContent(os, dirLPlus);
			   }
			   else if(dirLPlus.substring(dirLPlus.length() - 5).compareTo(".html") == 0)
			   {
			      writeHTTPHeader(os, "text/html", dirLPlus);
			      writeContent(os, dirLPlus);
			   }
			}
			else
			{
			   error404(os);
			}

			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>My web server works!</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());

			os.flush();
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Request error: " + e);
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

		while (true)
		{
			try
			{
		        String words = "";
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
			    while(line.length() != 0)
			    {
			       words.concat(line + "\n");
			       line = r.readLine();
			    }
			    String[] readList = words.split(" ");
				return readList[1];
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return "";
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/

	private void writeHTTPHeader(OutputStream os, String contentType, String branch) throws Exception
	{

	    File doc = new File(System.getProperty("user.dir") + branch);
		if(doc.exists())
		{
		   Date d = new Date();
		   DateFormat df = DateFormat.getDateTimeInstance();
		   df.setTimeZone(TimeZone.getTimeZone("GMT"));
		   os.write("HTTP/1.1 200 OK\n".getBytes());
		   os.write("Date: ".getBytes());
		   os.write((df.format(d)).getBytes());
		   os.write("\n".getBytes());
		   os.write("Server: Jon's very own server\n".getBytes());
		   // os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		   // os.write("Content-Length: 438\n".getBytes());
		   os.write("Connection: close\n".getBytes());
		   os.write("Content-Type: ".getBytes());
		   os.write(contentType.getBytes());
		   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		   return;
		}
		else
		{
		   Date d = new Date();
		   DateFormat df = DateFormat.getDateTimeInstance();
		   df.setTimeZone(TimeZone.getTimeZone("GMT"));
		   os.write("HTTP/1.1 404 ERROR: Not found\n".getBytes());
		   os.write("Date: ".getBytes());
		   os.write((df.format(d)).getBytes());
		   os.write("\n".getBytes());
		   os.write("Server: Jon's very own server\n".getBytes());
		   // os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		   // os.write("Content-Length: 438\n".getBytes());
		   os.write("Connection: close\n".getBytes());
		   os.write("Content-Type: ".getBytes());
		   os.write(contentType.getBytes());
		   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		   return;
		}
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, String dirLoc) throws Exception
	{
		try
		{
			if(dirLoc.substring(dirLoc.length()-5).compareTo(".html") == 0)
			{
			   BufferedReader readFile = new BufferedReader(new FileReader(dirLoc));
			   String data;

			   while((data = readFile.readLine()) != null)
			   {
				   TimeZone timezone = TimeZone.getTimeZone("America/Mountain");
				   data = data.replaceAll("<cs371date>",getDate("MMMM dd, yyyy",timezone));
				   data = data.replaceAll("<cs371server>", "newton.cs.nmsu.edu");
				   data = data.replaceAll("\"\"", "creepcreep.png");
				   os.write(data.getBytes());
			   }
			   readFile.close();
 			}
 			else if(dirLoc.substring(dirLoc.length()-4).compareTo(".png")==0 || dirLoc.substring(dirLoc.length()-4).compareTo(".jpg")==0 || dirLoc.substring(dirLoc.length()-4).compareTo(".gif") == 0)
 			{
 			   FileInputStream is = new FileInputStream(new File(dirLoc));
			   int reader;
			   while((reader = is.read()) != -1)
			   {
			      os.write(reader);
			   }
 			}

		}
		catch(Exception e)
		{
			System.err.println("Request error: " + e);
		}
	}
	
	private String locRetrieval(InputStream inStream)
	{

		BufferedReader readIn = new BufferedReader(new InputStreamReader(inStream));

		while(true)
		{
			try
			{

				String req = readIn.readLine();
				System.err.println(req);
				String[] readList = req.split(" ");
				return readList[1];

			}
			catch(Exception e)
			{

				System.err.println("Request error: " + e);
				return null;

			}
		}

	}

	private void error404(OutputStream os) throws Exception
	{
		try
		{
			System.out.println("Error 404: The page you were looking for was not found.");
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>ERROR 404: PAGE NOT FOUND.</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
			return;
		}
		catch(Exception e)
		{
			System.err.println("Request error: " + e);
			return;
		}
	}
      
      public String getDate(String df, TimeZone tz)
      {
         Date todayDate = new Date();
         DateFormat todayDateFormat = new SimpleDateFormat(df);
         todayDateFormat.setTimeZone(tz);
         String strTodayDate = todayDateFormat.format(todayDate);
         return strTodayDate;
      }

} // end class
