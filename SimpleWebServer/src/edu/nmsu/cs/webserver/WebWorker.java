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

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class WebWorker implements Runnable
{

	private Socket socket;

	private String fileDir;
	private String mimeType;

	private boolean isFile = true;
	private boolean isDefault = true;

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
			writeHTTPHeader(os, mimeType);
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
	private void readHTTPRequest(InputStream is) {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		String line = "";

		while (true) {
			try {
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();

				// make sure GET request is not default
				if (line.contains("GET /") && !line.contains("GET / ")) {

					// set default to false
					isDefault = false;

					// create new substring for the GET request
					String sub = "." + line.substring(4, line.length()-9);

					// create new file based on substring dir
					File dir = new File(sub);

					mimeType = Files.probeContentType(dir.toPath());

					// check if the directory leads to a file
					if (dir.isFile())
						fileDir = sub;
					else
						isFile = false;
				}

				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
			} catch (Exception e) {
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
		os.write("HTTP/1.1 200 OK\n".getBytes());
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
	 **/
	private void writeContent(OutputStream os) throws Exception
	{
		// create default-gateway if there is no file
		// attempting to be accessed
		if (isDefault) {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>Default gateway</h3>\n".getBytes());
			os.write("</body><html>\n".getBytes());
			os.close();
		}

		else {

			// write 404 error if it is not a file being accessed, and return
			if (!isFile) {
				write404(os);
				return;
			}

			// inits two strings for tags
			String date = "<cs371date>";
			String server = "<cs371server>";

			// instantiates a new date obj with formatting
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));

			// inits a string for a server 'phrase'
			String serverPhrase = "Matt's Server";


			// creates a substantial new string array
			String[] read = new String[1000];

			// create new file with directed file root
			File file = new File(fileDir);

			// create type of content string
			String type = fileDir.substring(fileDir.length()-4);

			// creates byte stream to read in images
			if (!type.contains("html")) {
				int bytes;
				InputStream byteStream = new FileInputStream(fileDir);

				while ((bytes = byteStream.read()) != -1) {
					os.write(bytes);
				}
				os.close();
			}
			else {
				// instantiate new buffered reader (file reader to read the HTML file)
				BufferedReader r = new BufferedReader(new FileReader(file));

				// for loop until the end of the string array
				for (int a = 0; a < read.length; a++) {

					// reads the line, places it into string array
					read[a] = r.readLine();

					// checks for tags, instantiates new strings and replaces
					// all instances of those tags with corrected date/phrase
					if (read[a].contains(date)) {
						String tempSub1 = read[a];
						String tempSub2 = read[a];
						tempSub1 = read[a].replaceAll(date, df.format(d));
						if (read[a].contains(server))
							tempSub2 = tempSub1.replaceAll(server, serverPhrase);
						os.write(tempSub2.getBytes());
					}
					// if there were no tags, it will just write out what
					// is insided the Output Stream.
					else
						os.write(read[a].getBytes());
				}

				os.close();
			}

		}
	}

	private void write404(OutputStream os) throws Exception {
		os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>404 Not Found</h3>\n".getBytes());
		os.write("</body><html>\n".getBytes());
		os.close();
	}

} // end class
