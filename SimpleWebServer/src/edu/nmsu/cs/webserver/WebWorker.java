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

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.Files;

public class WebWorker implements Runnable
{

	private Socket socket;
	private String address; //handles file address
	private String err404 = "<html><head></head><body><h1>404 Page Not Found!</h1><p>Sorry, pardner. You should've taken that left turn at Albuquerque.</p></body></html>";
	private String defDis = "<html><head></head><body><h3>Default Page</h3><p><cs371date></p><p><cs371server></p></body></html>";
	private String conType = "text/html";
	
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
			writeHTTPHeader(os, conType);
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
				System.err.println("Request line: (" + line + ")");
				if(line.contains("GET") && line.contains("/res/acc")) address = line;
				if (line.length() == 0)
					break;
			}//end try
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}//end catch
		}//end while
		if(address != null) processAddress(address);
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
		os.write("HTTP/1.1 200 OK\n".getBytes());
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
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os) throws Exception
	{
		
		if(conType.equals("text/html")) {
		   if(address != null) 
		      os.write(pageFileText(address).getBytes());

		   else 
			   os.write(checkTags(defDis).getBytes());
	
		}//end if
		
		else {
			try {
			   
			   InputStream imgS = new FileInputStream(address);
			   int getByte;
			   while((getByte = imgS.read()) != -1 )
			      os.write(getByte);
			   imgS.close();
			   
			}//end try
			catch(Exception e) {
				os.write(err404.getBytes());
			}//end catch
		}//end else
		
	    //revert address for future queries
		address = null;
		
	}//end writeContent

	// ME
	private void processAddress(String adr) {
		
		char directOp;
		
		address = System.getProperty("user.dir") + "/www" + adr.substring(4,adr.length()-9);
		
		directOp = address.charAt(address.indexOf("SimpleWebServer") - 1);
		if(directOp == '\\'); //for Windows directories
			address = address.replace('/', directOp);
			
	   try {
		File target = new File(address);
		conType = Files.probeContentType(target.toPath());
	   }
	   catch(Exception e) {}
		
	}//end processAddress
	
	private String pageFileText(String fileName){
		
		Scanner fScan;
		String fileText = "";
		try {
			fScan = new Scanner(new File(fileName));
			while(fScan.hasNext()) {
				fileText += fScan.nextLine();
			}//end while
			
			
			fScan.close();
			return checkTags(fileText);
		}//end try
		catch(FileNotFoundException e) {
			//404
			return checkTags(err404);
		}//end catch
		
	}//end pageFileText
	
	private String checkTags(String page) {
		
		String tagA = "<cs371date>";
		Date currDate = new Date();
		SimpleDateFormat form = new SimpleDateFormat("MM-dd-yyyy");
		String stringA = form.format(currDate);
		String tagB = "<cs371server>";
		String stringB = "Darkwater Town Square Server";
			
		page = page.replace(tagA, stringA);
		page = page.replace(tagB, stringB);
		
		return page;
		
	}//end checkTags
	
} // end class
