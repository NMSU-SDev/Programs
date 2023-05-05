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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.io.File;
import java.io.FileReader;

//for serving HTML files
// import java.net.*;
// import java.io.*;
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
	 * then returns, which
	 * destroys the thread. This method assumes that whoever created the worker
	 * created it with a
	 * valid open socket object.
	 **/
	public void run() {

		System.err.println("Handling connection...");
		try {
			// website URL empty string
			String url_address = "";
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			url_address = readHTTPRequest(is); // get this request info into 57,58
			// System.out.println(url_address);

			// i think this serves(sends) the url address to the html file
			writeHTTPHeader(os, "text/html", url_address);
			writeContent(os, url_address);
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
	 * search for the request header and return if found or not to console
	 * changed return type in order to read input url address
	 **/
	private String readHTTPRequest(InputStream is) {

		// stores url
		String line = "";
		// read the file and parse it
		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		while (true) {
			try {
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");

				// // search for request URL header (file_address)
				// if ((line.contains("GET")) && (line.length() > 3) && (line.length() != 14)) {
				// file_address = line.substring(4, line.length() - 9);
				// System.err.println("File Found: (" + file_address + ")");

				// }

				if (line.length() == 0)
					break;
				else
					return line;

			} catch (Exception e) {
				System.err.println("Request error: " + e);
				break;
			}
		}

		return line;

	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * need to add a 404 error if file cannot be located/does not exist
	 * need the actual served file
	 * 
	 * @param os
	 *                    is the OutputStream object to write to
	 * @param contentType
	 *                    is the string MIME content type (e.g. "text/html")
	 * 
	 * @param urlAddress
	 *                    is the file name of URL address
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String urlAddress) throws Exception {

		// storing just the file name of url address
		urlAddress = urlAddress.substring(5, urlAddress.length() - 9);
		// System.out.println("******check: " + urlAddress);

		// access file
		File file = new File(urlAddress);
		
		// checking using File class methods
		if (file.exists() || file.isDirectory()) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		} else {
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}

		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		os.write("HTTP/1.1 200 OK\n".getBytes());// return 200 ok and add 404 not
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Chey's very own server\n".getBytes());
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
	 * after the HTTP
	 * header has been written out.
	 * TO DO:
	 * get date when we see <cs371date> tag is seen
	 * output name when <cs371server> tag is seen
	 * write connents to the os command
	 * check if file is there return 404 to server and client
	 * 
	 * @param os
	 *                 is the OutputStream object to write to
	 * 
	 * @param readFile
	 *                 is the string that holds the address of the file path
	 **/
	private void writeContent(OutputStream os, String readFile) throws Exception {
		// search for the file and read the file to write to the file
		// write the contents to the os command
		// readFile into object buffered reader fileReader
		readFile = readFile.substring(5, readFile.length() - 9);
		// access file
		File file = new File(readFile);

		// empty string that will store when bufferedReader reads lines
		String line = "";

		// stores which content type the file should be
		String type = "text/html";

		// dynamic server changing tags
		if (!file.exists() || file.isDirectory()) {
			System.err.println("File not found: " + readFile);
			os.write("<h1>Error: 404 Not found<h1>\n".getBytes());
		} else {
			// check that the file is .html
			if (type == "text/html") {

				// reads the text within file
				FileReader fr = new FileReader(file);
				// parses and does everything magical for me that I dont have to worry about
				BufferedReader br = new BufferedReader(fr);

				// create a date d object
				Date d = new Date();
				// change format of the date to year/month/day
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				// store the df into string with proper format
				String date = df.format(d);
				// traverse through hello.html
				while ((line = br.readLine()) != null) {
					// when br finds <cs371date> it will be replaced with the the string date
					line = line.replace("<cs371date>", date);
					// when br finds <cs371server> it will be replaced by that sentence
					line = line.replace("<cs371server>", "Chey's most coolest web server");
					// write to hello.html
					os.write(line.getBytes());

				} // end while
					// close the bufferedReader to stop reading the file
				br.close();
			} // end if

		} // end else

	}// end writeContent method

} // end class

// // this will write into the html/console msg if file is found
// if (file.exists() || file.isDirectory()) {
// // writing into local host using html
// os.write("<html><head></head><body>\n".getBytes());
// os.write("<h3>My web server works!</h3>\n".getBytes());
// os.write("</body></html>\n".getBytes());
// System.err.println("File found: " + readFile);

// } else {
// System.err.println("File not found: " + readFile);
// os.write("<h1>Error: 404 Not found<h1>\n".getBytes());
// }
//