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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class WebWorker implements Runnable {

	private Socket socket;
	private boolean exist = false;
	private FileInputStream in;
	private String fileName;

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
		System.err.println("Handling connection..."); // prints to command line when run
		try {
			InputStream is = socket.getInputStream(); // used for Input
			OutputStream os = socket.getOutputStream(); // used for output
			readHTTPRequest(is);
			writeHTTPHeader(os, "text/html");
			writeContent(os);
			os.flush();
			socket.close(); // closes socket
			in.close(); // closes File Input Stream
		} catch (Exception e) {
			System.err.println("Output error: " + e); // prints to command line when there is an error
		}
		System.err.println("Done handling connection."); // prints to command line before program is closed
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private void readHTTPRequest(InputStream is) {
		String line = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		int fileCheck = 1;
		// while running this is constantly checking for new http request headers
		while (line.length() == 0) {
			try {
				while (!r.ready())
					Thread.sleep(1);
				// saves the HTTP path into string variable line
				line = r.readLine();
				// checks for a filepath
				if (fileCheck > 0) {
					// seperates the line into tokens
					StringTokenizer token = new StringTokenizer(line);
					token.nextToken();
					// goes through tokens to build the file name that will be accessed saves to
					// fileName
					fileName = token.nextToken();
					fileName = "." + fileName;
					in = null;
					exist = true;
					// open the html file that was built
					try {
						in = new FileInputStream(fileName);
					} catch (Exception fileNotFound) {
						exist = false;
					}
					fileCheck--;
				}
				System.err.println("Request line: (" + line + ")");
			} // end try
			catch (Exception e) {
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
		if (exist) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		} else {
			os.write("HTTP/1.1 404 File Not Found\n".getBytes());
		}
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Anish's very own server\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes());
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done
	 * after the HTTP header has been written out.
	 * 
	 * @param os is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os) throws Exception {
		System.out.println("writeContent");
		if (exist) {
			System.out.println("writeContent: File exists");
			byte[] buffer = new byte[1024];
			int bytes = 0;
			Date date = new Date();
			// file is used for the html file that will be opened in the browser
			String file = "";
			String server = "Anish's Server!";

			while ((bytes = in.read(buffer)) != -1) {
				file = new String(buffer, 0, bytes);
				// prints date when date tag is used
				if (file.contains("<cs371date>")) {
					file = file.replace("<cs371date>", date.toString());
				}
				// prints server name when server tag is used
				if (file.contains("<cs371server>")) {
					file = file.replace("<cs371server>", server);
				}
			}

			os.write(file.getBytes());
		} else { // if file cannot be open/does not exist this html is opened
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404: File not found.</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
		}
	}

} // end class