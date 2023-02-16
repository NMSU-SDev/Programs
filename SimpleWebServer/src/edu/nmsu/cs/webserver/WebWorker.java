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
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.IOException;



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
			String fName = "";

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			fName = readHTTPRequest(is);
			writeHTTPHeader(os, "text/html", fName); // Writes header based on file
			writeContent(os, fName);
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
		String line = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try // Listening for new line readings
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine(); // Reads server GET request

				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
				else return line; // New!
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return line;
	}



/**
 * Read HTML file and write its contents to web page. Also, replaces
 * use of tags <cs371date> and <cs371server> with specified strings.
 * 
 * @precondition HTML file exists under fName
 * @precondition should be used in writeContent method
 * @param fName
 * @param os
 */
private void writeWebPage(String fName, OutputStream os) throws Exception{

	// Get current date
	Date d = new Date();
	DateFormat df = DateFormat.getDateTimeInstance();
	df.setTimeZone(TimeZone.getTimeZone("MST")); // Changed to MST

	String line = "";

	fName = fName.substring(5,fName.length()-9); // getting only file name itself

	if(fName.length() < 1) return; // if there's no significant input.

	try{ // Try to open filereader
		FileReader fReader = new FileReader(fName);
		BufferedReader bReader = new BufferedReader(fReader);

		// While file has lines remaining
		while ((line = bReader.readLine()) != null) {

			if(line.contains("<cs371date>")){
				line = line.replace("<cs371date>", df.format(d));
			} if(line.contains("<cs371server>")){
				line = line.replace("<cs371server>", "Ruby H's webserver");
			} // end if

			os.write(line.getBytes()); // write each line of file to webpage
		} // end while

		fReader.close();
		bReader.close();

	} catch(FileNotFoundException e){
		System.err.println("File missing at \"" + fName + "\"");
		os.write("<h1>404 Not Found</h1>\n".getBytes());
	} catch(IOException e){
		System.err.println("Error reading file \"" + fName + "\"");
		os.write("<h1>404 Not Found</h1>\n".getBytes());
	}

} // end writeWebPage



	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String fName) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("MST")); // Changed to MST

		// if we encounter a reading error write 404 not found
		fName = fName.substring(5,fName.length()-9); // getting only file name itself

		File f = new File(fName);
		if(!f.exists() || f.isDirectory()) { 
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		} else{
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Ruby's very own server\n".getBytes());
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
	private void writeContent(OutputStream os, String fName) throws Exception
	{
		os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>My web server works!</h3>\n".getBytes());
		writeWebPage(fName, os);
		os.write("</body></html>\n".getBytes());
	}

} // end class
