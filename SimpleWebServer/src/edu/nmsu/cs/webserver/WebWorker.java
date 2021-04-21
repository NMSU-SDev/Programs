package edu.nmsu.cs.webserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.FileReader;
import java.io.FileInputStream;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.time.*;

public class WebWorker implements Runnable
{

	private Socket socket;

	public WebWorker(Socket s)
	{
		socket = s;
	}

	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			String filename;
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			filename = readHTTPRequest(is);
			
			if(filename == " / ") {
				writeHTTPHeader(os, "text/html", filename);
				htmlWriter(os, filename);
			}

			if(filename.contains(".html")) {
				writeHTTPHeader(os, "text/html", filename);
				htmlWriter(os, filename);
			}
			if(filename.contains(".png")) {
				writeHTTPHeader(os, "image/png", filename);
				imageOutput(os, filename);
			}
			if(filename.contains(".jpg")) {
				writeHTTPHeader(os, "image/jpg", filename);
				imageOutput(os, filename);
			}
			if(filename.contains(".gif")) {
				writeHTTPHeader(os, "image/gif", filename);
				imageOutput(os, filename);
			}

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

	private String readHTTPRequest(InputStream is)
	{
		String line;
		String file = "";
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if(line.startsWith("GET")) {
					file = line.substring(5, line.length()-9);
						
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
		return file;
	}

	public void htmlWriter(OutputStream os, String filename) {
		try {
			FileReader file = new FileReader(filename);
			Scanner scanner = new Scanner(file);
			String line = "";
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				LocalDate currentTime = LocalDate.now();
				line = line.replaceAll("<cs371date>", currentTime.toString());
				line = line.replaceAll("<cs371server>", "Kian Bianco's 371 Server");
				os.write(line.getBytes());
			}
			scanner.close();
			}catch(Exception e){
				System.err.println("Output error: " + e);
			}
		return;
	}


	public void imageOutput(OutputStream os, String image) {
		try {
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(image));
			int currentLine = is.read();

			while(currentLine != -1) {
				os.write(currentLine);
				currentLine = is.read(); 
			}

			is.close();
			}catch(Exception e){
				System.err.println("Output error: " + e);
			}finally{ //here to avoid error with catch, not needed
			}
		return;
	}

	private void writeHTTPHeader(OutputStream os, String contentType, String file) throws Exception
	{
		int flag = 0;
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
			FileReader filename = new FileReader(file);
		}catch(Exception e) {
			os.write("HTTP/1.0 404 Not Found\n".getBytes());
			flag = 1;
		}
		finally {
			if(flag == 0) {
				os.write("HTTP/1.1 200 OK\n".getBytes());
			}
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Kian's very own server\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	}

	private void writeContent(OutputStream os) throws Exception
	{
		os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>My web server works!</h3>\n".getBytes());
		os.write("</body></html>\n".getBytes());
	}

}
