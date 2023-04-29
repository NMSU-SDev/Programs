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

 // cd C:\Users\brock\OneDrive\Desktop\SWD371\Programs\SimpleWebServer\src


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.io.File;
import java.io.FileInputStream;

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
			File in_file = readHTTPRequest(is);
			String path = in_file.getPath();
			String ext_type = "";

			// Get extension of file
			if(path.contains(".jpeg")){
				ext_type = "image/png";	
			}
			else if (path.contains(".png")) {
				ext_type = "image/jpeg";	
			}
			else if (path.contains(".gif")) {
				ext_type = "image/gif";
			}
			else if (path.contains(".html")) {
				ext_type = "text/html";	
			}
			else if (path.contains("favicon.ico")) {
				ext_type = "image/x-icon";	
			}
			else{
				System.err.println("Failed to get input file type. Input file type may not be supported.\n");
			}

			writeHTTPHeader(os, in_file, ext_type); 
			writeContent(os, in_file, ext_type);
			os.flush();
			socket.close();

		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private File readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		File file=null;
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");

				if (line.length() == 0)
					break;

				// Split each reaed line by whitespace
				String[] splitStr = line.trim().split("\\s+");
				int stringSize = splitStr.length;

				// Iterate over elements of string
				for (int i = 0; i < stringSize; i++){

					// If we have an 'GET' - then we know the next element is path
					if (splitStr[i].equals("GET")) {

						String path = splitStr[i + 1];

						// Get path up to SimpleWebServer, append to full path
						String prefix_path = System.getProperty("user.dir");
						String final_path = prefix_path + path;


						// Grab file from path
						try {

							file = new File(final_path);
						} catch(Exception e) {
							e.printStackTrace();
						}


					} // if
				} // for 
			} //try 

			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return file;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param ext_type
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, File file, String ext_type) throws Exception
	{

		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate = df.format(d);

		// If result is 404, give proper header
		if (file.exists()){
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}
		else {
			os.write("HTTP/1.1 404 NOT FOUND".getBytes());
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Brock's web server\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(ext_type.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		System.err.println("Header written.\n");
		return;

	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, File file, String type) throws Exception
	{
		// If input does not exist, 404
		if (!file.exists()){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 - file not found.</h3>\n".getBytes());
		    os.write("</body></html>\n".getBytes());

		}

		// If content type is html, write with bufferedreader
		else if (type.equals("text/html")){
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;

			while((line=br.readLine()) != null) {

				if(line.contains("<cs371date>")) {
					Date d = new Date();
					DateFormat df = DateFormat.getDateTimeInstance();
					df.setTimeZone(TimeZone.getTimeZone("GMT"));
					line = line.replaceAll("<cs371date>",  df.format(d));
				}

				if (line.contains("<cs371server>")){
					line = line.replaceAll("<cs371server>",  "Brock's Web Server");
				}

				os.write(line.getBytes());
			}
			br.close();

		}

		// Else, we have an image. We need to write byte by byte instead of line by line
		else {
			FileInputStream fip = new FileInputStream(file);

			int bytesRead;
			while ((bytesRead=fip.read())!=-1) {
				os.write(bytesRead);
			}
			fip.close();
		}

	} // writeContent

} // end class
