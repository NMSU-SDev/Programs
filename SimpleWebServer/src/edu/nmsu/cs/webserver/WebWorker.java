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
    private void readHTTPRequest(InputStream is) {
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Create BufferedReader to read input stream
        while (true) {
            try { // Wait for BufferedReader to be ready
                while (!br.ready()) {
                    Thread.sleep(1);
                }
                line = br.readLine(); // Read a line from input stream
                System.err.println("Request line: (" + line + ")");
                if (line.startsWith("GET")) { // Check if line starts with a GET request
                    String[] pieces = line.split(" "); // Split line into pieces
                    if (pieces.length >= 2) {  // Check if there are at least two pieces (GET and requested file path)
                        requestedFile = pieces[1].substring(1);
                    }
                }
                if (line.length() == 0) { // Break loop when an empty string is parsed
                    break;
                }
            } 
            catch (Exception e) {
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
        try (FileInputStream fis = new FileInputStream(file)) { // Open requested file using a FileInputStream
            if (contentType.equals("text/html")) { // Check if content type is "text/html"
                BufferedReader br = new BufferedReader(new InputStreamReader(fis)); // Use BufferedReader to read file line by line
                String line;
                while ((line = br.readLine()) != null) { // Read and process each line of file
                    if (line.contains("<cs371date>")) { // Replace <cs371date> tag with current date in MST
                        SimpleDateFormat date = new SimpleDateFormat("MMMM dd, yyyy");
                        date.setTimeZone(TimeZone.getTimeZone("MST"));
                        String todaysDate = date.format(new Date());
                        line = line.replace("<cs371date>", todaysDate);
                    }
                    if (line.contains("<cs371server>")) { // Replace <cs371server> tag with "Jonas' server"
                        line = line.replace("<cs371server>", "Jonas' server");
                    }
                    os.write(line.getBytes()); // Write modified line to output stream
                }
            } 
        } 
        catch (IOException e) {
            System.err.println("Error reading file: " + e);
        }
    }
}
