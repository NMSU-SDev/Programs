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
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class WebWorker implements Runnable
{
	private String pathName; 
	private Boolean fileExists; 	
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
			readHTTPRequest(is);
			writeHTTPHeader(os, "text/html");
			writeContent(os);
			os.flush();
			socket.close();
		}
		catch (Exception e)
		{System.err.println("WRONG CODE");
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
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


				// Finds request that starts with GET and find files that are requested if available or not
				// GET /favicon.ico HTTP/1.1)

				if( line.startsWith("GET")) { 
					String[] request = line.split(" "); 
					pathName = request[1].substring(1); // char '/' 
					File requestFile = new File (pathName); 
					
					// Checks if file exists 
					if( requestFile.exists() && !requestFile.isDirectory() ) 
						fileExists = true; 
					else 
						fileExists = false; 
				
				}

				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return;
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
		
		if(fileExists == true){
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}
		else
			os.write("HTTP/1.1 404 Not Found\n".getBytes());


		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());

		os.write("Server: Rey's very own server\n".getBytes());
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
	private void writeContent(OutputStream os) throws Exception
	{
		File requestFile = new File (pathName);

		SimpleDateFormat date = new SimpleDateFormat("DD-MM-YYYY"); 
		String dateFinal = date.format(new Date());

		// Check first if file exists 
		if( requestFile.exists() && !requestFile.isDirectory() ) {
			os.write("<html><head></head><body>\n".getBytes());
			Scanner scan = new Scanner(requestFile);

			while (scan.hasNextLine()) {
				String line = scan.nextLine();

				if (line.contains("<Cs371date>"))
					line = line.replace("<Cs371date>", dateFinal);

				if (line.contains("<Cs371server>"))
					line = line.replace("<Cs371server>", "Bianka's Server"); 

				os.write(line.getBytes());
			} // end of while 
			os.write("</body></html>\n".getBytes());
			scan.close();		
				
		} // end of if 

		// if file is not found 
		else { 
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h1>404 Error: File Not Found</h1>\n".getBytes());
			os.write("</body></html>\n".getBytes());	
	}

} // end class
