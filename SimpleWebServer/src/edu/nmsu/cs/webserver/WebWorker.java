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

 // cd C:\Users\brock\OneDrive\Desktop\SWD371\Programs\SimpleWebServer\src
 // javac edu/nmsu/cs/webserver/*.java -d ../bin
 // cd..
 // java -cp bin edu.nmsu.cs.webserver.WebServer

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileReader;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.*;

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

			// 2/17/23 
			// Altered readHTTPRequest and writeHTTPHeader to return string
			// They each return the served HTML file in string formart
			String res = readHTTPRequest(is);
			String final_res = writeHTTPHeader(os, "text/html", res);
			writeContent(os, final_res);
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
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");

				if (line.length() == 0)
					break;

				// Split each reaed line by whitespace
				String[] splitStr = line.trim().split("\\s+");
				int stringSize = splitStr.length;

				// Iterate over elements of string
				for (int i = 0; i < stringSize; i++){

					// If we have an 'GET' - then we know the next element is path
					if (splitStr[i].equals("GET")) {

						// THIS IS OUR PATH - splitStr[i+1]
						String path = splitStr[i + 1];

						// Get path up to SimpleWebServer
						String prefix_path = System.getProperty("user.dir");

						// Append the path to SWS and to target HTML file
						String final_path = prefix_path + path;

						// Correct forward slashes
						String fixed_path = final_path.replace("/","\\");

						// Instantiate builder to return string
						StringBuilder html = new StringBuilder();

						// TRY - to read file path
						// If we cannot read, we default to 404
						try {
							FileReader fr = new FileReader(final_path);

							BufferedReader br = new BufferedReader(fr);

							String val;
							while (((val = br.readLine()) != null)){
								html.append(val);
							}

							String result = html.toString();
							System.err.println("Content received, continuing to writeHeader...");
							return result;
							
						} // try
						
						// If we can't serve the provided HTML, return 404
						catch(Exception ex){
							return "404";

						} // catch

					} // if
				} // for 
			} //try 

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
	/*
	 * Brock Middleton - 2/17/23
	 * Added parameter 'result
	 * Result is the content from the served HTML file
	 * If we were given an HTML file, but could not read it, we return 404
	 * If we were not given an HTML, we return a default page. 
	*/
	private String writeHTTPHeader(OutputStream os, String contentType, String result) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate = df.format(d);

		// If result is 404, give proper header
		if (result == "404"){
			os.write("HTTP/1.1 404 NOT FOUND".getBytes());
		}
		else {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}

		// Replace DateTag
		String result1 = result.replace("<cs371date>", strDate);
		// Replace ServerTag
		String result2 = result1.replace("<cs371server>", "Brock's web server");

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Brock's web server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		System.err.println("Header written, continuing to writeContent...");

		return result2;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, String result) throws Exception
	{
		// If 404, write 404
		if (result == "404"){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 - file not found.</h3>\n".getBytes());
		    os.write("</body></html>\n".getBytes());

		}
		// If no HTML provided, give default
		else if (result.length() == 0){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>Brock's web server works! No HTML file provided.</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}
		// Else, write result
		else {
			os.write(result.getBytes());
		}
	}

} // end class
