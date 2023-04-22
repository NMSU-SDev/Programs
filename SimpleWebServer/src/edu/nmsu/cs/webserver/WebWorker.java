package edu.nmsu.cs.webserver;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import java.net.URL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class WebWorker implements Runnable
{

	private Socket socket;
	private String serverID = "Austin's Server";
	public WebWorker(Socket s)
	{
		socket = s;
	}


	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			String contentType = "";

			InputStream is = socket.getInputStream(); 
			OutputStream os = socket.getOutputStream(); 
			File f = readHTTPRequest(is); 
			String fName = f.getPath();

			
			if(fName.contains(".gif")){
				contentType = "image/gif";
			}
			else if (fName.contains(".jpeg")) {
				contentType = "image/jpeg";	
			}
			else if (fName.contains(".png")) {
				contentType = "image/png";	
			}
			else if (fName.contains(".html")) {
				contentType = "text/html";	
			}
			else if (fName.contains("favicon.ico")) {
				contentType = "image/x-icon";	
			}else{
				System.err.println("File type not recognized");
			}

			writeHTTPHeader(os, contentType, f.exists());
			writeWebPage(f, os, contentType);
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

	
	private File readHTTPRequest(InputStream is)
	{
		String line; 
		BufferedReader r = new BufferedReader(new InputStreamReader(is)); 
		File f = null; 
		
		while (true)
		{
			try
			{
				while (!r.ready()) 
					Thread.sleep(1); 
				
				line = r.readLine(); // reads one line of input
				
				// Find the GET line and parse requested file path
				if(line.contains("GET")){ 
					String fpath = line.substring(line.indexOf("GET /")+5);
					fpath = fpath.substring(0,fpath.indexOf(" "));
					f = new File(fpath); // GET request file
				}
				
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
	}

	

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, boolean fileExists) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("MST")); // Changed to MST

		if(fileExists == false) { 
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		} else{
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write(("Server: " + serverID + "\n").getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); 
		return;
	}
	

	private void writeWebPage(File f, OutputStream os, String ContentType) throws Exception{

		//404, file does not exist/found
		if(!f.exists()) { 
			os.write(("<h1>404 File Not Found</h1>").getBytes());
			return;
		}
       	
		
		if(ContentType.equals("text/html")) { 
			FileReader fr = new FileReader(f);
	       	try (BufferedReader br = new BufferedReader(fr)) {
				
	       		String line; 
	      		while((line = br.readLine()) != null)  {
	          
	            	if (line.contains("<cs371date>")){
	            		Date d = new Date();
	            		DateFormat df = DateFormat.getDateTimeInstance();
	            		df.setTimeZone(TimeZone.getTimeZone("GMT"));
	            		line = line.replaceAll("<cs371date>",  df.format(d));
	            	}
	            	
	            	if (line.contains("<cs371server>")){
	            		line = line.replaceAll("<cs371server>",  serverID);
	            	}
	            	
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
		
	
	} // end writeWebPage
} // end class
