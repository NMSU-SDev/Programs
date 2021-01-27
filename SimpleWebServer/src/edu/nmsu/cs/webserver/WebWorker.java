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

import java.io.BufferedReader;
import java.io.*;
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
	
	private String filedirectory; 

	/**
	 * Constructor: must have a valid open socket
	 **/
	private boolean Rfile=true;
	private boolean hp=true; 
	
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
//			String cntyp="text/html";
//			switch(cntyp) {
//			
//			case "text/html" :
//				writeHTTPHeader(os, "text/html");
//				break;
//				
//			case "image/gif" :
//				writeHTTPHeader(os, "image/gif");
//				break;
//				
//			case "image/jpeg" :
//				writeHTTPHeader(os, "image/jpeg");
//				break;
//				
//			case "image/png" :
//				writeHTTPHeader(os, "image/png");
//				break;
//			}
			writeHTTPHeader(os, "image/png");
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
				if(line.contains("GET /")&&!line.contains("GET / ")) {
					hp=false;
					String str=line.substring(5,line.length()-8); 
					File directory= new File (str);
					System.err.println(str);
					if(directory.isFile())
						filedirectory=str;
					else {
						Rfile=false; 
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
		
		String []line2= new String[1000];
		int readByte;
		if(hp) {
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>Kashka's server</h3>\n".getBytes());
			os.write("</body><html>\n".getBytes());
			os.close();
		}
		
		if(Rfile==false){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>404 Not Found</h3>\n".getBytes());
			os.write("</body><html>\n".getBytes());
			os.close();
			return; 

		}
		
		try {
			File fr= new File (filedirectory);
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			BufferedReader Br= new BufferedReader(new FileReader(fr));
			readByte=Br.read();
			String csdate="<cs371date>";
			String csServer="<cs371server>";
			
			for (int i=0;i<line2.length; i++) {
				line2[i]=Br.readLine();
				if (line2[i].contains(csdate)) {
					String str1=line2[i];
					String str2=line2[i];
					str1=line2[i].replaceAll(csdate, df.format(d));
					if(line2[i].contains(csServer)) {
						str2=str1.replaceAll(csServer, "Kashka's server"); 
					}
					os.write(str2.getBytes());
				}
				else
				while(readByte!=-1) {
					os.write(readByte);
					readByte=Br.read();
				}
				//os.write(line2[i].getBytes());
			}
			Br.close();

			
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}

} // end class
