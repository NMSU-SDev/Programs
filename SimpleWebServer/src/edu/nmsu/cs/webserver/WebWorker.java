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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.StringTokenizer;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
			readHTTPRequest(is, os);	
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
	private void readHTTPRequest(InputStream is, OutputStream os) {

		String line;
		String file;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		try {
			while (!r.ready())
				Thread.sleep(1);

			line = r.readLine();
			StringTokenizer stk = new StringTokenizer(line);

			if (stk.hasMoreElements() && stk.nextToken().equalsIgnoreCase("GET")
					&& stk.hasMoreElements())
				file = stk.nextToken();
			else 
				throw new IOException("400");

			if (file.startsWith("/"))
				file = file.substring(1);
			if (file.equals(""))
				file = "test.html";

			FileInputStream fis = new FileInputStream(file);

			String mimetype = "text/html";
			if (file.endsWith(".jpg") || file.endsWith(".jpeg"))
				mimetype = "image/jpeg";
			else if (file.endsWith(".png"))
				mimetype = "image/png";
			else if (file.endsWith(".gif"))
				mimetype = "image/gif";
			else if (file.endsWith(".ico"))
				mimetype = "image/x-icon";

			writeHTTPHeader(os, mimetype, "200");
			writeContent(os, file);

		} catch (FileNotFoundException e0) {
			try { 
				writeHTTPHeader(os, "text/html", "404");
			} catch (Exception e1) {
				System.err.println("Error: " + e1);
			}
		} catch (Exception e2) {
			System.err.println("error: " + e2);
		}

		while (true) {
			try {
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
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
	private void writeHTTPHeader (OutputStream os, String mimeType, String status) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));

		os.write("HTTP/1.1 ".getBytes());
		os.write(status.getBytes());
		os.write("\nDate: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Julio's very own server\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(mimeType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		
		try {
			if(status.equalsIgnoreCase("404"))
				writeContent(os, "404page.html");
		} catch (Exception e) {
			System.err.println("Error writing to OutputStream");
		}
		
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, String file) throws Exception {
		File toSend = new File(file);
		byte[] bytearr = new byte [(int)toSend.length()];
		FileInputStream fis = new FileInputStream(toSend);
		BufferedInputStream bis = new BufferedInputStream(fis);

		String line = "";
		if(file.endsWith(".html")) {
			String sdf = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("<cs371date>", sdf);
				line = line.replaceAll("<cs371server>", "Julio's Server");
				os.write(line.getBytes());
			}
			return;
		}

		bis.read(bytearr,0,bytearr.length);
		System.err.println("Send: " + toSend + "(" + bytearr.length + " bytes)");
		os.write(bytearr,0,bytearr.length);
		os.flush();
		System.err.println("Send: Done.");
	}
	

} // end class
