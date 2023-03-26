package edu.nmsu.cs.webserver;

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;  
import java.util.Date;  

public class WebWorker implements Runnable
{

	private Socket socket;
	private File file; 
	private String request;
	private String fileName;

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
         
         //****************************
			file = new File(request);
			fileName = request.substring(request.lastIndexOf(".")+1);
			if(file.exists() && file.isFile())
			{
				//check to the fileName to determine the file type
				if (fileName.equals("gif"))
				{
					writeHTTPHeader(os, "image/gif");
				}
				else if (fileName.equals("png")) 
				{
					writeHTTPHeader(os, "image/png");
				}
				else if (fileName.equals("jpg"))
				{
					writeHTTPHeader(os, "image/jpeg");
				}
				else
				{
					writeHTTPHeader(os, "text/html");
				}
            
				writeContent(os);
			}
			else
			{
				//else output 404 Error Page Not Found
				writeHTTPHeader(os, "text/html");
				os.write("<html><head></head>".getBytes());
				os.write("<body><h1><center>404 Error Page Not Found</center></h1></body></html>".getBytes());
			}
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

<<<<<<< HEAD
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
				if (line.length() == 0)
					break;	
=======
   /**
   * Write the HTTP header lines to the client network connection.
   * @param os is the OutputStream object to write to
   * @param contentType is the string MIME content type (e.g. "text/html")
   * @param address is the website address
   **/
   private void writeHTTPHeader(OutputStream os, String contentType, String address) throws Exception{
      
      Date d = new Date();
      DateFormat df = DateFormat.getDateTimeInstance();
      df.setTimeZone(TimeZone.getTimeZone("GMT-6"));
      
      String copy = '.' + address;
      File f1 = new File(copy);
      
      try{
         FileReader file = new FileReader(f1);
         BufferedReader r = new BufferedReader(file);
      }catch(FileNotFoundException e){
         System.out.println("File not found: " + address);
         os.write("HTTP/1.1 404 Error: Not Found\n".getBytes());
      }
      
      os.write("HTTP/1.1 200 OK\n".getBytes());
      os.write("Date: ".getBytes());
      os.write((df.format(d)).getBytes());
      os.write("\n".getBytes());
      os.write("Server: Nate's server\n".getBytes());
      os.write("Connection: close\n".getBytes());
      os.write("Content-Type: ".getBytes());
      os.write(contentType.getBytes());
      os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
      return;
      
   } // of writeHTTPHeader
>>>>>>> ff1db646634be5f7652800e201dd4536d6a362ec

				if (line.substring(0,3).equals("GET"))
				{
					String[] part = line.split(" ");
					request = "." + part[1];
					System.out.println(request);
					if(request.equals("./"))
					{
						System.out.println("This server works");

					}

				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return;
	}

<<<<<<< HEAD
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
		os.write("HTTP/1.1 200 OK\n".getBytes());
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
		 //check the file type if it is html
		if(fileName.equals("html"))
		{
			//output the html file
			BufferedReader b = new BufferedReader(new FileReader(file));
			String replace;
			
			Date date = new Date();  
		    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");  
		    String strDate= formatter.format(date); 
		
		// while loop to replace the text that fix the request
        while((replace = b.readLine()) != null)
        {
        	replace = replace.replaceAll("<cs371date>", strDate);
        	replace = replace.replaceAll("<cs371server>", "Nhat Le's test server");
			os.write(replace.getBytes());
        }
         b.close();
		}
		else
		{
			//output image and read local image
			FileInputStream f = new FileInputStream(request);
			int i = f.available();
			//use array to store the data
			byte[] temp = new byte[i];
			f.read(temp);
			f.close();
			os.write(temp);

		}
		//os.write("<html><head></head><body>\n".getBytes());
		//os.write("<h3>My web server works!</h3>\n".getBytes());
		//os.write("</body></html>\n".getBytes());
	}

=======
>>>>>>> ff1db646634be5f7652800e201dd4536d6a362ec
} // end class
