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
 
 /**
	Webworker actually serves html files. Respondes given an incorrect filename to header and 404 not found page.
	Processes dynamic html tags for date and server name.
	Ryan Schwarzkopf
 
  */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

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
			String fileRequest = readHTTPRequest(is);
			String contentType = readFileType(fileRequest);
			int HTTPstatus = writeHTTPHeader(os, contentType, fileRequest);
			writeContent(os, fileRequest, HTTPstatus, contentType);
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
	/**
	 * Search request header for any file request. Print to console if found.
	 * Ryan Schwarzkopf
	 * 
	 */
	private String readHTTPRequest(InputStream is)
	{
		String line;
		String fileRequest = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if((line.length() > 3) && (line.substring(0, 3).compareTo("GET") == 0) && (line.length() != 14)) {
					fileRequest = line.substring(5, line.length()-9);
					System.err.println("File name found: ("+fileRequest+")");
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
		return fileRequest;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	/**
	 * Search for fileRequest in top directory. Write http header 200 or 404. Return 200 or 404
	 * Ryan Schwarzkopf
	 * 
	 * @param fileRequest
	 * @return 200: file found 404: file not found
	 */
	private int writeHTTPHeader(OutputStream os, String contentType, String fileRequest) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		File f = new File(fileRequest);
		if(f.exists()) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		} else {
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}
			os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Ryan's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		if(!f.exists()) return 404; // Could not find the requestFile
		return 200; // requestFile found
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	/**
	 * Search for file and write its contents to output stream. Dynamic HTML for date and server name. 
	 * Returns 200 if file is found. 404 if not found
	 * Ryan Schwarzkopf
	 *  
	 * @param filePath
	 * 	path to file to read
	 *
	 */
	private void writeContent(OutputStream os, String filePath, int HTTPstatus, String contentType) throws Exception
	{
		if(HTTPstatus == 404) { // File was not found. Cannot write content.
		os.write("".getBytes());
			os.write("<!DOCTYPE html><html><head><link rel=\"icon\" type=\"image/x-icon\" href=\"https://icons8.com/icon/35747/mario-8-bit\"></head><body><p>404: Not found</p></body>".getBytes());
		} else {
			if(contentType.equals("text/html")) { // Case text/html
				try {
					// Get the date
					Date date = new Date();
					DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
					String theDate = formatter.format(date);
					// Get a file reader
					BufferedReader in = new BufferedReader(new FileReader(filePath));
					String line;
					while((line = in.readLine()) != null) {
						line = line.replace("cs371date", theDate);
						System.out.println(line);
						line = line.replace("cs371server", "Ryan's server");
						os.write(line.getBytes());
					}
					in.close();
					return;
				} catch(FileNotFoundException e) {
					System.err.println("File not found.");
					return;
				}
			} else {
				if (contentType.equals("image/jpeg") || contentType.equals("image/gif") || contentType.equals("image/png")) {
					try {
						// Load an image from a file
						File imageFile = new File("www/res/acc/image.jpg"); // Replace with your image file path
						BufferedImage image = ImageIO.read(imageFile);
			
						// Write the image to the OutputStream
						ImageIO.write(image, "jpg", os);
			
						System.out.println("Image written to OutputStream successfully.");
					} catch (IOException e) {
						System.out.println("Error writing image to OutputStream: " + e.getMessage());
					}
				}
			} // end if/else
		} // end file not found catch
	}

	private String readFileType(String fileRequest) {
		if(fileRequest.contains("html")) return "text/html";
		if(fileRequest.contains("jpeg")) return "image/jpeg";
		if(fileRequest.contains("png")) return "image/png";
		if(fileRequest.contains("gif")) return "image/gif";
		return "text/html";
	}

} // end class
