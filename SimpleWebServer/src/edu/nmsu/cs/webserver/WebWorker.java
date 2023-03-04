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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WebWorker implements Runnable
{   // Stores file path
	private String filePath;

	// Stores type of content
	private String contentString;
	
	private Socket socket;
	
	// Keeps track if file exists
	private static boolean tag;

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
			writeHTTPHeader(os, contentString);
			writeContent(os);
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
	private void readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready()) {
					Thread.sleep(1);
				}
				
				line = r.readLine();

				// Stores path of GET request
				if (line.startsWith("GET")) {
					String [] request = line.split(" ");
					filePath = request[1];

					Path test = Paths.get(filePath.substring(1));
					
					// Updates tag to whether file exists or not
<<<<<<< HEAD
					if (Files.exists(test) && !Files.isDirectory(test)) {
						tag = true;
					}

					else {
						tag = false;
					}
					
					// Finds what type of content request is
					if (filePath.endsWith(".jpeg")) 		
						contentString = "image/jpeg";
					else if(filePath.endsWith(".png"))
						contentString = "image/png";
					else if (filePath.endsWith(".gif"))
						contentString = "image/gif";
					else
						contentString = "text/html";
=======
					if (Files.exists(test) && !Files.isDirectory(test)) 
						tag = true;
					else 
						tag = false;
>>>>>>> a1fd5937b738fb5b87f9c66de10e3ef9c85287b3
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

		// If file exists, the header status is 200 OK
		if (tag) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}
		// If it doesn't, the header status is 404 Not Found
		else {
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Pat's very own server\n".getBytes());
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
		// Formats date accordingly
		String date = "MMMM, dd yyyy";
		SimpleDateFormat dateF = new SimpleDateFormat(date);
		String result = dateF.format(new Date());

<<<<<<< HEAD
		// DEFAULT REQUEST - hello.html
		if ("/".equals(filePath))
			filePath ="/hello.html";

=======
>>>>>>> a1fd5937b738fb5b87f9c66de10e3ef9c85287b3
		Path file = Paths.get(filePath.substring(1));

		// Checks if file exits once more
		if (Files.exists(file) && !Files.isDirectory(file)) {
			// Puts contents of file in list
<<<<<<< HEAD
			if (contentString.equals("text/html")) {
				List<String> fileContents = Files.readAllLines(file);
				
				// Reads list line by line
				for (String line : fileContents) {
					// If a specific tag is found, replace with correct info
					if (line.contains("<cs371date>")) {
						line = line.replace("<cs371date>", result);
					}

					if (line.contains("<cs371server>")) {
						line = line.replace("<cs371server>", "Patrick Jojola's server");
					}

					// Prints line to screen
					os.write(line.getBytes());
				}
		    }

			// If file is an image
			else {
				os.write(Files.readAllBytes(file));
			}
		
=======
			List<String> fileContents = Files.readAllLines(file);
			
			// Reads list line by line
			for (String line : fileContents) {
				// If a specific tag is found, replace with correct info
				if (line.contains("<cs371date>")) {
					line = line.replace("<cs371date>", result);
				}
				if (line.contains("<cs371server>"))
					line = line.replace("<cs371server>", "Patrick Jojola's server");
				// Prints line to screen
				os.write(line.getBytes());
			}
>>>>>>> a1fd5937b738fb5b87f9c66de10e3ef9c85287b3
		}
		
		// File doesn't exist, 404 Not Found html body is produced
		else {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h1>404 File Not Found</h1>\n".getBytes());
			os.write("</body></html>".getBytes());
		}
	}

} // end class