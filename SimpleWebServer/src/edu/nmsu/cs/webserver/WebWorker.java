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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;

import java.nio.charset.StandardCharsets; // for serving text files



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
	public void run() {
		System.err.println("Handling connection...");
		try {
		  InputStream is = socket.getInputStream();
		  OutputStream os = socket.getOutputStream();
		  String requestedFilePath = readHTTPRequest(is); // read the HTTP request
		  String basePath = new File(".").getCanonicalPath(); // get the current directory
		  String filePath = basePath + File.separator + requestedFilePath; // construct the absolute path
		  boolean fileExists = new File(filePath).exists(); // check if the file exists
		  int statusCode = fileExists ? 200 : 404; // set the status code
	
		  String fileExtension = getFileExtension(requestedFilePath); // get the file extension
		  String contentType = getContentType(fileExtension); // get the content type based on the file extension
	
		  writeHTTPHeader(os, contentType, statusCode); // write the HTTP header
		  writeContent(os, filePath, contentType); // write the content
		  os.flush(); // flush the output stream
		  socket.close(); // close the socket
		} catch (Exception e) {
		  System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	  } // end run()

	  // for file content
	  private String getFileExtension(String filePath) {
		int lastDotIndex = filePath.lastIndexOf('.'); // get the last index of '.'
		// if the file has an extension
		if (lastDotIndex != -1) {
		  return filePath.substring(lastDotIndex + 1); // return the file extension
		}
		return ""; // otherwise, return an empty string
	  } // end getFileExtension()

	  // for serving images based on file extension
	  private String getContentType(String fileExtension) {
		switch (fileExtension.toLowerCase()) {
		    case "gif":
            return "image/gif";
        case "jpeg":
        case "jpg":
            return "image/jpeg";
        case "png":
            return "image/png";
        case "svg":
            return "image/svg+xml";
		case "ico":
			return "image/x-icon"; // for favicon
        default:
            return "text/html";
		}
	  } // end getContentType()


	/**
	 * Read the HTTP request header.
	 **/
	private String readHTTPRequest(InputStream is)
{
    String line = "";
    BufferedReader r = new BufferedReader(new InputStreamReader(is));
    String filePath = "";

    try
    {
        while (!r.ready())
            Thread.sleep(1);

        line = r.readLine();
        System.err.println("Request line: (" + line + ")");

        // parse request line
        String[] tokens = line.split(" ");
        if (tokens.length >= 2) {
            filePath = tokens[1].substring(1); // Remove the leading '/'
        }

        // Read the rest of the request header
        while (true)
        {
            if (!r.ready()) {
                Thread.sleep(1);
            }

            line = r.readLine();
            if (line.length() == 0) {
                break;
            }
        }
    }
    catch (Exception e)
    {
        System.err.println("Request error: " + e);
    }
	// error checking
	//System.out.println("FilePath: " + filePath);
    return filePath;
} // end readHTTPRequest()


	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, int statusCode) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		// os.write("HTTP/1.1 200 OK\n".getBytes());
		String statusLine;
		if (statusCode == 200) {
			statusLine = "HTTP/1.1 200 OK";
		} else if (statusCode == 404) {
			statusLine = "HTTP/1.1 404 Not Found";
		} else {
			statusLine = "HTTP/1.1 500 Internal Server Error";
		}
		os.write((statusLine + "\n").getBytes());

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Kurt's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	} // end writeHTTPHeader()

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private boolean writeContent(OutputStream os, String fp, String contentType) throws Exception {
		try {
			// Create file object
			File file = new File(fp);
			// Create file input stream
			FileInputStream fis = new FileInputStream(file);
			// Create byte array to store file data
			byte[] data = new byte[(int) file.length()];
			// Read file data into byte array
			fis.read(data);
			// Check if the content type is HTML
			if (contentType.equals("text/html")) {
				// Convert the byte array to a string
				String content = new String(data, StandardCharsets.UTF_8);

				// Replace the <cs371date> tag with the current date
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date currentDate = new Date();
				content = content.replace("<cs371date>", dateFormat.format(currentDate));

				// Replace the <cs371server> tag with the server identification string
				String serverName = "Kurt Lyell's very own server";
				content = content.replace("<cs371server>", serverName);

				// Convert the modified content string back to a byte array
				data = content.getBytes(StandardCharsets.UTF_8);
			}
			os.write(data);
			
			// Close file input stream
			fis.close();
			return true;
		} catch (FileNotFoundException e) {
			// Handle file not found error
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
			os.write("Content-Type: text/html\n\n".getBytes());
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 Not Found</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());
			return false;
		} // end try-catch
	} // end writeContent()
	
	

} // end class