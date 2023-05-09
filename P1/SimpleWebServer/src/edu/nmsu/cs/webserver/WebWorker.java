package edu.nmsu.cs.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor, the object executes on its "run" method, and leaves
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
public class WebWorker implements Runnable {
    private Socket socket;
    private String requestedFile;
    
	/**
	 * Constructor: must have a valid open socket
	 **/
    public WebWorker(Socket s) {
        socket = s;
    }

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
    public void run() {
        System.err.println("Handling connection...");
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            readHTTPRequest(is); // Read HTTP request
            if (requestedFile != null) { // Check if the requested file exists
                File file = new File(requestedFile);
                if (file.exists() && !file.isDirectory()) { // Checks if requested file exists and isn't a directory
                    String contentType = Files.probeContentType(Paths.get(requestedFile)); // Get content type of requested file
                    writeHTTPHeader(os, contentType); // Call writeHTTPHeader
                    writeContent(os, file, contentType); // Call writeContent
                } 
                else { // Else return a not found error
                    writeHTTPHeader(os, "text/html");
                    os.write("<html><head></head><body>\n".getBytes());
                    os.write("<h3>404 Not Found</h3>\n".getBytes());
                    os.write("</body></html>\n".getBytes());
                }
            } 
            else { // Write HTTP header for a bad request
                writeHTTPHeader(os, "text/html");
                os.write("<html><head></head><body>\n".getBytes());
                os.write("<h3>Bad Request</h3>\n".getBytes());
                os.write("</body></html>\n".getBytes());
            }
            os.flush(); // Flush the output stream and close the socket connection
            socket.close();
        } 
        catch (Exception e) {
            System.err.println("Output error: " + e);
        }
        System.err.println("Done handling connection.");
        return;
    }

	/**
	 * Read the HTTP request header.
	 **/
    private void readHTTPRequest(InputStream is) { // Begin method to read HTTP request
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Create BufferedReader to read input stream (HTTP request)
        while (true) { // Read all lines of request
            try {
                while (!br.ready()) { // Wait for BufferedReader
                    Thread.sleep(1);
                }
                line = br.readLine(); // Read line from input
                System.err.println("Request line: (" + line + ")");
                if (line.startsWith("GET")) { // Check if line starts with GET request
                    String[] pieces = line.split(" ");
                    if (pieces.length >= 2) { // Check if there are at least two pieces (GET request and requested file path)
                        requestedFile = "www/" + pieces[1].substring(1); // Updated requested file path to include "www/" prefix
                    }
                } 
                if (line.length() == 0) { // If an empty line is encountered, exit loop
                    break;
                }
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
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
    private void writeHTTPHeader(OutputStream os, String contentType) throws Exception {
        Date d = new Date();
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("MST"));
        os.write("HTTP/1.1 200 OK\n".getBytes());
        os.write("Date: ".getBytes());
        os.write((df.format(d)).getBytes());
        os.write("\n".getBytes());
        os.write("Server: Jonas' very own server\n".getBytes());
        os.write("Connection: close\n".getBytes());
        os.write("Content-Type: ".getBytes());
        os.write(contentType.getBytes());
        os.write("\n\n".getBytes());

        return;
    }
    
	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
    private void writeContent(OutputStream os, File file, String contentType) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            if (contentType.startsWith("text/")) { // Check if content type starts with "text/"
                BufferedReader br = new BufferedReader(new InputStreamReader(fis)); // Create BufferedReader to read file
                String line;
                while ((line = br.readLine()) != null) { // Read each line of file
                    if (line.contains("<cs371date>")) {
                        SimpleDateFormat date = new SimpleDateFormat("MMMM dd, yyyy");
                        date.setTimeZone(TimeZone.getTimeZone("MST"));
                        String todaysDate = date.format(new Date());
                        line = line.replace("<cs371date>", todaysDate);
                    }
                    if (line.contains("<cs371server>")) {
                        line = line.replace("<cs371server>", "Jonas' server");
                    }
                    os.write(line.getBytes()); // Write modified line to output stream
                }
            }
            else if (contentType.startsWith("image/")) { // Check if content type starts with "image/"
                byte[] buffer = new byte[4096]; // Create buffer to read binary data from image file
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) { // Read binary data and write it to output
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
    }
}