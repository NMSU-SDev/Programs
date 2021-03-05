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

/*
 * Code modified by:    Shannon Head
 * Date:                19 February 2021
 * Changes made:        - Implemented ability to locate and serve HTML files, if they can be located
 * 						- If files are requested but not found, 404 Not Found error will be served
 * 						- Separated writeContent() function into 3 functions: writeGenericContent()
 * 						where no filepath is provided, writeFileContent() where the filepath provided
 * 						could be located, and writeError(), where the path was incorrect
 * 						- Implemented the file reader to parse for tags <cs371server> and <cs371date>,
 * 						and replace them with custom server message and date format, respectively
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			String path = readHTTPRequest(is);
			
			// case: no request for file, print generic content
			if ( path == null )
			{
				writeHTTPHeader(os, "text/html", true);
				writeGenericContent(os);
				os.flush();
				socket.close();
			} // end if
			
			// case: there is a file request
			else
			{
				// attempt to open file; if valid, use os to write content of file 
				try
				{
					File file = new File(path);
					BufferedReader r = new BufferedReader( new FileReader(file));
					writeHTTPHeader(os, "text/html", true);
					writeFileContent(os, r);
					r.close();
					is.close();
					os.flush();
					socket.close();
				} // end inner try
				
				// if file request fails, send 404 error
				catch( Exception e )
				{
					System.err.println("Exception occurred: " + e);
					writeHTTPHeader(os, "text/html", false);
					writeError(os);
					is.close();
					os.flush();
					socket.close();
				} // end inner catch
			} // end else

		} // end outer try
		
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		} // end outer catch
		System.err.println("Done handling connection.");
		return;
	} // end function run

	/**
	 * Read the HTTP request header.
	 * 
	 * @return path
	 * 			String representing filepath of requested file, if one exists.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		String line;
		String path = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
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
                
				// if first 3 letters are GET, use String processing to get filepath
				if (line.substring(0, 3).compareTo("GET") == 0)
				{
					// process string
					path = line.substring(line.indexOf(' ') + 1);
					path = path.substring(0, path.indexOf(' '));

					// case: no supplied path
					if (path.length() < 2)
						path = null;
					
            	    // case: supply rest of filepath by getting current working directory
					else
					{
						String directory = System.getProperty("user.dir");
						path = directory.concat(path);
					}
				}
			}
			
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return path;
	} //end readHTTPRequest

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 * @param valid
	 *          is a boolean indicating whether to send 200 OK or 404 message in header
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, boolean valid) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
      
      if (valid)
		   os.write("HTTP/1.1 200 OK\n".getBytes());
      else
         os.write("HTTP/1.0 404\n".getBytes());
         
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Shan's CS 371 server\n".getBytes());
		os.write("Last-Modified: Mon, 22 Feb 2021 18:17:15 MST\n".getBytes());
		os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	} // end function writeHTTPHeader

	
	/**
	 * Write generic content to client network connection; must be done after HTTP header 
	 * has been written.
	 * 
	 * @param os
	 * 			is the OutputStream object to write to
	 **/
	
	private void writeGenericContent(OutputStream os) throws Exception
	{
		os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>My web server works!</h3>\n".getBytes());
		os.write("</body></html>\n".getBytes());
	} // end function writeGenericContent
	
	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
     * @param r
     *          is a BufferedReader object used to parse the file
	 **/
	private void writeFileContent(OutputStream os, BufferedReader r) throws Exception
	{
      String serverTag = "<cs371server>";
      String dateTag = "<cs371date>";
      String line;
      
      while (true)
      {
         try
         {
            line = r.readLine();
            if (line == null) 
               break;
            
            // replaces <cs371server> tag with my server information
            if (line.contains(serverTag))
            {
               String myServer = "Shan's CS371 Server";
               line = line.replaceAll(serverTag, myServer);
            } 
            
            // replaces <cs371date> tag with the current formatted date
            if ( line.contains( dateTag ))
            {
               String form = "EEEEEEEEE, dd MMMMMMMMM yyyy";
               SimpleDateFormat dateForm = new SimpleDateFormat( form );
               Date date = new Date();
               String dateStr = dateForm.format( date );
               line = line.replaceAll( dateTag, dateStr );
            }
            
            os.write( line.getBytes() );
         } 
         
         catch (Exception e)
         {
            System.err.println("Error occurred: " + e);
            return;
         } 
      } 
	} // end function writeFileContent
   
	/**
	 * Write "404 Not Found" message in the case of an invalid file request. Must be done
	 * after writeHTTPHeader() is called with boolean valid == false.
	 * 
	 * @param os
	 * 			is the OutputStream object to write to
	 *  
	 **/
   private void writeError(OutputStream os) throws Exception
   {
      os.write("<html><head></head><body>\n".getBytes());
      os.write("<h3>404 Not Found</h3>\n".getBytes());
      os.write("<h3>The page you were looking for could not be found</h3>\n".getBytes());
      os.write("</body></html>\n".getBytes());
      
   } // end function writeError

} // end class