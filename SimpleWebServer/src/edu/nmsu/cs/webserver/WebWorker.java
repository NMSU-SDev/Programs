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
import java.io.*;
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
			// Get file path
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			 String filePath = readHTTPRequest(is);

			 // Check for file stte
			File file = new File("." + filePath);
			if (file.exists() && !file.isDirectory()) {
				String contentType = getContentType(filePath);
				writeHTTPHeader(os, contentType);
				writeContent(os, file);
			} else {
				writeHTTPHeader(os, "text/html");
				os.write("<h1>404 NOT FOUND</h1>\n\n".getBytes());
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
	private String readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String filePath = "";
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if (line.startsWith("GET")) {
					String[] parts = line.split(" ");
					if (parts.length > 1) {
						filePath = parts[1];
					}
				}
				while (line.length() != 0) {
					line = r.readLine();
				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);

			}
		return filePath;
	}

	// Get file content type
	private String getContentType(String filePath) {
		 String contentType = "text/plain";
		 if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
			 contentType = "text/html";
		 } 
		 return contentType;
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
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Raul's very own server\n".getBytes());
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
	private void writeContent(OutputStream os, File file) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = reader.readLine()) != null) {
  
			  // Replace the <cs371date> tag with the current date
				if (line.contains("<cs371date>")) {
					DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
					String currentDate = df.format(new Date());
					line = line.replace("<cs371date>", currentDate);
					// Replace the <cs371server> tag with the server identification string
				} if (line.contains("<cs371server>")) {
					line = line.replace("<cs371server>", "Rauls's Server");
				}
				   // Write the modified line to the output stream
				os.write(line.getBytes());
				os.write("\n".getBytes());
			}
			fis.close();
		} catch (IOException e) {
			System.err.println("Error reading file: " + e);
		}
	}

} // end class