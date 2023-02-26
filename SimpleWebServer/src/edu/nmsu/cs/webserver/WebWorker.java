package edu.nmsu.cs.webserver;

//// CS 468 -- Software Development P1
//// Program: WebWorker.java
//// Name: Rob Armendariz
//// Date: 02-17-2023
//// Description: 

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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;


public class WebWorker implements Runnable
{
	// used to enable debug statements
	boolean myDebug = true;

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
		// starting connection with client
		System.err.println("Handling connection...");
		try
		{
			
			// define and initialize InputStream and OutputStream
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			// read request and store output (path) of method into string 'path'
			String path = readHTTPRequest(is);
			
			// File object contains possible path
			File checkPath = new File(System.getProperty("user.dir") + path);
			
			// request is a ".gif" file and is valid file path
			if((path.substring(path.length()-4).compareTo(".gif") == 0) && checkPath.exists()){
				// write the HTTP Header to output stream, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct 
				// HTTP header based on validity
				writeHTTPHeader(os, "image/gif", path);
				if(myDebug) {
					System.out.println("----[.gif] recognized----");
				}
				
				// write data content to client network connection, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct content
				// based on validity
				writeContent(os, path, 0);
			}
			
			// request is a ".jpg" file and is valid file path
			else if((path.substring(path.length()-4).compareTo(".jpg") == 0) && checkPath.exists()){
				// write the HTTP Header to output stream, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct 
				// HTTP header based on validity
				writeHTTPHeader(os, "image/jpg", path);
				if(myDebug) {
					System.out.println("----[.jpg] recognized----");
				}
				
				// write data content to client network connection, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct content
				// based on validity
				writeContent(os, path, 0);
			}
			
			// request is a ".png" file and is valid file path
			else if((path.substring(path.length()-4).compareTo(".png") == 0) && checkPath.exists()){
				// write the HTTP Header to output stream, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct 
				// HTTP header based on validity
				writeHTTPHeader(os, "image/png", path);
				if(myDebug) {
					System.out.println("----[.png] recognized----");
				}
				
				// write data content to client network connection, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct content
				// based on validity
				writeContent(os, path, 0);
			}
			
			// request is a ".html" file or INCORRECT FILE/PATH NAME
			else{
				// write the HTTP Header to output stream, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct 
				// HTTP header based on validity
				writeHTTPHeader(os, "text/html", path);
				if(myDebug) {
					System.out.println("----[.html] recognized----");
				}
				
				// write data content to client network connection, give 'path' as parameter to determine
				// whether file trying to be served is valid or not, then output the correct content
				// based on validity
				writeContent(os, path, 1);
			}
			
			// flush the output stream
			os.flush();
			
			// close socket (end of data stream for the moment)
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		
		// connection with client is done
		System.err.println("Done handling connection.");
		
		return;
		
	} // end of run()

	
	/**
	 * Read the HTTP request header.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		// used to store each line
		String line;
		
		// buffer taking in stream
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
		// new StringBuilder to be used to store the GET request
		StringBuilder request = new StringBuilder();
		
		// used to store the actual path from GET request
		String path = "";
		
		while (true)
		{
			try
			{
				// no line is currently in buffer
				while (!r.ready())
					Thread.sleep(1);
				
				// read current line
				line = r.readLine();
				
				// while line is not of length 0
				while(!(line.isEmpty())) {
					
					// append each line to 'request'
					request.append(line + "\n");
					
					// fetch new line
					line = r.readLine();
				}
				
				// this is the first line of input stream (which contains the directory we must attempt to serve)
				String[] tokens = request.toString().split(" ");
				
				// 'path' is the directory where the file is that we must serve
				path = tokens[1];
				
				// debug statement: file path and favicon info
				if(myDebug) {
					System.out.println(path);
//					String favicon = tokens[3];
//					System.out.println(favicon);
				}
				
				// return path as string
				return path;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		// return path as string
		return path;
		
	} // end of readHTTPRequest(...)

	
	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 * @param filePath
	 * 			this is the file path of the file we want to write to output stream
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String filePath) throws Exception
	{
		
		// File object contains possible path
		File checkPath = new File(System.getProperty("user.dir") + filePath);
		
		// if path is not valid
		if(!checkPath.exists()) {
			
			// HTTP header for INVALID path trying to be served
			if(myDebug) {
				System.out.println("INVALID PATH HEADER USED");
			}
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
			os.write("Server: Rob's very own server\n".getBytes());
			// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
			// os.write("Content-Length: 438\n".getBytes());
			os.write("Connection: close\n".getBytes());
			os.write("Content-Type: ".getBytes());
			os.write(contentType.getBytes());
			os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
			return;
		}
		
		// path is valid
		else {
			if(myDebug) {
				System.out.println("VALID PATH HEADER USED");
			}
			// HTTP header for VALID path trying to be served
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			os.write("HTTP/1.1 200 OK\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
			os.write("Server: Rob's very own server\n".getBytes());
			// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
			// os.write("Content-Length: 438\n".getBytes());
			os.write("Connection: close\n".getBytes());
			os.write("Content-Type: ".getBytes());
			os.write(contentType.getBytes());
			os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
			return;
		}
		
	} // end of writeHTTPHeader(...)

	
	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param filePath
	 * 			this is the file path of the file we want to write to output stream
	 * @param type
	 * 			type 0: ".gif", ".jpg", and ".png" files
	 * 			type 1: ".html" files
	 **/
	private void writeContent(OutputStream os, String filePath, int type) throws Exception
	{	
		// File object contains possible path
		File checkPath = new File(System.getProperty("user.dir") + filePath);
		
		// if path is not valid, provide the HTML message body
		if(!checkPath.exists()) {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 Not Found</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}
		
		// path is valid		
		else {
			
			// write content for ".gif", ".jpg", and ".png" files
			if(type == 0) {
				if(myDebug) {
					System.out.println("Writing .gif content to os!");
				}
				
				// read in the file input
				FileInputStream in = new FileInputStream(new File(System.getProperty("user.dir") + filePath));
				int cursor;
				
				// write out content to output stream
				while((cursor = in.read()) != -1) {
					os.write(cursor);	
				}
			}
			
			// write content for ".html" files
			else if(type == 1) {
				if(myDebug) {
					System.out.println("Writing .html content to os!");
				}
				
				// scanner gets the proper path
				Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + filePath));
				
				// path as string
				String htmlFile = checkPath.toString();
				
				// get path
				Path path = Paths.get(htmlFile);
	
				// store HTML file contents into "content"
				String content = new String(Files.readAllBytes(path));
				
				// check if .html file contains "<img>" tag
				if(content.contains("<img>")) {
					content = content.replaceAll("\"\"", "mcgavin.gif");
				}
				
				// set date and time format
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");  
				
				// get current date and time
				LocalDateTime now = LocalDateTime.now();
				
				// get date and time as a formatted string
				String dateTime = dtf.format(now);
				
				// replace tag "<cs371date>" with current date and time
				content = content.replaceAll("<cs371date>", dateTime);
				
				// replace tag "<cs371server>" with name of server
				content = content.replaceAll("<cs371server>", "Rob's Server");
	
				// close scanner instance
				scanner.close();
				
				// write the data content to the client network connection
				os.write(content.getBytes());
			}
		}	
		
	} // end of writeContent(...)

} // end class
