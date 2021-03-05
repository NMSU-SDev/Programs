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
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WebWorker implements Runnable {

	private Socket socket;

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
		System.err.println("Handling connection...");
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			// Grabs the request URL from the headers
			String requestURL = readHTTPRequest(is);
			// Parses the HTML with the tags.
			byte[] parsedHTML = parseHTML(requestURL);
			// Checks to see if the file exists so that the headers can return the correct
			// status code.
			String resHeader = "200 OK";
			if (!fileExists(requestURL)) {
				resHeader = "404 Not Found";
			}
			// Added a option to plug in the header from the last step.
			writeHTTPHeader(os, "text/html", resHeader);
			// Added a way to take in the parsed html from the previous function
			writeContent(os, parsedHTML);
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
	private String readHTTPRequest(InputStream is) {
		String line;
		String requestURL = "/";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true) {
			try {
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
				// Get's the request URL from the original headers.
				if (line.contains("GET")) {
					String[] args = line.split(" ");
					requestURL = args[1];
				}
			} catch (Exception e) {
				System.err.println("Request error: " + e);
				break;
			}
		}
		return requestURL;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os          is the OutputStream object to write to
	 * @param contentType is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String resHeader) throws Exception {
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		os.write(("HTTP/1.1 " + resHeader + "\n").getBytes());
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
	 * Write the data content to the client network connection. This MUST be done
	 * after the HTTP header has been written out.
	 * 
	 * @param os is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, byte[] parsedHTMl) throws Exception {
		// os.write("<html><head></head><body>\n".getBytes());
		// os.write("<h3>My web server works!</h3>\n".getBytes());
		// os.write("</body></html>\n".getBytes());
		os.write(parsedHTMl);
		return;
	}

	private byte[] parseHTML(String url) {
		// Set's up a return value for this function
		String finalHTML = "";
		// Checks to see if they are requesting the main page.
		if (url.compareTo("/") == 0) {
			finalHTML = "<html><head></head><body>\n<h3>My web server works!</h3>\n</body></html>\n";
			return finalHTML.getBytes();
		}
		url = url.replaceFirst("/", "");

		// Loads the file and reutrns nothing if the file dosen't exsits so it can be
		// proccessed later.
		try {
			BufferedReader reader = new BufferedReader(new FileReader(url));
			while (reader.ready()) {
				String line = reader.readLine();
				// Parses cs371 date and server (Used date code from above as template)
				Date d = new Date();
				DateFormat df = DateFormat.getDateInstance();
				line = line.replace("<cs371date>", df.format(d));
				line = line.replace("<cs371server>", "Camerons' Server");
				finalHTML += line;
			}
			reader.close();
		} catch (Exception e) {
			finalHTML = "<html><head></head><body>\n<h3>404 Not Found</h3>\n</body></html>\n";
		}

		return finalHTML.getBytes();
	}

	// Method checks to see if a file exists so the header function know what to
	// return 200 or 404
	private boolean fileExists(String url) {
		url = url.replaceFirst("/", "");
		File checkFile = new File(url);
		return checkFile.exists();
	}

} // end class
