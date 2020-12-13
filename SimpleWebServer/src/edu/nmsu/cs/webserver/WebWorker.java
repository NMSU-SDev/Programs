package edu.nmsu.cs.webserver;

import java.io.BufferedInputStream;

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

/**
 * This simple web server was modified to have HTML file delivery work, a 404 response, 
 * tag substitution, serve image files in the GIF, JPEG, and PNG formats and the extra 
 * credit of creating an icon for the server and properly send it to the browser upon request.
 * Modified by Meagan Waldo
 * Last date modified: 10/29/20
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class WebWorker implements Runnable
{
	int code; // This will contain the HTTP response code which is 200 or 404.
	String type; // This will contain the mime type.
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
			String neededFileName; // This will be the name of the file requested.

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			neededFileName = readHTTPRequest(is); // Initializes neededFileName with the name of the file.

			setType(neededFileName); // Sets the mime type.

			File file = new File(neededFileName); // Creates a file instance using the requested file name.

			// If the neededFileName is not null then check for the file.
			if(neededFileName != null) {

				// If the file is found then the code is 200. Else the code is 404.
				if(file.exists() && !file.isDirectory()) {
					code = 200;
				} // end of if
				else {
					code = 404;
				} // end of else
			} // end of if

			writeHTTPHeader(os, type);
			writeContent(os, file);
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
	 * @return neededFile
	 * 		      is a string that contains the name of the file requested.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		String line;
		String neededFile = null; // String that will hold the name of the file requested.

		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");

				// If the line starts with GET then you have found the name of the file requested.
				if(line.startsWith("GET")) {
					neededFile = line.substring(5, line.length() - 9); // Sets neededFile to the name of the requested file.
				} // end of if

				if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return neededFile;
	}

	/**
	 * Sets the mime type.
	 * @param neededFile
	 *          is the name of the file along with its mime type.
	 **/
	private void setType(String neededFile) {

		// Finds which mime type the file has and then sets the variable type to that mime type.
		if(neededFile.endsWith(".html")) {
			type = "text/html";
		} // end of if
		else if(neededFile.endsWith(".jpg")) {
			type = "image/jpeg";
		} // end of else if
		else if(neededFile.endsWith(".png")) {
			type = "image/png";
		} // end of else if
		else if(neededFile.endsWith(".gif")) {
			type = "image/gif";
		} // end of else if
		else if(neededFile.endsWith(".ico")) {
			type = "image/x-icon";
		} // end of else if
	} // end of setType

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

		// If the neededFile exists then the code is 200 else it is a 404 error.
		if(code == 200) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		} // end of if
		else {
			os.write("HTTP/1.1 404 Not Found\n".getBytes());	
		} // end of else

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
	 * @param neededFile 
	 * 			is a file that contains information to be written out to the page.
	 **/
	private void writeContent(OutputStream os, File neededFile) throws Exception
	{

		Date dateNow = new Date(); // This gets the current date.
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy"); // This formats the date.

		String currentDate = formatter.format(dateNow); // The current date formatted.
		String date = "<cs371date>"; // The tag for the date.
		String server = "<cs371server>"; // the tag for the server identification.
		String serverInfo = "Meagan Waldo's Server"; // The server identification.
		String image = "<img>";

		// if the code is 200 print out what is in the file. Else it is a 404.
		if (code == 200) {

			Scanner scan = new Scanner(neededFile); // Scanner for the file.

			// If the mime type if text/html then print out the text.
			if(type == "text/html") {

				// While the file has a next line scan it in and check to see if there are any tags.
				while (scan.hasNextLine())  {
					String newLine = scan.nextLine(); // The next line.

					// If there are one or both tags then update them to be the correct information.
					if(newLine.contains(date) || newLine.contains(server)) {

						// If the tag for the date exists then update the string with the date.
						if(newLine.contains(date)) {
							newLine = newLine.replaceAll(date, currentDate);
						} // end of if

						// If the tag for the server identification exists then update the string with the server identification.
						if(newLine.contains(server)) {
							newLine = newLine.replaceAll(server, serverInfo);
						} // end of if

						os.write(newLine.getBytes()); // Write out the next line with the updated tags.
					} // end of if
					else if(newLine.contains(image)) {   
						writeImage(os, neededFile); // There is an image so read in the image binary bytes and write it out.
					} // end of else if
					else {
						os.write(new String(newLine).getBytes()); // There were no tags so just write out the next line.
					} // end of else  
				} // end of while
			} // end of if
			else {
				writeImage(os, neededFile); // There is an image binary bytes so read in the image.
			} // end of else
		} // end of if
		else {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 Not Found</h3>\n".getBytes()); // Prints out the 404 error.
			os.write("</body></html>\n".getBytes());
		} // end of else
	}

	/**
	 * There is an image so read in the image binary bytes and write it out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param neededFile 
	 * 			is a file that contains information to be written out to the page.
	 **/
	private void writeImage(OutputStream os, File neededFile) throws FileNotFoundException, IOException {

		// Creates and sets up the buffered input stream.
		BufferedInputStream fileStream; 
		fileStream = new BufferedInputStream(new FileInputStream(neededFile));   

		int read; // Hold the binary bytes of the image.

		read = fileStream.read(); // Reads in the first binary byte.

		// Until there are no more binary bytes write out the binary byte and then read in the next one.
		while(read != -1) {
			os.write(read);
			read = fileStream.read();
		} // end of while

		fileStream.close(); // Closes the buffered input stream.
	} // end of writeImage
} // end class