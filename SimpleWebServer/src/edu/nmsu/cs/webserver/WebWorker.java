package edu.nmsu.cs.webserver;

/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection. 
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring 
* the fact that the entirety of the webserver execution might be handling
* other clients, too. 
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format). 
*
**/

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.TimeZone;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.net.InetAddress;

public class WebWorker implements Runnable {

   private Socket socket;

   public WebWorker(Socket s) {
       socket = s;
   }

   public void run() {
    System.err.println("Handling connection...");
    try {
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();

        // Get file path from HTTP request
        String filePath = readHTTPRequest(is);

        // Check if file exists, and write content or a 404 error accordingly
        File file = new File("." + filePath);
        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(filePath);
            writeHTTPHeader(os, contentType);

            if (contentType.startsWith("image/")) {
                writeBinaryContent(os, file);
            } else {
                writeContent(os, file);
            }
        } else {
            writeHTTPHeader(os, "text/html");
            os.write("<h1>404 NOT FOUND</h1>\n\n".getBytes());
        }
        os.flush();
        socket.close();
    } catch (Exception e) {
        System.err.println("Output error: " + e);
    }
    System.err.println("Done handling connection.");
}



   private String readHTTPRequest(InputStream is) {
       String line;
       BufferedReader r = new BufferedReader(new InputStreamReader(is));
       String filePath = "";
       try {
         // Wait until there is data to be read from the input stream
           while (!r.ready()) {
               Thread.sleep(1);
           }

            // Read the first line of the request to determine the HTTP method and file path
           line = r.readLine();
           System.err.println("Request line: (" + line + ")");
           if (line.startsWith("GET")) {
               String[] parts = line.split(" ");
               if (parts.length > 1) {
                   filePath = parts[1];
               }
           }
           while (line.length() != 0) {
               line = r.readLine();
           }
       } catch (Exception e) {
           System.err.println("Request error: " + e);
       }
       return filePath;
   }

   private String getContentType(String filePath) {
      // Determine the content type based on the file extension
       String contentType = "text/plain";
       if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
           contentType = "text/html";
       } else if (filePath.endsWith(".gif")) {
           contentType = "image/gif";
       } else if (filePath.endsWith(".jpeg") || filePath.endsWith(".jpg")) {
           contentType = "image/jpeg";
       } else if (filePath.endsWith(".png")) {
           contentType = "image/png";
       } else if (filePath.endsWith(".ico")) { // line to handle favicon.ico files
        contentType = "image/x-icon";
    }
       return contentType;
   }

   private void writeHTTPHeader(OutputStream os, String contentType) throws Exception {

      // Write the HTTP response headers
       Date d = new Date();
       DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
       df.setTimeZone(TimeZone.getTimeZone("GMT"));

       // Write the HTTP response status and other important information to the output stream
       os.write("HTTP/1.1 200 OK\n".getBytes());
       os.write("Date: ".getBytes());
       os.write((df.format(d)).getBytes());
       os.write("\n".getBytes());
       os.write("Server: My Web Server\n".getBytes());
       os.write("Connection: close\n".getBytes());
       os.write("Content-Type: ".getBytes());
       os.write(contentType.getBytes());
       os.write("\n\n".getBytes());
   }

   /**
* Writes the contents of a file to the output stream, replacing certain tags with dynamic
* content as specified in the assignment. Specifically, replaces <cs371date> with the 
* current date, and <cs371server> with the server identification string.
*
* @param os the OutputStream to write the content to
* @param file the File object representing the file to be read
* @throws Exception if there is an error reading the file or writing to the output stream
*/

   private void writeContent(OutputStream os, File file) throws Exception {
      try {
          FileInputStream fis = new FileInputStream(file);
          BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
          String line;
          while ((line = reader.readLine()) != null) {

            // Replace the <cs371date> tag with the current date
              if (line.contains("<cs371date>")) {
                  DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
                  String currentDate = df.format(new Date());
                  line = line.replace("<cs371date>", currentDate);
                  // Replace the <cs371server> tag with the server identification string
              } if (line.contains("<cs371server>")) {
                  line = line.replace("<cs371server>", "Christian's Server");
              }
                 // Write the modified line to the output stream
              os.write(line.getBytes());
              os.write("\n".getBytes());
          }
          fis.close();
      } catch (IOException e) {
          System.err.println("Error reading file: " + e);
      }
  }

  private void writeBinaryContent(OutputStream os, File file) throws Exception {
    try {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        fis.close();
    } catch (IOException e) {
        System.err.println("Error reading file: " + e);
    }
}

  
}//end class
