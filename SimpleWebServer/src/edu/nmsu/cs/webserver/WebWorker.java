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
 * @author Jon Cook, Ph.D.
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
import java.io.FileReader;
import java.nio.file.Files;


public class WebWorker implements Runnable
{

	private Socket socket;
	private File fileToGet;

	//data used for header information
	//dontent type currently constant
	private String contentType;
	private String statusCode;

	// used later for reading file and instream
	private BufferedReader r = null;
	private FileReader fr= null;

	//for date formatting later
	Date d;
	DateFormat df;




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
		
		d = new Date();
		df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));


		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			readHTTPRequest(is);
			writeHTTPHeader(os);
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
		r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				if (line.length() == 0)
					break;

				System.err.println("Request line: (" + line + ")");
				
				//if the request is a get request
				if (line.indexOf("GET") != -1) {

					// attempt to make a file reader of the file at that location
					try {
						String fileName = line.substring(line.indexOf(" ") + 1, line.lastIndexOf(" "));
						fileToGet = new File(System.getProperty("user.dir") + fileName);

						fr = new FileReader(fileToGet);
						//if successful, change status to 200 OK because the file exists
						statusCode = "HTTP/1.1 200 OK\n";

						//change the content type for the header by giving the file content type
						changeContentType(fileName.substring(fileName.indexOf(".") + 1));
					}

					//if not successful, then change status to 404 Not Found and header to html for printing
					catch (Exception e) {
						contentType = "text/html";
						statusCode = "HTTP/1.1 404 Not Found\n";
					}
				}

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
	private void writeHTTPHeader(OutputStream os) throws Exception
	{

		os.write(statusCode.getBytes());
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Nick's very own server\n".getBytes());
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
		
		//System.out.println(statusCode);
		//System.out.println(fileToGet.toString());


		//make sure there is no 404 error
		if (statusCode != "HTTP/1.1 404 Not Found\n")  {

			//if this is a text file then read line by line
			if (contentType.substring(0, contentType.indexOf("/")).compareTo("text") == 0) {

				//reassign r to a reader for the file
				r = new BufferedReader(fr);

				String ln = r.readLine();
				int index;

				//while there is something to print from the file
				while (ln != null) {



					//if there is "<cs371date>" then replace it with the date format
					index = ln.indexOf("<cs371date>");
					if (index != -1) {
						ln = ln.substring(0, index) + df.format(d) + ln.substring(index + 11);
					}
					
					//if there is "<cs371server>" then replace it with "xXGamerBoiXx's Server" cuz why not
					index = ln.indexOf("<cs371server>");
					if (index != -1) {
						ln = ln.substring(0, index) + "xXGamerBoiXx's Server" + ln.substring(index + 13);
					}
					
					os.write(ln.getBytes());
					ln = r.readLine(); 
				}
			}

			// else if its an image file read all bytes at once
			else if (contentType.substring(0, contentType.indexOf("/")).compareTo("image") == 0) {
				os.write(Files.readAllBytes(fileToGet.toPath()));
			}

		}

		//else print 404 not found for user
		else {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 File Not Found!</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}
	}

	
	// method to take the file type and assign the header accordingly
	private void changeContentType(String fileType) {
		if (fileType.compareTo("html") == 0) {
			contentType = "text/html";
		}
		
		else if (fileType.compareTo("txt") == 0)
			contentType = "text/plain";

		else if (fileType.compareTo("gif") == 0)
			contentType = "image/gif";

		else if (fileType.compareTo("jpg") == 0)
			contentType = "image/jpeg";

		else if (fileType.compareTo("png") == 0)
			contentType = "image/png";

		else if (fileType.compareTo("ico") == 0)
			contentType = "image/x-icon";

		else
			contentType = "";
	}


} // end class
