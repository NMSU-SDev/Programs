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
import java.io.File; // import file for file reading
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class WebWorker implements Runnable {

	private Socket socket;

	private File page; // web page to be read

	private String path; // path to project

	private String status;

	private boolean pageExists;

	private boolean debug = false; // for testing

	private static final String SERVER_ID = "Josh G's CS371 Web Server";

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s) {
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and
	 * then returns, which destroys the thread. This method assumes that whoever
	 * created the worker created it with a valid open socket object.
	 **/
	public void run() {
		System.err.println("\nHandling connection...");
		try {
			// path to project \ replaced with /
			path = System.getProperty("user.dir").replace("\\", "/");
			status = ""; // status of web page {200, 404, etc.}
			if (debug) // display path
				System.out.println("Project path is: " + path + ".");
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			readHTTPRequest(is);
			writeHTTPHeader(os, "text/html");
			writeContent(os);
			os.flush();
			socket.close();
		} catch (Exception e) {
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private void readHTTPRequest(InputStream is) {
		String line; // variable to hold value from BufferedReader
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true) {
			try {
				while (!r.ready()) // while BufferedReader is not ready
					Thread.sleep(1); // sleep for one (millisecond?)
				line = r.readLine(); // assing the read value from r to line
				String splitLine[] = line.split(" "); // split line seperated by commas
				// if GET
				if (splitLine.length > 1 && splitLine[0].equals("GET")) {
					if (debug) // display line with GET
						System.err.println("Get Found: " + line);
					int getItemLength = splitLine[1].length(); // length of item following GET
					// if filetype of page is html
					if (splitLine[1].substring(getItemLength - 5).equals(".html")) {
						if (debug) { //
							System.err.println("Web Page Found: " + splitLine[1]);
							System.err.println("Web Page Path: " + path.concat(splitLine[1]));
						} // end if
						try {
							// create file with path + html page as path
							page = new File(path.concat(splitLine[1]));
							if (!page.exists()) { // if the page does not exist
								status = "HTTP/1.1 404 Not Found\n"; // set status to 404
								pageExists = false; // page does not exist
							} else { // page does exist
								status = "HTTP/1.1 200 OK\n"; // status is 200
								pageExists = true;
							} // end if-else
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
					// get returned an icon
					if (splitLine[1].substring(getItemLength - 4).equals(".ico")) {
						if (debug)
							System.err.println("Icon Found: " + splitLine[1]);
					}
				}
				if (debug)
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
	 * @param os          is the OutputStream object to write to
	 * @param contentType is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception {
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (status.length() == 0) // if status has not been set, create page
			status = "HTTP/1.1 404 Not Found\n";
		os.write(status.getBytes());
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Josh G's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
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
	 * @param os is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os) throws Exception {
		try {
			if (!pageExists) { // if page was not found page is error404 page
				page = new File(path.concat("/res/acc/error404.html"));
			}
			// buffered reader reades Files
			BufferedReader reader = new BufferedReader(new FileReader(page));

			String line; // String to store each line of file

			// read each line of file
			while ((line = reader.readLine()) != null) {
				if (line.contains("cs371date")) { // if line has a date tag
					if (debug)
						System.out.println("Date tag found: " + line);
					DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // format date output
					LocalDateTime currentTime = LocalDateTime.now(); // set currentTime to formatted date
					// replace all occurences of date tag with actual date-time
					line = line.replaceAll("<cs371date>", currentTime.toString());
				} // end if
				if (line.contains("<cs371server>")) { // if server tag is found
					if (debug)
						System.out.println("Server tag found: " + line);
					// replace all server tags with server id
					line = line.replaceAll("<cs371server>", SERVER_ID);
				} // end if

				if (debug)
					System.out.println("Web Page Line: " + line);

				os.write(line.getBytes()); // print line
			} // end while

			reader.close(); // close buffered reader

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

	}
} // end class
