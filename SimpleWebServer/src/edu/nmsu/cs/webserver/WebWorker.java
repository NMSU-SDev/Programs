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
 * Changes made on February 16, 2022
 * - Modified readHTTPRequest to return the file referred to by the GET request.
 * - Modified  writeHTTPHeader to set the response status to either 200 OK or 404 Not Found. 
 * 	 depending on if GET request refers to an existing filename. 
 * - Modified writeContent to write from the HTML file.
 * 
 * Changes made on March 2, 2022
 * - Modified run to find the content type of a requested file.
 * - Modified writeHTTPHeader to set contentType to the MIME type referred to by the GET request.
 * - Modified writeContent to open and read the images in binary mode, rather than line-by-line.
 * 
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

// added for reading files
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader; 

public class WebWorker implements Runnable {

	private Socket socket;
	private String serverID = "Hannah's very own server"; // server ID

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
		
		    
			InputStream is = socket.getInputStream(); // from web browser (client) to us (server). Get from socket object
			OutputStream os = socket.getOutputStream(); // what we (server) send back to web browser
			File f = readHTTPRequest(is); // file referred to by the GET request
			
			//Set contentType to correct file type
			
			String fName = f.getPath();
			String contentType = "";
			
			if(fName.contains(".gif")){
				contentType = "image/gif";
			}
			else if (fName.contains(".jpeg")) {
				contentType = "image/jpeg";	
			}
			else if (fName.contains("favicon.ico")) {
				contentType = "image/x-icon";	
			}
			else if (fName.contains(".png")) {
				contentType = "image/png";	
			}
			else if (fName.contains(".html")) {
				contentType = "text/html";	
			}
			else {
				System.err.println("File type not recognized");
			}
			
			
			writeHTTPHeader(os, contentType, f.exists());  // writes HTTP header
			writeContent(os, f, contentType); // writes HTTP contents
			
			os.flush(); // clear the output stream
			socket.close(); // shut down socket
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return; // thread finished
	} // end of run

	
	
	
	/**
	 * Read the HTTP request header.
	 * 
	 * @param is
	 * 		the HTTP request header to read
	 * @precondition
	 * 		'is' is a non-null, valid HTTP request header
	 * @postcondition
	 * 		reads and prints the HTTP request to the console. Returns the file referred to by the GET request
	 **/
	
	private File readHTTPRequest(InputStream is) 
	{
		
		String line; // temporally holds a line of input
		BufferedReader r = new BufferedReader(new InputStreamReader(is)); //to read input data
		File f = null; // to hold file referred to by GET request
		
		while (true)
		{
			try
			{
				// don't do anything until current data comes in
				while (!r.ready()) 
					Thread.sleep(1); 
				
				line = r.readLine(); // reads one line of input
				System.err.println("Request line: (" + line + ")"); // prints requested data to error console
				
				// Find the GET line and parse requested file path
				if(line.contains("GET")){ 
					String fpath = line.substring(line.indexOf("GET /")+5);
					fpath = fpath.substring(0,fpath.indexOf(" "));
					//System.out.println(fpath);
					f = new File(fpath); // file referred to by the GET request. May not exist
				}
				
				// header terminated by blank line 
				if (line.length() == 0) 
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		} //end of while
		
		return f; // return file if it exists
	} //end of read httpRequest
	
	

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html") img/
	 * @param fileExists
	 * 			specifies whether the GET request refers to an existing filename 
	 * @precondition
	 * 			os is not null
	 * 			contentType is image/gif, image/jpeg, text/html or image/png
	 * @postcondition
	 * 			writes HTTP header lines to the client network connection
	 * 		
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, boolean fileExists) throws Exception 
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		// Write 200 OK if file exists. Otherwise, write 404 Not Found
		if (fileExists == false){
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}else{
			os.write("HTTP/1.1 200 OK\n".getBytes()); 
		}
		
		// Write current date
		os.write("Date: ".getBytes()); 
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		
		// Write serverID
		os.write(("Server: "+serverID+"\n").getBytes());
		
		// Network connection closes after the current transaction finishes
		os.write("Connection: close\n".getBytes());
		
		// content type
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
		
	} //end of writeHTTPHeader

	
	
	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param f
	 * 			is the file to write the HTML code from
	 * @param contentType
	 * 			 is the string MIME content type (e.g. "text/html") img/
	 * @precondition 
	 * 			os is not null
	 * 			f is not null
	 * 			writeHTTPHeader() ran before the execution of this method to to write out the HTTP header
	 * 			contentType is image/gif, image/jpeg, text/html or image/png
	 * @postcondition
	 * 		If file f exists, write the content of the file to the client network connection.
	 * 		Otherwise, write 404 File Not Found
	 * @note 
	 * 		the tag <cs371date> is replaced by a formatted date string for the current date,
	 *  	and the tag <cs371server> is replaced with the server's identification string
	 * 				
	 **/
	private void writeContent(OutputStream os, File f, String contentType) throws Exception
	{
		// throw error if file doesn't exist
		if(!f.exists()) { 
			os.write(("<h1>404 File Not Found</h1>").getBytes());
			return;
		}
       	
		// read html files line by line
		if(contentType.equals("text/html")) { 
			FileReader fr = new FileReader(f);
	       	try (BufferedReader br = new BufferedReader(fr)) {
				
	       		String line; 
	      		while((line = br.readLine()) != null)  {
	            	//System.out.println(line);
	          
	            	// Replace <cs371date> with current time
	            	if (line.contains("<cs371date>")){
	            		Date d = new Date();
	            		DateFormat df = DateFormat.getDateTimeInstance();
	            		df.setTimeZone(TimeZone.getTimeZone("GMT"));
	            		line = line.replace("<cs371date>",  df.format(d));
	            	}
	            	
	            	// Replace <cs371server> with serverID
	            	if (line.contains("<cs371server>")){
	            		line = line.replace("<cs371server>",  serverID);
	            	}
	            	
	            	//write one line of the file to output stream
	            	os.write((line).getBytes());
	           	
	      		} //end of while 
	      		
	       	}// end of try	
	       	catch (Exception e) {
		    	System.err.println("Error reading file: " + e);
	       	}
			
		}
		
		// read images in binary mode
		else { 
			try (InputStream is = new FileInputStream(f); ) {
			            
				int myByte = -1;
				//process image byte by byte
				while ((myByte = is.read()) != -1) {
					os.write(myByte);
			    }
			 } catch (Exception e) {
			    	System.err.println("Error reading file: " + e);
			 }
		}
		
	} // end of writeContent
	
	
	
} // end class
