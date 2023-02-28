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
import java.util.Date;
import java.util.TimeZone;
import java.io.*;
import java.lang.Runnable;

public class WebWorker implements Runnable
{

	private Socket socket;
	private String store;

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

			store = readHTTPRequest(is);
			
			writeHTTPHeader(os,"text/html", store);
			writeContent(os,"text/html", store);
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
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		String store = "";

		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				// store = store + line + "\n";
				String[] vault = line.split(" ");

				if(line.contains("GET ")){
					store = line.substring(4);
					for(int i = 0; i < store.length(); i++){
						if(store.charAt(i) == ' '){
							store = store.substring(0, i);
						}
					}
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
		return store;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String store) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT-7"));

		String cp = '.' + store;
		File file = new File(cp);

		try{
			FileReader readFile = new FileReader(file);
			BufferedReader read = new BufferedReader(readFile);
		}
		catch(FileNotFoundException e){
			System.out.println("File not found: " + store);
			os.write("HTTP/1.1 404 Error: Not Found\n".getBytes());
		}

		os.write("HTTP/1.1 200 OK\n".getBytes());
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
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
	private void writeContent(OutputStream os, String contentType, String store) throws Exception
	{
		Date d = new Date();
		DateFormat dformat = DateFormat.getDateTimeInstance();
		dformat.setTimeZone(TimeZone.getTimeZone("GMT-7"));

		String fcont = "";
		String copy = "." + store.substring(0, store.length());
		String date = dformat.format(d);
		File f1 = new File(copy);

		try{
			FileReader fRead = new FileReader(f1);
			BufferedReader fBuff = new BufferedReader(fRead);

			while((fcont = fBuff.readLine()) != null) {
				os.write(fcont.getBytes());
				os.write("\n".getBytes());

            if (fcont.contains("<cs371date>")) {
               os.write(date.getBytes());
               os.write("<br>".getBytes());
            } // of if

            if (fcont.contains("<cs371server>")){
               os.write("Mauricio's server\n".getBytes());
               os.write("<br>".getBytes());
            }// of if

         } // of while

		}
		catch(FileNotFoundException e) {
			System.err.println("File not found: " + store);
			os.write("<h1>Error: 404 Not found<h1>\n".getBytes());
		} // end try-catch

	}

} // end class
