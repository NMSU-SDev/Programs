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
		String image = "text/html";
		
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			String path = readHTTPRequest(is);
			if(path.contains(".png")) {
				image = "image/png";
			}
			else if(path.contains(".jpg")) {
				image = "image/jpg";
			}
			else if(path.contains(".gif")) {
				image = "image/gif";
			}
			writeHTTPHeader(os, image, path);
			writeContent(os, image, path);
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
		String line = " ";
		String path = "";
		String [] split;
		try
			{
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				split = line.split(" ");
				path = split[1];
				System.err.println("Request line: (" + line + ")");
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				return "404";
			}
		
		return path;
	}
	

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String path) throws Exception
	{
		int flag = 0;

		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
			File file = new File(path);
		} catch (Exception e) {
			flag = 404;
		}

		if (flag == 0)
			os.write("HTTP/1.1 200 OK\n".getBytes());
		else 
			os.write("HTTP/1.1 404 Not Found\n".getBytes());

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Jared's very own server\n".getBytes());
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
	private void writeContent(OutputStream os, String image, String path) throws Exception
	{	
		String line = "";
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("MST"));

		if (path.contains("html")){
		try {

		File file = new File(path);
		FileInputStream r = new FileInputStream(file);
		BufferedReader b = new BufferedReader(new InputStreamReader(r));

		while((line = b.readLine()) != null) {

		if(line.contains("<cs371date>")){
			line = line.replace("<cs371date>", d.toString());
		}
		if (line.contains("<cs371server>")) {
			line = line.replace("<cs371server>", "Jared's Server");
		}
		
			os.write(line.getBytes());
		
		}
	} catch (Exception e) {
		os.write("404 Not Found".getBytes());
	}
	}
	else if (image.contains("image")) {
		int place;
		File file = new File(path.substring(1));
		FileInputStream ff = new FileInputStream(path.substring(1));
		int size = (int)file.length();
		byte x[] = new byte[size];
		while((place = ff.read(x)) > 0)
			os.write(x,0,place);
	} 
 }

} // end class
