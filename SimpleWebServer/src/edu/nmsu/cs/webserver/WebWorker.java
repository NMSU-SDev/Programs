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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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

			String[] request = readHTTPRequest(is);
			String requestString = String.join(" ", request); //String used to store file if it is requested
			String filePath = requestString.split(" ")[1]; //Filepath isolated from request
			File htmlFile = new File("."+filePath); 
			if(htmlFile.exists() && htmlFile.isFile()){ //The server checks if the file requested exists
				String contentType = Files.probeContentType(Path.of(htmlFile.getAbsolutePath()));
				writeHTTPHeader(os, contentType);
				FileInputStream fis = new FileInputStream(htmlFile);
				String content = new String(Files.readAllBytes(htmlFile.toPath())); //Contents of html file read to string variable
				DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
				Date currentDate = new Date();
				String currentDateFormatted = dateFormat.format(currentDate);
				content = content.replaceAll("<cs371date>", currentDateFormatted); //Replace all used to substitute html tags for corresponding info
				String serverIdentificationString = "Marco the Electrical Engineer's server";
				content = content.replaceAll("<cs371server>", serverIdentificationString);
	
				os.write(content.getBytes());
				fis.close();
			}
			else{
				writeHTTPHeader(os, "text/html"); //If no file exists, 404 message written to header
				os.write("<html><head></head><body>\n".getBytes());
				os.write("HTTP/1.1 404 Not Found\n".getBytes());
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
	 * Read the HTTP request header.
	 **/
	private String[] readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder request = new StringBuilder();
			try
			{
				while ((line = r.readLine()) != null) {
					request.append(line).append("\r\n");
					if(line.isEmpty()){
						break;
					}
					
				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				//break;
			}
		return request.toString().split("\\r?\\n");
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		os.write("HTTP/1.1 200 OK\n".getBytes());
		os.write("Date: ".getBytes());
		os.write("Server: Marco's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		os.write(("Content-Type: " + contentType + "\r\n").getBytes());
		os.write("\r\n".getBytes()); 
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os) throws Exception
	{

		os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>File not found!</h3>\n".getBytes());
		os.write("</body></html>\n".getBytes());
		}
	}

 // end class
