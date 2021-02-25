import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.File;//ADDED 9/16/2020
import java.io.FileInputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Scanner;//Added 9/16/2020
import java.text.SimpleDateFormat;

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
			String file;
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			file = "www\\" + readHTTPRequest(is);
         
         String cT = file.substring(file.indexOf('.') + 1);
         //cT holds the file suffix (png, gif, jpeg, html)
         System.err.println("-----------------CT = " + cT + ".---------------");
         
         //use a switch on cT to set cT to the right kind of content Type
         switch(cT){
            case "html": 
               cT = "text/html";
               break;
            case "gif":
               cT = "image/gif";
               break;
            case "jpeg":
               cT = "image/jpeg";
               break;
            case "png":
               cT = "image/png";
               break;
            default: 
               cT = "text/html";
               break;
         }//end switch
			
         //testing statement
         System.err.println("Request is for a " + cT + " content type.");
         
         //latch file to the actual file it references
         
         File myFile = new File(file);
         
         String path = myFile.getAbsolutePath();
         System.out.println(myFile.getAbsolutePath());
			
			//writeHTTPHeader(os, "text/html", myFile);
			//THIS LINE WAS WORKING AND IS COMMENTED OUT FOR REFERENCE
         
         writeHTTPHeader(os, cT, myFile);
         writeContent(os, file, path, cT);
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
	private String readHTTPRequest(InputStream is){
		String line;
      String file = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true){
			try{
				while (!r.ready()) Thread.sleep(1);
            
				line = r.readLine();
				
            System.err.println("Request line: (" + line + ")");
            
				if(line.startsWith("GET")){
               //now we know we are in a get request
               //now we need to substring to get the dir / file they requested THE LINE RETURN ENDS IN "HTTP/1.1" so we want to destroy that part
               file = line.substring(5, line.indexOf(" HTTP/1.1"));//file updates with whatever is in it.
               }//end if
				if (line.length() == 0) break;
			}//end try
			catch (Exception e) {
				System.err.println("Request error: " + e);
				break;
			}//end catch
		}//end while
		return file;//returns file FILE IS EITHER NULL, HAS A REFERENCE, OR IS EMPTY
	}//end readHTTPRequest

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, File myFile) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
      if(myFile.exists()){
         os.write("HTTP/1.1 200 OK\n".getBytes());
      }
      else{
         os.write("HTTP/1.1 404 Not Found\n".getBytes());
      }
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Jon's very own server\n".getBytes());
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
	private void writeContent(OutputStream os, String file, String path, String ct) throws Exception
	{
      System.err.println("----Looking for file: " + file + "-------------");
      System.err.println("--------Content Type: " + ct + "-------------");
      
      if(file.length() == 0){
    	 //the request had nothing, this puts us on the front page
       os.write("<h3>My web server works!</h3>\n".getBytes());
      }
      else if(file.length() >= 1){
         //this request has an actual request in it. This file will either exist, or not
         //make file reference an actual file
         File myFile = new File(path);
         //for(int i = 0; i < 10; i++) System.err.println(path);
         //System.err.println(file + " is the name of the file we recieved");
         //System.err.println(myFile.getAbsolutePath());
         
         if(myFile.exists() && ct.equals("text/html")){
            //System.err.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            Scanner scan = new Scanner(myFile);
            while(scan.hasNextLine()){
               //while there is still info in this line
               String line = scan.nextLine();
               String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
               line = line.replaceAll("<cs371date>",date);
               line = line.replaceAll("<cs371server>", "Ben Longwell's Webserver!");
               //os.write(("<br>" + line).getBytes());
               os.write(line.getBytes());
            }//end while
         }//end if (text documents)
         else if(myFile.exists() && ct.equals("image/gif") || ct.equals("image/jpeg") || ct.equals("image/png")){
            System.err.println("********************" + ct + "**********************");
            System.err.println("********************" + file + "**********************");
            System.err.println("********************" + path + "**********************");
            
            //we are on a picture file
            //import the picture to bytes, spit them out
            InputStream input = new FileInputStream(file);

            int data = input.read();
            while(data != -1) {
               //do something with data...
               os.write(data);

               data = input.read();
            }//end while
            input.close();
         }//end else if
         else{
            //file cannot exist in the scope we have so far
            os.write("<h3>404 Page Not Found</h3>\n".getBytes());
            
         }//end else
         
      }//end if
	}

} // end class
