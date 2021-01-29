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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.OutputStream;

public class WebWorker implements Runnable
{

	public String i1;
	public String i2;
	public String filename;
	public String mime;
	public String output;
	public InputStream file;
	public int x ;
	public int a;
	public long fileLength;
	public byte ax[];
	private Socket socket;

	/**
	 * Constructor: must have a valid open socket
	 **/
	/*public WebWorker(Socket s)
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	/*public void run()
	{
		System.err.println("Handling connection...");
		try( InputStream  is = socket.getInputStream();
			 OutputStream os = socket.getOutputStream();) {
			
			readHTTPRequest(is, os);
			writeHTTPHeader(os, mime); 
			writeContent(os);
			os.flush();
			socket.close();
		}//try
		catch (Exception e) {
			      System.err.println("Output error: " + e);
			   }//catch
		
		System.err.println("Done handling connection.");
		return;
	}//run

	/**
	 * Read the HTTP request header.
	 **/
	
	//return string
/*	private void readHTTPRequest(InputStream is, OutputStream os)
	{
		String line;
	//	String testline;
		//String name = "";
		BufferedReader read = new BufferedReader(new InputStreamReader(is));
		BufferedReader r;
		int count = 0;
		while (true)
		{
			try
			{
				count++;
				  while (!read.ready())
					  Thread.sleep(1);
				i1 = read.readLine();
				String input[] = i1.split(" ");
				if(input.length > 1 && count == 1) {
					filename = input[1];
					filename = filename.substring(1);
					if(filename.endsWith(".html") || filename.endsWith(".HTML")) {
			        	  mime = "text/html";
			          }//.html
				         else if(filename.endsWith(".gif") || filename.endsWith(".GIF")) {
				        	 mime = "image/gif";
				         }//.gif
				         else if(filename.endsWith(".jpeg") || filename.endsWith(".JPEG")||filename.endsWith(".jpg")) {
				                  mime = "image/jpeg";
				         }//.jpeg
				         else if(filename.endsWith(".png") || filename.endsWith(".PNG")) {
				        	 mime = "image/png";
				         }//.png
				         else {
				        	 x = 404;
				        	 throw new Exception();
				         }//error
				}//if input.length > 1
				System.err.println("Request line: (" + i1 + ")");
				if(i1.length() == 0)
					break;
			}//try
			catch (Exception e) {
				System.err.println("Request error: "+e);
		        break;
			}//catch
		}//while true
		try {
			if(mime == "text/html") {
				output = "";
				x = 200;
				r = new BufferedReader(new FileReader(filename));
				Date date = new Date();
			    DateFormat dform = DateFormat.getDateTimeInstance();
			    dform.setTimeZone(TimeZone.getTimeZone("GMT"));
				while( (i2 = r.readLine()) != null) {
					i2 = i2.replace("<cs371date>", dform.format(date));
					i2 = i2.replace("<cs371server>", "Johnathon Garcia");
					output = output + i2;
					if(i2.length() == 0)
						break;
				}//while i2 = r.readline != null 
			}//if mime == text/html
			else {
				x = 200;
				file = new FileInputStream(filename);
			    fileLength = new File(filename).length();
			    ax = new byte[(int)fileLength];        
			}//else
		}//try
		catch (Exception e) {
	         x = 404;
	        } //catch if the file was not found
	   return;
	}//read 
	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	/*private void writeHTTPHeader(OutputStream os, String mimeType) throws Exception
	{
		 Date d = new Date();
		 DateFormat df = DateFormat.getDateTimeInstance();
		 df.setTimeZone(TimeZone.getTimeZone("GMT"));
		 if(x == 404)
		     os.write("HTTP/1.1 404 Not Found\n".getBytes());
		 os.write("Content-Type: ".getBytes());
	     os.write(mimeType.getBytes());
		 os.write("\n\n".getBytes());
		 return;
	}//writeHTTPHeader

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	//another variable File file
	/*private void writeContent(OutputStream os) throws Exception
	{

	      if(output != null) {
	         os.write(output.getBytes());
	      }// if
	      else if(mime == "image/jpeg" || mime == "image/gif" || mime == "image/png") {
		            while((a = file.read(ax)) > 0) {
	                    os.write(ax, 0, a);
		            }
	      }//image handle
	      if(x == 404){
	         os.write("<html><head></head><body>\n".getBytes());
	         os.write("<h3>404: Not Found</h3>\n".getBytes());
	         os.write("</body></html>\n".getBytes());
	        }//if the file not found

	    }//write Content

} // end class*/


import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;

public class WebWorker implements Runnable
{


public String i1;
public String i2;
public String filename;
public String mime;
public String output;
public int x;
public int a;
public long fileLength; 
public byte[] ax;
public InputStream file;


private Socket socket;

/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
}

/**
* Worker thread starting point. Each worker handles just one HTTP 
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
	//status prints
   System.err.println("Handling connection...");
   try( InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();) 
      {
      readHTTPRequest(is, os);
      writeHTTPHeader(os, mime); 
      writeContent(os);
      os.flush();
      socket.close();
   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   System.err.println("Done handling connection.");
   return;
}

/**
* Read the HTTP request header.
**/
private void readHTTPRequest(InputStream is, OutputStream os){

   BufferedReader read = new BufferedReader(new InputStreamReader(is));
   BufferedReader r;
   int count = 0;
   while (true) {
      try {
	      count++;
         while(!read.ready())
        	 Thread.sleep(1);
         i1 = read.readLine();
         //System.out.println("\n\n\naaaaaa" + + "bbbbb\n\n\n");
	   String inputPrs[] = i1.split(" ");
	   //System.out.println("\n\n\naaaaaa" + filename + "bbbbbbbb\n\n\n");
	   if(inputPrs.length > 1 && count == 1){
	       filename = inputPrs[1];//1
	       //System.out.println("\n\n\naaaaaa" + filename + "bbbbbbbb\n\n\n");
	       filename = filename.substring(1);
	       //System.out.println("\n\n\naaaaaa" + i1 + "\n\n\n");
          if(filename.endsWith(".html")||filename.endsWith(".HTML")) {
        	  mime = "text/html";
          }//html
	          if(filename.endsWith(".gif")||filename.endsWith(".GIF")) {
	        	 mime = "image/gif";
	         }//gif
	          if(filename.endsWith(".jpeg")||filename.endsWith(".JPEG")||filename.endsWith(".jpg")) {
	                  mime = "image/jpeg";
	                  //System.out.println("\n\n\naaaaaa" + filename + "\n\n\n");
	         }//jpeg
          //apng???
	          if(filename.endsWith("png")||filename.endsWith("PNG") ) {
	        	 mime = "image/png";
	        	 //System.out.println("\n\n\nPNGGGGGGGG\n\n\n");
	         }//png
	         else {
	        	 x = 404;
	        	 throw new Exception();
	         }//else
	                 
	}//if
   
         System.err.println("Request line: (" + i1 + ")");
         if (i1.length()==0) break;
      }//try 
      catch (Exception e) {
         System.err.println("Request error: " + e);
         break;
      }//catch
   }//while


  try{
	   if (mime == "text/html"){
		      output = "";
		      x = 200;
		      r = new BufferedReader(new FileReader(filename));
		      Date date = new Date();
		      DateFormat dformat = DateFormat.getDateTimeInstance();
		      dformat.setTimeZone(TimeZone.getTimeZone("MST"));
		   	while((i2 = r.readLine()) != null){
			         i2 = i2.replace("<cs371date>", dformat.format(date));
			         i2 = i2.replace("<cs371server>", "Johnathon Server");
			          output += i2;
		         if (i2.length() == 0) 
		         break;
		   }//while
   }//if
    else{
      x = 200;
      file = new FileInputStream(filename);
      fileLength = new File(filename).length();
      ax = new byte[(int)fileLength];         
    }//else 
   }//try
       catch (Exception e) {
         x = 404;
        } //if the was file not found
   return;
}//read


/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param mimeType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String mimeType) throws Exception{
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
   if(x == 200)
   os.write("HTTP/1.1 200 OK\n".getBytes());
   else if(x == 404)
   os.write("HTTP/1.1 404 Not Found\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(mimeType.getBytes());
   os.write("\n\n".getBytes()); 
   return;
}//writeHTTPHeader



private void writeContent(OutputStream os) throws Exception{

      if(output != null) {
         os.write(output.getBytes());
      }// html handle
      else if(mime == "image/jpeg" || mime == "image/gif" || mime == "image/png") {
	            while((a = file.read(ax)) > 0) {
	            	//System.out.println("\n\n\nthe mime is:" + mime + "\n\n\n");
                    os.write(ax, 0, a);
	            }//while
      }//else if
      if(x == 404){
         os.write("<html><head></head><body>\n".getBytes());
         os.write("<h3>404 Not Found</h3>\n".getBytes());
         os.write("</body></html>\n".getBytes());
        }//if the was file not found

    }//write

} //class
