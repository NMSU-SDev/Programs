package edu.nmsu.cs.webserver;

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;

public class WebWorker implements Runnable{

   private Socket socket;

   /**
   * Constructor: must have a valid open socket
   **/
   public WebWorker(Socket s){
   
      socket = s;
   }

   /**
   * Worker thread starting point. Each worker handles just one HTTP 
   * request and then returns, which destroys the thread. This method
   * assumes that whoever created the worker created it with a valid
   * open socket object.
   **/
   
   public void run(){
   
      // website address 
      String address = "";
      
      System.err.println("Handling connection...");
      
      try {
         InputStream  is = socket.getInputStream();
         OutputStream os = socket.getOutputStream();
         
         address = readHTTPRequest(is);
         
         // send the address to the URL
         writeHTTPHeader(os,"text/html", address);
         writeContent(os,"text/html", address);
         os.flush();
         socket.close();
      
      } catch (Exception e) {
         System.err.println("Output error: "+e);
      }
      
      System.err.println("Done handling connection.");
      
      return;
      
   } // of run

   /**
   * Read the HTTP request header.
   * @return URL String 
   **/
   private String readHTTPRequest(InputStream is){
      
      String line;
      BufferedReader r = new BufferedReader(new InputStreamReader(is));
      
      String address = "";
      
      while (true) {
         try {
            while (!r.ready()) Thread.sleep(1);
            line = r.readLine();
            
            if(line.contains("GET ")){
               address = line.substring(4);
               for(int i = 0; i < address.length(); i++){
                  if(address.charAt(i) == ' '){
                     address = address.substring(0, i);
                  } // of if 
               } // of for 
            } // of if 
            
            System.err.println("Request line: ("+ line +")");
            if (line.length() == 0) break;
         
         } catch (Exception e) {
            System.err.println("Request error: " + e);
            break;
         }
      }
      return address;
      
   } // of readHTTPRequest

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
      
      String copy = "." + address;
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
      os.write("Server: John's very own server\n".getBytes());
      os.write("Connection: close\n".getBytes());
      os.write("Content-Type: ".getBytes());
      os.write(contentType.getBytes());
      os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
      return;
      
   } // of writeHTTPHeader

   /**
   * Write the data content to the client network connection. This MUST
   * be done after the HTTP header has been written out.
   * @param os is the OutputStream object to write to
   * @param contentType 
   * @param address is website address
   **/
   private void writeContent(OutputStream os, String contentType, String address) throws Exception{
      
      // date info
      Date d = new Date();
      DateFormat dformat = DateFormat.getDateTimeInstance();
      dformat.setTimeZone(TimeZone.getTimeZone("GMT-6"));

	   // file contents and address copy
      String fcont = "";
      String copy = "." + address.substring(0, address.length());
      String date = dformat.format(d);
      File f1 = new File(copy);

      // put the file contents in a string.
	   // If "<cs371date>" is in the file write the date. 
      // If "<cs371server>"is  in the file write my id string. 
      try{
         FileReader fRead = new FileReader(f1);
         BufferedReader fBuff = new BufferedReader(fRead);
         
         while((fcont = fBuff.readLine()) != null) {
            os.write(fcont.getBytes());
            os.write("\n".getBytes());
            if (fcont.contains("<cs371date>")) {
               os.write(date.getBytes());
            } // of if
            
            if (fcont.contains("<cs371server>")){
               os.write("\nIt works kinda! \n".getBytes());
            }// of if
            
            if (address.equals("favicon.ico")) {
                FileInputStream fis = new FileInputStream(f1);
                byte[] buffer = new byte[1024];
                int bytes = 0;
                while ((bytes = fis.read(buffer)) != -1) {
                   os.write(buffer, 0, bytes);
                }
                fis.close();
             }
         } // of while
           
      }catch(FileNotFoundException e) {
         System.err.println("File not found: " + address);
         System.out.println("Working Directory = " + System.getProperty("user.dir"));
         os.write("<h1>Error: 404 Not found<h1>\n".getBytes());
      } // end try-catch
      
   }  // of writeContent

} // end class