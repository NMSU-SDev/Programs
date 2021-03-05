//Modified by : Dylan Lassard
//Date : 2-19-21
//Course : CS 371 Software Development : Program1

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
 * modifed by Dylan Lassard
 **/

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;

public class WebWorker implements Runnable
{
        private Socket socket;
        private boolean favSet;
        private int errorCode;
        String userDirectory = System.getProperty("user.dir");
        private String mimeType;
        
        /**
        * Constructor: must have a valid open socket
        **/
        public WebWorker(Socket s)
        {
            socket = s;
        } //end WebWorker constructor
        
        public String getMimeType() 
        {
                return this.mimeType;
        } //end MimeType accessor
        
        public void setMimeType(String type) 
        {
                this.mimeType = type;
        } //end mimeType mutator
        
        public int getErrorCode() 
        {
                return this.errorCode;
        } //end ErrorCode accessor
        
        public void setErrorCode(int num) 
        {
                this.errorCode = num;
        } //end ErrorCode mutator
        
        // Easy way to format dates
        public String getDate() 
        {
                String dateToString;
                Date date = new Date();
                DateFormat dateF = DateFormat.getDateTimeInstance();
                dateF.setTimeZone(TimeZone.getTimeZone("GMT"));
                
                dateToString = dateF.format(date);
                return dateToString;
        } //end dateToString method
        
        //create icon for your server and properly send it to the browser upon request.
        public boolean getFavicon() 
        {
                return this.favSet;
        } //end Favicon accessor
        
        public void setFavicon(boolean set) 
        {
                this.favSet = set;
        } //end Favicon mutator
        
        /**
        * Worker thread starting point. Each worker handles just one HTTP
        * request and then returns, which destroys the thread. This method
        * assumes that whoever created the worker created it with a valid
        * open socket object.
        **/
        public void run()
        {
            System.err.println("Handling connection...");
            try 
            {
                InputStream  is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                String contentFile = readHTTPRequest(is);
                
                // If nothing went wrong...then do this
                if(getErrorCode() == 200) 
                {
                        // Checking what type of file is being processed
                                if(contentFile.contains(".html"))
                                {
                                        setMimeType("text/html");
                                } //end if
                                
                                else
                                {
                                     setMimeType("text/html");
                                } //end else
                        }  //end if block
                
                else 
                {
                      mimeType = "text/html";
                } //end else
                
                writeHTTPHeader(os, getMimeType(), contentFile);
                writeContent(os, getMimeType(), contentFile);
                os.flush();
                socket.close();
          } //end try
            
            catch (Exception e) 
            {
                System.err.println("Output error: " + e);
            }
            
            System.err.println("Done handling connection.");
            return;
        } //end run method
        
        /**
        * Read the HTTP request header and return the file path in a String.
        **/
        private String readHTTPRequest(InputStream is)
        {
            String line;
            String path = "";
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
           
            while (true) 
            {
                try 
                {
                    while (!r.ready()) 
                    {
                        Thread.sleep(1);
                    } //end while
                    
                    line = r.readLine();

                    if(line.contains("GET ")) 
                    {
                        path = line.substring(4);
                        
                        for(int i = 0; i < path.length(); i++)
                        {
                            if(path.charAt(i) == ' ')
                            {
                                path = path.substring(0, i);
                            } //end if
                        } //end for
                    } //end if block
                    
                    System.err.println("Request line: (" + line + ")");
                    
                    if (line.length() == 0)
                    {
                        break;
                    } //end if
                } //end try block
                
                catch (Exception e) 
                {
                    System.err.println("Request error: "+e);
                    break;
                } //end catch
            } //end while loop
            
                File file = new File(userDirectory+path);
                
                if(file.exists())
                {
                        setErrorCode(200);
                } //end if
                // File doesnt exist 
                else
                {
                        setErrorCode(404);
                } //end else
                
            return path;
        } //end read HTTPRequest method
        
        /**
        * Write the HTTP header lines to the client network connection.
        * @param os is the OutputStream object to write to
        * @param contentType is the string MIME content type (e.g. "text/html")
        **/
        private void writeHTTPHeader(OutputStream os, String contentType, String contentPath) throws Exception
        {
            String path = userDirectory + contentPath;
            
                try 
                {
                        FileReader contentFile = new FileReader(path);
                os.write("HTTP/1.1 200 OK\n".getBytes());
                System.out.println("Content Collected: " + path + " successfully!");
            }
                
            catch(FileNotFoundException fnfe) 
                {
                os.write("HTTP/1.1 404 ERROR\n".getBytes());
            }
            
            os.write("Date: ".getBytes());
            os.write(getDate().getBytes());
            os.write("\n".getBytes());
            os.write("Server: Dylan's Server\n".getBytes());
            os.write("Connection: close\n".getBytes());
            os.write("Content-Type: ".getBytes());
            os.write(contentType.getBytes());
            os.write("\n\n".getBytes());
            return;
        }
        
        /**
        * Write the data content to the client network connection. This MUST
        * be done after the HTTP header has been written out.
        * @param os is the OutputStream object to write to
        **/
        private void writeContent(OutputStream os, String contentType, String contentPath) throws Exception
        {
                String content = "";
                String path = userDirectory + contentPath;
                
                if(contentType.contains("text/html")) 
                {
                try 
                {
                    File fileName = new File(path);
                    BufferedReader inBuffer = new BufferedReader(new FileReader(fileName));
                    
                    while((content = inBuffer.readLine()) != null) 
                    {
                        // Replace that tag with date and server tag as required in p1
                        if(content.contains("<cs371date>"))
                        {
                                                content = getDate();
                        }
                        
                        if(content.contains("<cs371server>"))
                        {
                                                content = "Hello World, the owner of this server is Dylan L.";
                        }
                        
                        os.write(content.getBytes());
                        os.write( "\n".getBytes());
                    }
                }
                
                catch(FileNotFoundException fnfe) 
                {
                    System.err.println("ERROR: File " + path + " does not exist!");
                    write404Content(os, path);
                }
            }
                
                // if the file contains an image then send to IOstream
                else if(contentType.contains("image")) 
                {  try
                        {
                    File file = new File(path);
                    int fileLength = (int) file.length();
                    FileInputStream inputStream = new FileInputStream(file);
                    
                    byte allBytes[] = new byte[fileLength];
                    
                    inputStream.read(allBytes);
                    os.write(allBytes);
                        } //end try
                        
                        catch (FileNotFoundException fnfe) 
                        {
                    System.err.println("ERROR: Image not found at: " + path);
                        } //end catch
                } //end else if
                
                  else 
                  {
                        write404Content(os,path);
                } //end else
        } //end writeContent method
        
        /**
        * A Simple method to put into OutputStream the ERRORCODE 404 content
        * @param os is the OutputStream object to write to
        * @param path is the path of the object being requested
        **/
        private void write404Content(OutputStream os, String path) throws Exception
        {
            os.write("<h1><b>404: Not Found</b></h1>\n".getBytes());
            os.write("The page you are looking for does not exist!\n".getBytes());
            os.write("Unable to locate:".getBytes());
            os.write(path.getBytes());
        }

}
 
