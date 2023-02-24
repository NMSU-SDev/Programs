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

import javax.management.ConstructorParameters;

import java.io.File;
import java.io.FileReader;

public class WebWorker implements Runnable
{

	private Socket socket;
	public boolean existingFile;
	public String fileDirectory = "";
	public String serverName = "Alen's Server";
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
	} //end run

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

				// When the request line is at GET, it typically has the file location/directory
				// Serving .html files
				if (line.contains("GET")){
					//get the file seperated from the GET and HTTP portion of the request line
					String lineManip = line.substring((line.indexOf("/") + 1), (line.indexOf("HTTP") - 1)); 
					fileDirectory = lineManip;
					File inputFile = new File(lineManip);

					if(inputFile.exists()){
						existingFile = true;
					}else{
						existingFile = false;
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
		return;
	} //end readHTTPRequest

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
		
		
		if(existingFile){
			//200 OK if the file exists404 Error if the file doesn't exist
			os.write("HTTP/1.1 200 OK\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
		}else{
			//404 Error if the file doesn't exist
			//throw new HttpRetryException("File Not Found", 404);
			os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
		}

		os.write("Server: Alen's very own server\n".getBytes());
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
	private void writeContent(OutputStream os) throws Exception
	{
		
		if(existingFile && fileDirectory.contains("html")){
			//os.write("<html><head></head><body>\n".getBytes());
			//os.write("<h3>My web server works!</h3>\n".getBytes());
			//os.write("</body></html>\n".getBytes());

			/* The following code is taken from https://stackoverflow.com/questions/12035316/reading-entire-html-file-to-string 
			 * from the user Jean Logeart (lifesaver, thanks Jean)
			 * the file adjustments is done by me, Alen 
			*/
			
			// reading the inputted html file
			StringBuilder contentBuilder = new StringBuilder();
			try {
				BufferedReader input = new BufferedReader(new FileReader(fileDirectory));
				String htmlFile;
				while ((htmlFile = input.readLine()) != null) {
					contentBuilder.append(htmlFile);
					//to make sure the html code is not on just one line
					//can be seen in browser > inspect > network > [filename] > response
					contentBuilder.append("\n"); 
				}
				input.close();
			}catch(Exception e){ /*do nothing*/ }

			String write = contentBuilder.toString(); // making the html file as a string to be written to os

			//replacing the needed information with the correct information
			if (write.contains("<cs371server>")){
				write = write.replace("<cs371server>", serverName);
			}
			if (write.contains("<cs371date>")){
				SimpleDateFormat date = new SimpleDateFormat("MM-dd-YYYY");
				write = write.replace("<cs371date>", date.format(new Date()));
			}
			os.write(write.getBytes());

		}else{
			//throw the 404 error
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h1><b>404 Error! File Not Found</b></h1>\n".getBytes());
			os.write("<h3>File Directory: </h3>\n".getBytes());

			//showing what the file directory is if the file doesnt exist
			if (fileDirectory.equals("")){
				os.write("<h3>(null) : check if the URL contains a file directory and not just localhost:[PORT]</h3>\n".getBytes());
				os.write("</body></html>\n".getBytes());
			}else if(fileDirectory.equals("hello")){
				//if the file is similar to hello.html, show this recommendation
				os.write(fileDirectory.getBytes());
				os.write("Did you mean <a href=\"hello.html\">localhost:8080/hello.html</a> ?".getBytes());
			}else{
				os.write(fileDirectory.getBytes());
				os.write("</body></html>\n".getBytes());
			}
		}
	}

} // end class
