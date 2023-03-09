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
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WebWorker implements Runnable {

   private Socket socket;

   // added new global variables to keep method signatures the same
   // but use certain variables accross class
   private boolean fileFound = true;
   private File fileReq;
   private String fileType;
   private String cs371server = "Rafa's web server";
   private Date cs371date;
   private DateFormat df;

   /**
    * Constructor: must have a valid open socket
    **/
   public WebWorker(Socket s) {
      socket = s;
   }

   /**
    * Worker thread starting point. Each worker handles just one HTTP request and
    * then returns, which
    * destroys the thread. This method assumes that whoever created the worker
    * created it with a
    * valid open socket object.
    **/
   public void run() {
      System.err.println("Handling connection...");
      try {
         InputStream is = socket.getInputStream();
         OutputStream os = socket.getOutputStream();
         // determine availability of file
         readHTTPRequest(is);

         // response building
         writeHTTPHeader(os, fileType);

         // content of response
         if (!fileFound)
            write404Content(os);
         else {
            if (fileType.contains("text") || fileReq.getName().isEmpty())
               writeTextContent(os);
            else if (fileType.contains("image")) {
               writeImageContent(os);
            }
         }
         os.flush();
         socket.close();
      } catch (Exception e) {
         System.err.println("Output error: " + e);
      }
      System.err.println("Done handling connection.");
      return;
   }

   /**
    * Read the HTTP request header.
    **/
   private void readHTTPRequest(InputStream is) {
      String line;
      BufferedReader r = new BufferedReader(new InputStreamReader(is));

      while (true) {
         try {
            while (!r.ready())
               Thread.sleep(1);

            String fileName;
            // take in request
            line = r.readLine();
            System.err.println("Request line: (" + line + ")");

            // parse GET line and obtain file info
            if (line.contains("GET")) {
               String getRequest[] = line.split(" ");

               // parse file name
               fileName = getRequest[1].substring(1);

               // obtain content type
               if (fileName.endsWith("html")) // text or
                  fileType = "text/html";

               else if (fileName.endsWith("gif"))
                  fileType = "image/gif";

               else if (fileName.endsWith("jpg"))
                  fileType = "image/jpeg";

               else if (fileName.endsWith("png"))
                  fileType = "image/png";

               else // file type unknown
                  fileType = "";

               // generate path of requested file
               System.err.println(fileName.isEmpty());
               fileReq = fileName.isEmpty() ? fileReq = new File(fileName) : new File("www/" + fileName);

               // determine availability
               if (!fileReq.exists() && !(fileReq.getName().isEmpty()))
                  fileFound = false;

               // debug prints
               System.err.println("Requested file name: " + fileName);
               System.err.println("Requested media type: " + fileType);
               System.err.println("Requested file found: " + fileFound);
               System.err.println("done " + (fileFound && fileReq.getName().isEmpty()));
               System.err.println(fileReq.getName());

            } // end GET parse

            if (line.length() == 0)
               break;
         } catch (Exception e) {
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
    *                    is the OutputStream object to write to
    * @param contentType
    *                    is the string MIME content type (e.g. "text/html")
    **/
   private void writeHTTPHeader(OutputStream os, String contentType) throws Exception {

      // generate date instance
      cs371date = new Date();
      df = DateFormat.getDateTimeInstance();
      df.setTimeZone(TimeZone.getTimeZone("MST"));

      // GET request found file
      if (fileFound)
         os.write("HTTP/1.1 200 OK\n".getBytes());

      // GET request couldn't find file
      else
      os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());
      
      // more header information
      os.write("Date: ".getBytes());
      os.write((df.format(cs371date)).getBytes());
      os.write("\n".getBytes());
      os.write("Server: ".getBytes());
      os.write((cs371server + "\n").getBytes());
      // os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
      // os.write("Content-Length: 438\n".getBytes());
      os.write("Connection: close\n".getBytes());
      os.write("Content-Type: ".getBytes());
      os.write(contentType.getBytes());
      os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
      
      if (!fileType.contains("image"))
         os.write("<link rel=\"shortcut icon\" type=\"image/png\" href=\"dying.png\"/>".getBytes());
      return;
   }

   /**
    * Write the data content to the client network connection. This MUST be done
    * after the HTTP
    * header has been written out.
    * 
    * @param os
    *           is the OutputStream object to write to
    **/
    private void writeTextContent(OutputStream os) throws Exception {
       // write default page
      
      System.err.println("done " + (fileFound && fileReq.getName().isEmpty()));
      if (fileFound && fileReq.getName().isEmpty()) {
         os.write("<html><head></head><body>\n".getBytes());
         os.write("<h3>My web server works!</h3>\n".getBytes());
         os.write("</body></html>\n".getBytes());
      }

      // write HTML content of file in response
      else if (fileFound) {
         BufferedReader br = new BufferedReader(new FileReader(fileReq));
         String fileLine = br.readLine();

         while (fileLine != null) {
            if (fileLine.contains("<cs371date>")) {
               fileLine = fileLine.replace("<cs371date>", df.format(cs371date));
            }
            if (fileLine.contains("<cs371server>")) {
               fileLine = fileLine.replace("<cs371server>", cs371server);
            }
            os.write((fileLine + '\n').getBytes());
            fileLine = br.readLine();
         }

         br.close();
      }

   } // end write content

   private void write404Content(OutputStream os) throws Exception {
      // write 404 page
      os.write("<html>\n".getBytes());
      os.write("<title>404 NOT FOUND\n</title>".getBytes());
      os.write("<head>\n".getBytes());
      os.write("</head>\n".getBytes());
      os.write("<body>\n".getBytes());
      os.write("<h1>Error 404</h1>\n".getBytes());
      os.write("<div>Rafa didn't find the page:(</div>\n".getBytes());
      os.write("</body>\n".getBytes());
      os.write("<head>\n".getBytes());
   }

   private void writeImageContent(OutputStream os) throws Exception {
      byte[] fileContent = Files.readAllBytes(fileReq.toPath());
      System.err.println("File : " + fileReq.toPath().toString());
      System.err.println("Concent byte []: " + fileContent);
      os.write(fileContent);
   }
}
// end class