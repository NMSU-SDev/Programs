package edu.nmsu.cs.webserver;
import java.lang.Object;
import java.nio.file.Files;
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
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class WebWorker implements Runnable
{

	private Socket socket;
	private File currFile = null;
	private boolean fileExists = false;
	private boolean fileRequested = false;
	private boolean fileError = false;
	public String dir = System.getProperty("user.dir");
	public String filePath;

	public boolean isHTML = false, isGIF = false, isPNG = false, isJPEG = false;

	// TODO: add boolean in case no filepath is given

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
	public void run() // TODO: reconficure and organize this
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			// This is where all to work for the assignment happens.
			
			System.out.println(dir);
			filePath = readHTTPRequest(is, os);
			
			// access file
			if (filePath != null) {
				boolean fileExists = true;
				String [] pathName = filePath.split(" ");
				filePath = pathName[1];
				System.out.println(filePath);
				currFile = new File(filePath);
			}	

			try {
				BufferedReader r = new BufferedReader(new FileReader(dir + currFile)); // ERROR HERE
				String line = null;
				fileExists = true;	
			}
			catch (Exception e)
			{
				System.out.println("Request error 1: " + e);
				fileExists = false;
				fileError = true;
			}
				// dynamically call this. Only do one.
			//writeHTTPHeader(os, "text/html");
			writeHTTPHeader(os, getContentType(filePath));
			//writeImage(os, filePath);
			writeContent(os, currFile);
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
	private String readHTTPRequest(InputStream is, OutputStream os)
	{
		String line; 
		String answer = null; // answer is what needs to be returned. 
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				String [] lineSegs = line.split(" ");

				if (lineSegs[0].equals("GET") && !lineSegs[1].equals("/")) {
					answer = line;	
					fileRequested = true;
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
		return answer;
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
		System.out.println(d);
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		// if state to check if file was found
		
		if (fileExists && fileRequested) os.write("HTTP/1.1 200 OK\n".getBytes());
		
		else if (fileRequested && !fileExists) os.write("HTTP/1.1 404 Not Found\n".getBytes());
		else os.write("HTTP/1.1 200 OK\n".getBytes());
		
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

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, File f) throws Exception
	{
			
		if (isHTML) {

			os.write("<html><head>".getBytes());
			os.write("<link rel=\"icon\" type=\"image/x-icon\" href=\"/www/favicon.ico\">".getBytes());
			os.write("</head><body>\n".getBytes());
			os.write("<h3>My web server works!</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());

			if (fileExists) {
				try {
				BufferedReader r = new BufferedReader(new FileReader(dir + f));
				String line = null;
				while((line = r.readLine()) != null) {
					// scan line with special command (1:27)
					if (line.contains("<cs371date>")) {
						String [] segs = line.split("<cs371date>");
						Date now = new Date();
						line = segs[0] + now + segs[1];
						
					}
					if (line.contains("<cs371server>")) {
						String [] segs = line.split("<cs371server>");
						Date now = new Date();
						line = segs[0] + "Z's Server" + segs[1];

					}
					os.write(line.getBytes());
					}
				}
				catch (Exception e) {
					fileError = true;
					//os.write("404 Not Found".getBytes());
				}
			}
		}
		if (isGIF || isJPEG || isPNG) {
			FileInputStream is = new FileInputStream(dir +filePath);
			try {
				int arraysize = is.available();
				byte bytes[] = new byte[arraysize];
				is.read(bytes);
				os.write(bytes);
				is.close();
			}
			catch (Exception e){
				System.out.println("Error accessing Image: " + e.getMessage());
			}
		}

		else {System.err.println("SOMETHING WRONG HERE");}

		if (fileError && fileRequested) { os.write("404 Not Found".getBytes()); }
	}

	private String getContentType(String path){
		if (path.contains("html")) { isHTML=true; return "text/html";}
		if (path.contains("gif")) { isGIF = true; return "image/gif";}
		if (path.contains("png")) { isPNG = true; return "image/png";}
		if (path.contains("jpeg") || path.contains("jpg")) { isJPEG = true; return "image/jpeg";}
		else return null;
	}

	private static void addFavicon(){

	}
} // end class