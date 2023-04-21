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
import java.net.*;
import java.util.*;
import java.io.FileReader;
import java.io.File;
import java.util.Scanner;
import java.io.BufferedReader;
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
	public String pFile = "";
	
	public String pError = "";
	public String result = "";
	public String date = "02/22/2023";
	public String servID = "Jason's server";
	public String tag1 = "<cs371date>";
	public String tag2 = "<cs371server>";
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
			writeHTTPHeader(os, "text/html");
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
	private void readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				if(line.startsWith("GET")) {
					pFile = line.substring(4, line.lastIndexOf(" "));
				}
				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
			}
			
			
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		try {
			String val;
			String currentWorkingDir = System.getProperty("user.dir");
			File f = new File(currentWorkingDir + pFile);
			StringBuilder html = new StringBuilder();
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			while ((val = br.readLine()) != null) {
				html.append(val);
			}
		
			result = html.toString();
		} 
		catch(Exception ex){
			pError = "404";
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
		
		if(pError == "404") 
			os.write("HTTP/1.1 404 NOT FOUND".getBytes());
		else {	
			os.write("HTTP/1.1 200 OK\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
			os.write("Jason's server\n".getBytes());
			os.write("Connection: close\n".getBytes());
			os.write("Content-Type: ".getBytes());
			os.write(contentType.getBytes());
			os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		} // end else
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
		String result1 = result.replace(tag1,date);
		String result2 = result1.replace(tag2,servID);
		os.write(result2.getBytes());
	
	}

} // end class
