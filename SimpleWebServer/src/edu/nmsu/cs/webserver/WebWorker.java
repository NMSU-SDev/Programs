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
 * I modified readHTTPRequest() function to return the file referred to by the GET request.
 * I also modified  writeHTTPHeader() function to set the response status to either 200 OK or 
 *   404 Not Found if the file exists and does not exist, respectively. 
 * I changed the writeContent() function to write the HTML file (if it exists) line by line.
 * 
 * 		Mehran Sasaninia
 * 		March 30, 2023
 **/

import java.io.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

public class WebWorker implements Runnable
{

	private Socket socket;
	private String path;

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
			String contenttype = ContentType(path);
			writeHTTPHeader(os, contenttype);
			/* if there is not anything after the port in the URL
			   "My webserver works!" will be shown in output */
            if (path.substring(1).isEmpty())
                os.write("<h3>My web server works!</h3>\n".getBytes());
            else
                writeContent(os, contenttype);
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
	 *
	 * Mainly obtains the file request wants server to serve.
	 *
	 * @param is the HTTP request
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
				// If the line contains "GET" extracts the line to find the path
				if (line.startsWith("GET")) {
					String [] lines = line.split(" ");
					path = lines[1];
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

	private String ContentType (String contenttype)
	{
		if (contenttype.endsWith(".html")) {
			return "text/html";
		} else if (contenttype.endsWith(".gif")) {
			return "image/gif";
		} else if (contenttype.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (contenttype.endsWith(".png")) {
			return "image/png";
		} else {
			return "Unkown contenttype";
		}
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
        File f = new File(path.substring(1));
        if(!f.exists()) {
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}else{
			os.write("HTTP/1.1 200 OK\n".getBytes()); 
		}
		os.write("HTTP/1.1 200 OK\n".getBytes());
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Jason's very own server\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done 
	 * after the HTTP header has been written out.
	 *
	 * This will open the file obtained from http request and begin parsing, if that
	 * file does not exist, the method will write "404 File Not Found" to the output stream.
	 * If file exists any occurrence of the string "<cs371date>" in the file will be replaced
	 * with the current date and time in the format "yyyy/MM/dd HH:mm:ss" and Any occurrence
	 * of the string "<cs371server>" in the file will be replaced with the string "Mehran's server".
	 *
	 * param: os
	 *          is the OutputStream object to write to
	 * postcondition :
	 * 			The contents of the file will be written to the output stream.
	 **/
	private void writeContent(OutputStream os, String contenttype) throws Exception
	{
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		Path file = Paths.get(path.substring(1));
        File f = new File(path.substring(1));
        if(!f.exists()) { 
			os.write(("<h3>404 File Not Found</h3>").getBytes());
			return;
		}
		// If file exists
		else if (contenttype == "text/html") {
			List<String> contents = Files.readAllLines(file);
		    for (String line : contents) {
		        // Replace <cs371date> with current time
				line = line.replace("<cs371date>", dateFormat.format(now)); 
				// Replace <cs371server> with my server name
				line = line.replace("<cs371server>", "Mehran's server");
				os.write(line.getBytes());
			}
		} else if (contenttype == "image/gif" || contenttype == "image/jpeg" || contenttype == "image/png") {
            FileInputStream fileInputStream = new FileInputStream(f);
            int data;
            while ((data = fileInputStream.read()) != -1) {
                System.out.print(data + " ");
			}
		}
	}

} // end class
