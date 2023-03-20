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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;


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

			//Read in request and return a file name
			String requests = readHTTPRequest(is);

			//Finds the content type of a request
			String contentType = getContentType(requests);
			File fil = writeHTTPHeader(os, contentType, requests);
			writeContent(os, contentType, fil);

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
		String [] requests = new String[25];
		int count = 0;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");

				//As long as the request is not an empty line, split the line and retain
				//the actual request from server in requests array
				if (line.length() > 0){
				//Get the request string after http verb
				requests[count] = line.split(" ")[1];
				count++;
				}

				if (line.length() == 0){
					break;
				}

			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}

		//return the server's requested file name
		return requests[0];
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 *
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 * @return File req
	 * 			Requested file from server
	 **/
	private File writeHTTPHeader(OutputStream os, String contentType, String requests) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));

		//Build the path for a requested file and create a File object based on path
		String path = "./www/res/acc" + requests;
		File req = new File(path);


		//if the file exists or is favicon.ico, print 200 OK, else print out 404 Not Found
		if(requests.contains("favicon")){
			os.write("HTTP/1.1 200 OK\n".getBytes());}
		else if (req.exists()){
			os.write("HTTP/1.1 200 OK\n".getBytes());}
		else if (!req.exists()){
			os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Trang\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines

		return req;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 *
	 * @param os
	 *          is the OutputStream object to write to
	 * I used an article in GeeksForGeeks to help me write the code to serve a file.
	 * https://www.geeksforgeeks.org/java-program-to-extract-content-from-a-html-document/
	 * @throws IOException
	 *
	 **/
	private void writeContent(OutputStream os, String contentType, File fil) throws IOException
	{
		/**
		* if file exists, depending on the contentType, write to output stream the appropriate
		* bytes or text
		*/
		if (fil.exists() && !fil.isDirectory()) {
			 //if file request is for favicon-icon or other image file, the bytes will be written
			 //to the output stream
			 if (contentType.contains("image")){
				FileInputStream fis = new FileInputStream(fil);
				int cursor;

				while((cursor = fis.read()) != -1) {
					os.write(cursor);
				}

				fis.close();
			}

			//if it is an html file, bufferedReader will pull every html line & append it
			//to one String. This string will be what is outputed to server
			else if(contentType.contains("html")){
				FileReader fr = new FileReader(fil);
				BufferedReader br = new BufferedReader(fr);
				String s;
				String htmlLines = "";
				while ((s = br.readLine()) != null) {
					htmlLines += s;
				}

				//find and output <img> tag from html
				htmlLines = htmlLines.replaceAll("\"img\"", "lemon.jpg");

				//set date
				Date d = new Date();
				DateFormat df = DateFormat.getDateTimeInstance();
				df.setTimeZone(TimeZone.getTimeZone("MST"));
				htmlLines = htmlLines.replaceAll("<today>", df.format(d));

				//set server
				htmlLines = htmlLines.replaceAll("<csServer>", "all about citrus");

				//serve file
				os.write("<html><head></head><body>\n".getBytes());
				os.write(htmlLines.getBytes());
				os.write("</body></html>\n".getBytes());
				br.close();
				fr.close();
			}
		}

		/**
		 * If there is no specified file name to output, but the directory exists
		 */
		else if (fil.exists() && fil.isDirectory()){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>Homepage</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}

		/**
		 * If the file does not exist, output "404 Not Found"
		 */
		else {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 Not Found</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}
	}

	/**
	 * This method will find the content type requested
	 * @param requests
	 * @return String contentType
	 */
	private String getContentType(String requests){

		if (requests.contains("jpg")){
			return "image/jpeg";
		}
		else if (requests.contains("png")){
			return "image/png";
		}
		else if (requests.contains("gif")){
			return "image/gif";
		}
		else if (requests.contains("favicon")){
			return "image/x-icon";
		}
		else {
			return "text/html";
		}
	}

} // end class
