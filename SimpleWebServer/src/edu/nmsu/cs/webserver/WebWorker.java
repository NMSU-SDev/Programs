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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.FileReader;

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
			String file = readHTTPRequest(is);
			if(writeHTTPHeader(os, file)){
				writeContent(os, file);
			} else {
				os.write("<html><head></head><body>\n".getBytes());
				os.write("<h3>ERROR! CODE: 404 NOT FOUND</h3>\n".getBytes());
				os.write("</body></html>\n".getBytes());
			}
			
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
	 * Read the HTTP request header and return String of file name.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		String line;
		String fileName = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if(line.startsWith("GET")){
					fileName = line.substring(5, line.length() - 9);
					// excludes ':8080' from file name
				}
				if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return fileName;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * Return boolean flag True if file found and False if not found
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 * @param fileName
	 * 			is the string for the name of file served
	 **/
	private boolean writeHTTPHeader(OutputStream os, String fileName) throws Exception
	{
		String contentType = "text/html"; // hardcoded for this assignment and removed from header
		boolean pageFlag; // flag for determining if file could be found or not
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		try
		{
			if(!fileName.equals("")){
				FileReader fileRead = new FileReader(fileName);
			}
			os.write("HTTP/1.1 200 OK\n".getBytes());
			pageFlag = true; //file was found
		}
		catch(Exception e){
			os.write("HTTP/1.1 404 Not Found\n".getBytes());  //HTTP header for incorrect file request
			pageFlag = false; //file was not found
		}
		
		
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Zack's server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines

		return pageFlag;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param fileName 
	 * 			is the string for name of file used for HTML content
	 **/
	private void writeContent(OutputStream os, String fileName) throws Exception
	{try{

		if(fileName.equals("")){  //default HTML message body if no file path provided
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>My web server works!</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		} else {
			BufferedReader buffRead = new BufferedReader(new FileReader(fileName));
			String currLine;
			String currDate = new java.util.Date().toString();
			if(fileName.endsWith(".html")){
				for(currLine = buffRead.readLine(); currLine != null; currLine = buffRead.readLine()){
					// loops through HTML file replacing tags line by line
					currLine = currLine.replaceAll("<cs371date>", currDate);
					currLine = currLine.replaceAll("<cs371server>", "Zackery Meyer's CS371 Server");
					os.write(currLine.getBytes());
				}
			} 
		}

		}catch(Exception e){
			System.err.println("Output error: " + e);
		}
		
	}

} // end class
