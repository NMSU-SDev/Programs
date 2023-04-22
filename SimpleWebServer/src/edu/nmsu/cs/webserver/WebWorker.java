package edu.nmsu.cs.webserver;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
public class WebWorker implements Runnable
{
	// Socket Object + serverID
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
			InputStream inputStream = socket.getInputStream(); 
			OutputStream outputStream = socket.getOutputStream(); 
			File f = readHTTPRequest(inputStream); 
			String filePath = f.getPath();

			if(filePath.contains(".png")){
				contentType = "image/png";	
			}
			else if (filePath.contains(".jpeg")) {
				contentType = "image/jpeg";	
			}
			else if (filePath.contains(".gif")) {
				contentType = "image/gif";
			}
			else if (filePath.contains("favicon.ico")) {
				contentType = "image/x-icon";	
			}
			else if (filePath.contains(".html")) {
				contentType = "text/html";	
			}else{
				System.err.println("Unrecognized file type, cannot continue.");
			}

			
			writeHTTPHeader(outputStream, contentType, f.exists());
			writeWebPage(f, outputStream, contentType);
			outputStream.flush();
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
	 * Writes the HTTP header lines from the GET request.
	 * 
	 * @param InputStream
	 *      Request stream to read from.
	 * @precondition
	 * 		InputStream is not null.
	 * @postcondition
	 * 		Returns the file from the given GET request.
	 **/
	private File readHTTPRequest(InputStream InputStream)
	{
		String line; 
		BufferedReader r = new BufferedReader(new InputStreamReader(InputStream)); 
		File file = null; 
		
		while (true)
		{
			try
			{
				while (!r.ready()) 
					Thread.sleep(1); 
				
				line = r.readLine(); // reads one line of input
				
				// Obtains the GET line and parses for the file path
				if(line.contains("GET")){ 
					String fpath = line.substring(line.indexOf("GET /")+5);
					fpath = fpath.substring(0,fpath.indexOf(" "));
					file = new File(fpath); // GET request file
				}// end of while
				
				if (line.length() == 0) 
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		} //end of while
		
		return file;
	}

	

	/**
	 * Writes the HTTP header lines.
	 * 
	 * @param OutputStream
	 *      OutputStream object where the program writes to
	 * @param contentType
	 *      Determines the type of content we are handling ie. "image/png", "text/html"
	 * *@param fileExists
	 *      Used to check if the file exists.
	 * @precondition
	 * 		OutputStream is not null
	 * 		contentType is image/png, image/jpeg, image/gif, text/html, or favioon icon
	 * @postcondition
	 * 		written HTTP header lines
	 * 
	 **/
	private void writeHTTPHeader(OutputStream OutputStream, String contentType, boolean fileExists) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("MST"));

		if(fileExists == false) { 
			OutputStream.write("HTTP/1.1 404 Not Found\n".getBytes());
		} else{
			OutputStream.write("HTTP/1.1 200 OK\n".getBytes());
		}

		OutputStream.write("Date: ".getBytes());
		OutputStream.write((df.format(d)).getBytes());
		OutputStream.write("\n".getBytes());
		OutputStream.write(("Server: " + serverID + "\n").getBytes());
		OutputStream.write("Connection: close\n".getBytes());
		OutputStream.write("Content-Type: ".getBytes());
		OutputStream.write(contentType.getBytes());
		OutputStream.write("\n\n".getBytes()); 
		return;
	}
	
	/**
	 * Writes the body of the web page.
	 * 
	 * @param file
	 *      File used to write content to.
	 * @param Outputstream
	 *      OutputStream object where the program writes to
	 * *@param ContentType
	 *      Determines the type of content we are handling ie. "image/png", "text/html"
	 * @precondition
	 * 		File is not null
	 *      Outputstream is not null
	 * @postcondition
	 * 		Writes content of the image or html file to the web page.
	 **/
	private void writeWebPage(File file, OutputStream Outputstream, String ContentType) throws Exception{

		// 404 Error
		if(!file.exists()) { 
			Outputstream.write(("<h1>404 File Not Found</h1>").getBytes());
			return; 
		}
       	
		
		if(ContentType.equals("text/html")) { 
			FileReader fr = new FileReader(file);
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
	            	
	            	Outputstream.write((line).getBytes());
	           	
	      		} //end of while 
	      		
	       	}// end of try	
	       	catch (Exception e) {
		    	System.err.println("Error reading file: " + e);
	       	}
			
		}

		// reading images 1 byte at a time
		else { 
			try (InputStream is = new FileInputStream(file); ) {
			            
				int myByte = -1;
				while ((myByte = is.read()) != -1) {
					Outputstream.write(myByte);
			    }
			 } catch (Exception e) {
			    	System.err.println("Error writing to file: " + e);
			 }
		}
		
	
	} // end writeWebPage
}
