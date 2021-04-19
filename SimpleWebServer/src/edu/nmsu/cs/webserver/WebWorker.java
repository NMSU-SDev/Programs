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
import java.util.Date;
import java.util.TimeZone;
import java.util.Scanner;


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
			
			//gets the directory to the file from the HTTP request puts it into fileName
			String fileName = readHTTPRequest(is);
			//instantiating a null File object 
			File file = null;
			
			//making sure the file name isnt null and that it isnt the main page of the web server which is directory "/"
			if( ( fileName != null ) && ( !fileName.equals( "/" ) ) )
			{
				//if its not null and not the main page, then set the file object to have fileName minus the "/" in the front"
				file = new File( fileName.substring( 1, fileName.length() ) );
			}
			
			//getting the content type from the file name
			String contentType = fileName.substring( fileName.indexOf( '.' ) + 1 );
			
			//going through the different possible content types and making it the appropriate MIME
			if( !fileName.equals( "/" ) && !file.exists() )
			{
				contentType = "text/html";
			} //end if
			else if( contentType.equals( "gif" ) )
			{
				contentType = "image/gif";
			} //end else if
			else if( contentType.equals( "jpeg" ) || contentType.equals( "jpg" ) )
			{
				contentType = "image/jpeg";
			} //end else if
			else if( contentType.equals( "png" ) )
			{
				contentType = "image/png";
			} //end else if
			else
			{
				contentType = "text/html";
			} //end else
			
			writeHTTPHeader(os, contentType, file, fileName);
			writeContent( os, file, fileName, contentType );
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
		String fileName = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				
				//parsing the HTTP request and looking for the GET line which contains the file directory
				if( line.startsWith( "GET" ) )
				{
					//Only take the file directory, not the other parts of the request
					fileName = line.substring( 4, line.length() - 9 );
				}
				if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		//returns the parsed out file name back to run
		return fileName;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, File file, String fileName) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("MST"));                                //changed this to MST
		
		//if the file is not the home page and the file isnt a directory known
		if( ( !fileName.equals( "/" ) ) && ( !file.exists() ) )
		{
			//then error 404
			os.write( "HTTP/1.1 404 Not Found\n".getBytes() );
		}
		else
		{
			//otherwise it is a known file directory and 200 OK
			os.write( "HTTP/1.1 200 OK\n".getBytes() );
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Thomas' very own server\n".getBytes());                    //changed this to my name
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
	private void writeContent(OutputStream os, File file, String fileName, String contentType ) throws Exception
	{
		//basically checks to see if it is a html file or an image
		if( contentType.equals( "text/html" ) )
		{
			//instantiates a date object and sets the timezone and formate
			//borrowed from writeHTTPHeader
			Date d = new Date();
			DateFormat df = DateFormat.getDateInstance();
			df.setTimeZone( TimeZone.getTimeZone( "MST" ) );
			
			String date = df.format( d );
			
			//if the file is the home page
			if( fileName.equals( "/" ) )
			{
				//then output home page info
				os.write("<html><head></head><body>\n".getBytes());
				os.write("<h3>My web server works!</h3>\n".getBytes());
				os.write("</body></html>\n".getBytes());
			} //end if
			//if the file is a different directory that we know where to find
			else if( file.exists() )
			{
				//then make a new scanner object with that file
				Scanner reader = new Scanner( file );
				
				//while there is a new line
				while( reader.hasNextLine() )
				{
					//read the line and output it but replace the tags with proper info
					String data = reader.nextLine();
					data = data.replaceAll( "<cs371server>", "Thomas' Server" );
					data = data.replaceAll( "<cs371date>", date );
					os.write( data.getBytes() );
				} //end while
				
				//close the scanner
				reader.close();
			} //end else if
			//if its not the home directory or not a file in a directory, then its an error
			else
			{
				os.write("<html><head></head><body>\n".getBytes());
				os.write("<h3>404 Not Found</h3>\n".getBytes());
				os.write("</body></html>\n".getBytes());
			} //end else
		}
		else
		{
			//making a buffered input stream because images must be handled in bytes and scanner wont do that
			BufferedInputStream reader;
			
			try
			{
				//reads the image file till there is -1 (end of file) and writes it out
				reader = new BufferedInputStream( new FileInputStream( file ) );
				
				//reading in the data 
				int data = reader.read();
				while( data != -1 )
				{
					//writing out the data
					os.write( data );
					data = reader.read();
				} //end while
				
				//closing the buffered input stream
				reader.close();
			} //end try
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			} //end catch
		} //end else
	} //end writeContent
} // end class
