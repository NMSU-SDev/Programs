package edu.nmsu.cs.webserver;

//Alexander Acuna
//2/17/2023
//CS468
//Program 1: Simple Web Server

/*
 * CHANGELOG:
 * - Added http code and page variables
 * - Added getSort and searchFile methods
 * - Changed readHTTPRequest to allow custom html file support 
 * - Changed writeHTTPHeader to support 404 errors 
 * - Changed writeContent to support 404 errors and custom html pages
 */

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
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;



public class WebWorker implements Runnable
{
	int code; // Integer for http status code
	File page; // File for html page 
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
			readHTTPRequest(is);
			writeHTTPHeader(os, "text/html");
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

	//HTML file searcher: Checks to see if requested page exists 
	//PRE: HTML address string
	//POST: File address set and http status code set 
	public void searchFile(String fileName){

		//Trys to find file 
		try{
			//File set to given address
			File file = new File(fileName);
			
			//file existance check: Sets http code to 200 if found and page set to file 
			//Sets code to 404 if cant be found 
			if (file.exists()){
				System.out.println("File found");
				page = file;
				code = 200;}
			else{System.out.println("File not found");
				code = 404;
				page = null;
			}


		}
		//Catches error if file address cannot be accessed
		catch(Exception e){System.out.println("File not found");}

	}
	//GET request handeler to search for html page request 
	//PRE: GET string from input string
	//POST:  HTML address if GET HTML request 
	public void getSort(String getLine){
		
		//string array to split file extension 
		String temp[] = getLine.split("[.]");
	
		//html check: icon request ignored 
		if(temp[1].equals("html")){

			//sets address string to windows file seperators 
			String address = getLine.replaceAll("/", "\\\\");

			//finds active directory to string 
			String curDir = System.getProperty("user.dir");

			//sends active dir and file dir to searchFile method
			searchFile(curDir+address);

		}
		

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

				//Creates string array to split input stream line
				String[] temp = line.split(" ");

				//Checks if current line is a GET request 
				if(temp[0].equals("GET")){

					//Sends page address to getSort method
					getSort(temp[1]);
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
		df.setTimeZone(TimeZone.getDefault());

		//Checks for 404 error 
		if(code == 404){os.write("HTTP/1.1 404 Page not found\n".getBytes());}
		else {os.write("HTTP/1.1 200 OK\n".getBytes());}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: H-S1\n".getBytes());
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
		//404 HTML Message 
		if(code == 404){
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h1>Error 404</h1>\n".getBytes());
			os.write("<h3>Sorry, page not found :< </h3>\n".getBytes());

		}
		//200 HTML Message 
		else{
		
		//Grabs current date
		Date date = new Date();
		//Sets date format 
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getDefault());

		//Sets html page to string for parsing
		String pageBody = Files.readString(page.toPath());

		//Sets <cs371date> to current date
		pageBody = pageBody.replaceAll("<cs371date>", df.format(date).toString() );

		//Sets <cs371server> to server name 
		pageBody = pageBody.replaceAll("<cs371server>", "H-S1");

		//Writes html page to outstream
		os.write(pageBody.getBytes());
		
		}
	}

} // end class
