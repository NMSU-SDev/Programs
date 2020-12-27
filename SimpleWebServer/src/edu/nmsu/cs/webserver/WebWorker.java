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
 *
 **/

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;


public class WebWorker implements Runnable {
    private Socket socket;

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
            String fileReq;
            fileReq = readHTTPRequest(is);
            writeHTTPHeader(os, "text/html", fileReq);
            if (fileReq.equals("")) {
                writeContent(os);
            } else {
                try {
                    FileReader input = new FileReader(fileReq);
                    Scanner scan = new Scanner(input);
                    String file = "";
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDateTime now = LocalDateTime.now();
                    String date = dtf.format(now);
                    while (scan.hasNextLine()) {
                        file = scan.nextLine();
                        file = file.replaceAll("<cs371date>", date);
                        file = file.replaceAll("<cs371server>", "Devin's Server");
                        os.write(file.getBytes());
                    }
                    scan.close();
                } catch (Exception e) {
                    System.err.println("Output error: " + e);
                }
            }
            os.flush();
            socket.close();
        } catch (Exception e) {
            System.err.println("Output error: " + e);
        }
        System.err.println("Done handling connection.");
    }

    /**
     * Read the HTTP request header.
     **/
    private String readHTTPRequest(InputStream is) {

        boolean GETdefault = false;
        String fileReq = null;
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                while (!r.ready())
                    Thread.sleep(1);
                line = r.readLine();
                System.err.println("Request line: (" + line + ")");
                if (line.contains("GET")) {
                    fileReq = "./" + line.substring(5, line.length() - 9); // code was developed in part by Dr. Toups
                    System.out.println(fileReq);
                }
                if (line.length() == 0)
                    break;
            } catch (Exception e) {
                System.err.println("Request error: " + e);
                break;
            }
        }
        return fileReq;
    }

    /**
     * Write the HTTP header lines to the client network connection.
     *
     * @param os
     *          is the OutputStream object to write to
     * @param contentType
     *          is the string MIME content type (e.g. "text/html")
     **/
    private void writeHTTPHeader(OutputStream os, String contentType, String fileReq) throws Exception {
        boolean err404 = false;
        Date d = new Date();
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            FileReader file = new FileReader(fileReq);
        }
        catch (Exception e) {
            os.write("HTTP/1.0 404 Not Found\n".getBytes());
            err404 = true;
        }
        if (!err404){
            os.write("HTTP/1.1 200 OK\n".getBytes()); //if it is valid show this or else 404 not found
            File filepath = new File (fileReq);
            contentType = Files.probeContentType(filepath.toPath()); //
            os.write("Date: ".getBytes());
            os.write((df.format(d)).getBytes());
            os.write("\n".getBytes());
            os.write("Server: Devin's server\n".getBytes());
            os.write("Connection: close\n".getBytes());
            os.write("Content-Type: ".getBytes());
            os.write(contentType.getBytes());
            os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
            }
    }

    /**
     * Write the data content to the client network connection. This MUST be done after the HTTP
     * header has been written out.
     *
     * @param os
     *          is the OutputStream object to write to
     **/
    private void writeContent(OutputStream os) throws Exception {

        os.write("<html><head></head><body>\n".getBytes());
        os.write("<h3>My web server works!</h3>\n".getBytes());
        os.write("<p> Today's date is: <cs371date> The server is <cs731server>. </p>".getBytes());
        os.write("</body></html>\n".getBytes());
    }

} // end class
