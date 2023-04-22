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
import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.io.IOException;




public class WebWorker implements Runnable
{

	private Socket socket;
	private String serverID = "Ruby H's webserver";

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
			String fName = "";

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			//fName = readHTTPRequest(is);
			File f = readHTTPRequest(is);
			fName = f.getPath();
            String contentType = "";

            // Getting Content Type
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
            writeContent(os, f, contentType);
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
	private File readHTTPRequest(InputStream is)
	{
		String line = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		File f = null;
		while (true)
		{
			try // Listening for new line readings
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine(); // Reads server GET request

				System.err.println("Request line: (" + line + ")");
				if(line.contains("GET")){ 
					String fpath = line.substring(line.indexOf("GET /")+5);
					fpath = fpath.substring(0,fpath.indexOf(" "));

					f = new File(fpath); // file referred to by the GET request. May not exist
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
		return f;
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

		// if we encounter a reading error write 404 not found
		if (fileExists == false){
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}else{ // Else write 200 OK
			os.write("HTTP/1.1 200 OK\n".getBytes()); 
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write(("Server: " + serverID + "\n").getBytes());
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
	private void writeContent(OutputStream os, File f, String contentType) throws Exception
	{

		// Write 404 if file not found
		if(!f.exists()) { 
			System.err.println(f.getName());
			os.write(("<h1>404 File Not Found</h1>").getBytes());
			return;
		}

		// If file is a text file
		if(contentType.equals("text/html")){

			FileReader fReader = new FileReader(f);

			try(BufferedReader bReader = new BufferedReader(fReader);){ // Try to open filereader

				String line;
	
				// While file has lines remaining
				while ((line = bReader.readLine()) != null) {
	
					if(line.contains("<cs371date>")){
						// Get current date
						Date d = new Date();
						DateFormat df = DateFormat.getDateTimeInstance();
						df.setTimeZone(TimeZone.getTimeZone("MST")); // Changed to MST
						line = line.replace("<cs371date>", df.format(d));

					} if(line.contains("<cs371server>")){
						// Replace tag with server ID
						line = line.replace("<cs371server>", serverID);
					} // end if
	
					os.write((line).getBytes()); // write each line of file to webpage
				} // end while
	
			} catch(FileNotFoundException e){
				System.err.println("File missing at \"" + f.getName() + "\"");
				os.write("<h1>404 Not Found</h1>\n".getBytes());
			} catch(IOException e){
				System.err.println("Error reading file \"" + f.getName() + "\"");
				os.write("<h1>404 Not Found</h1>\n".getBytes());
	
		}
	}


		else { // If file is an image, read byte by byte
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

		/* 
		os.write("<html><head>\n".getBytes());
		os.write("<title>WebServer</title>\n".getBytes());
		os.write(parseIconPath().getBytes());
		os.write("</head><body>\n".getBytes());
		os.write("<h3>My web server works!</h3>\n".getBytes());
		writeWebPage(fName, os, contentType);
		writeImage(os, "icon.png");
		os.write(("<img src=" + "\"" + "https://html.sammy-codes.com/images/small-profile.jpeg" + "\">").getBytes());
		os.write("</body></html>\n".getBytes());
		*/
	}


} // end class