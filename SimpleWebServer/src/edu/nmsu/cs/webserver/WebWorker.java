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

 import java.io.FileInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
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
			File in_file = readHTTPRequest(is);
			
			String cType = "";
			String path = in_file.getPath();

			if(path.contains(".jpeg")) {
				cType = "image/jpeg";
			}
			else if(path.contains(".png")) {
				cType = "image/png";
			}
			else if(path.contains(".gif")) {
				cType = "image/gif";
			}
			else if(path.contains(".html")) {
				cType = "image/png";
			}
			else if(path.contains("favicon.ico")) {
				cType = "image/x-icon";
			}
			else {
				System.err.println("Failed to get input file type. Input file type may not be supported.\n");	
			}
			writeHTTPHeader(os, in_file, cType);
			writeContent(os, in_file, cType);
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

		String line;
		File f = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				if (line.length() == 0)
					break;

				if(line.startsWith("GET")) {
					String pFile = line.substring(4, line.lastIndexOf(" "));
					String currentWorkingDir = System.getProperty("user.dir");
					String final_path = pFile + currentWorkingDir;
					try {

						f = new File(final_path);
					} 
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				System.err.println("Request line: (" + line + ")");
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
	private void writeHTTPHeader(OutputStream os, File file, String contentType) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		if(file.exists()) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}
		else {	
			os.write("HTTP/1.1 404 NOT FOUND".getBytes());
		}
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Jason's server\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		 // end else
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, File f, String type) throws Exception
	{
		if (!f.exists()){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>My web server works!</h3>\n".getBytes());
			os.write("</body></html>\n".getBytes());

		}
		else if (type.equals("text/html")){
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line;

			while((line=br.readLine()) != null) {

				if(line.contains("<cs371date>")) {
					Date d = new Date();
					DateFormat df = DateFormat.getDateTimeInstance();
					df.setTimeZone(TimeZone.getTimeZone("GMT"));
					line = line.replaceAll("<cs371date>",  df.format(d));
				}

				if (line.contains("<cs371server>")){
					line = line.replaceAll("<cs371server>",  "Jason's Web Server");
				}

				os.write(line.getBytes());
			}
			br.close();
		}
		else {
			FileInputStream fip = new FileInputStream(f);

			int bytesRead;
			while ((bytesRead=fip.read())!=-1) {
				os.write(bytesRead);
			}
			fip.close();
		}
	}

} // end class