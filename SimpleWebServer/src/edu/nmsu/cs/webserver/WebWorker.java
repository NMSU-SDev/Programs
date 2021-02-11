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
import java.util.*;
import java.lang.Runnable;

public class WebWorker implements Runnable
{

	private Socket socket;
	String l2;
	public String fname;
	String s;
	int errorCode;
	long filesize; 
	byte[] buffer;
	InputStream file;
	String mType;
		
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
		try(
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();)
			{
			readHTTPRequest(is,os);
			writeHTTPHeader(os, mType);
			writeImage(os);
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
	private void readHTTPRequest(InputStream is, OutputStream os)
	{
		String line;
		int counter=0;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		BufferedReader html;
		while (true)
		{
			try
			{   counter+=1;
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				
				String request[] = line.split(" ");
				   if(request.length > 1 && counter == 1){
				       fname = request[1];
				       fname = fname.substring(1);
				       //puts the correct mime type for file extension
				          if(fname.endsWith(".html"))
					          mType = "text/html";
					         else if(fname.endsWith(".gif"))
					                  mType = "image/gif";
					         else if(fname.endsWith(".jpg"))
					                  mType = "image/jpeg";
					         else if(fname.endsWith(".png"))
					                  mType = "image/png";
				            else if(fname.endsWith(".ico"))
				                     mType = "image/ico"; 
					         System.err.println(fname);
					}
				   
				         System.err.println("Request line: ("+line+")");
				         if (line.length()==0) break;
				      } catch (Exception e) {
				         System.err.println("Request error: "+e);
				         break;
				      }
				   }		        

		
		
		try{
			if (mType == "text/html") {
		      s = "";
		      errorCode = 200;
		      html = new BufferedReader(new FileReader(fname));
		      Date d1 = new Date();
		      DateFormat df1 = DateFormat.getDateTimeInstance();
		      df1.setTimeZone(TimeZone.getTimeZone("GMT"));

		   	while((l2 = html.readLine()) != null){
			         l2 = l2.replaceAll("<cs371date>", df1.format(d1));
			         l2 = l2.replaceAll("<cs371server>", "Kailey's server");
			          s += l2;
		         if (l2.length()==0) 
		         break;
			   }
		}//end if
			
			
			 else{
			      errorCode = 200;
			      file = new FileInputStream(fname);
			      filesize = new File(fname).length();
			      buffer = new byte[(int)filesize];         
			    }		
			
	}//end try 
		       catch (Exception e) {
                   errorCode = 404;
		        } 			        
		   return;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
	{
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		if(errorCode == 200)
		    os.write("HTTP/1.1 200 OK\n".getBytes());
		else if(errorCode == 404)
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		//os.write("Date: ".getBytes());
		//os.write((df.format(d)).getBytes());
		//os.write("\n".getBytes());
		//os.write("Server: Jon's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		//os.write("Connection: close\n".getBytes());
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
	
	 private void writeImage(OutputStream os) throws Exception{
           int a;
	       if(s != null)
	          os.write(s.getBytes());

	       else if(mType == "image/jpeg" || mType == "image/gif" || 
	               mType == "image/png" || mType == "image/ico")
	 	            while((a=file.read(buffer))>0)
	                os.write(buffer,0,a);
	     
	       if(errorCode == 404){
	          os.write("<html><head></head><body>\n".getBytes());
	          os.write("<h3>404: Error Page not Found!</h3>\n".getBytes());
	          os.write("</body></html>\n".getBytes());
	         }

	     }	
} // end class