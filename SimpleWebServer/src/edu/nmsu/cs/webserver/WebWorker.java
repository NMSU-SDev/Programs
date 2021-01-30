package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 * <p>
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 * <p>
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 *
 * @author Jon Cook, Ph.D.
 **/

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class WebWorker implements Runnable {

    private final Socket socket;

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
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            String file_path = readHTTPRequest(is);
            String http_code = writeHTTPHeader(os, "text/html", file_path);
            writeContent(os, http_code, file_path);
            os.flush();
            socket.close();
        } catch (Exception e) {
            System.err.println("Output error: " + e);
        }
        System.err.println("Done handling connection.");
    }

    /**
     * Read the HTTP request header.
     *
     * @return the string of the file path from the GET request if applicable
     **/
    private String readHTTPRequest(InputStream is) {
        String file_path = "/";
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                while (!r.ready())
                    Thread.sleep(1);
                line = r.readLine();

                // Split the request into items
                String[] line_items = line.split(" ");

                // Check if it's a GET request.
                if (line_items[0].equals("GET")) {
                    // if it is lets get the file path its requesting to return it
                    file_path = line_items[1];
                }

                System.err.println("Request line: (" + line + ")");
                if (line.length() == 0)
                    break;
            } catch (Exception e) {
                System.err.println("Request error: " + e);
                break;
            }
        }
        return "./" + file_path;
    }

    /**
     * Write the HTTP header lines to the client network connection.
     *
     * @param os          is the OutputStream object to write to
     * @param contentType is the string MIME content type (e.g. "text/html")
     * @param file_path   is the file path string from the GET request.
     * @return The string of the http response code
     **/
    private String writeHTTPHeader(OutputStream os, String contentType, String file_path) throws Exception {
        File file = new File("./" + file_path);
        String http_code = "200 OK";

        // check if it's a valid file
        if (!file.isDirectory() && file.exists()) {
            http_code = "200 OK";
            contentType = Files.probeContentType(file.toPath());
        } else {
            http_code = "404 Not Found";
        }
        Date d = new Date();
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        os.write(("HTTP/1.1 " + http_code + "\n").getBytes());
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
        return http_code;
    }

    /**
     * Write the data content to the client network connection. This MUST be done after the HTTP
     * header has been written out.
     *
     * @param os        is the OutputStream object to write to
     * @param file_path is the file path string from the GET request.
     * @param http_code is the http_code identified in the writeHttpHeader function
     **/
    private void writeContent(OutputStream os, String http_code, String file_path) throws Exception {
        File file = new File("./" + file_path);
        String contentType = Files.probeContentType(file.toPath());
        System.out.println(contentType.split("/")[1]);

        // 404 page render
        if (http_code.equals("404 Not Found")) {
            os.write("<html><head></head><body>\n".getBytes());
            os.write("<h3>404 Not Found</h3>\n".getBytes());
            os.write(" <img src=\"https://media1.tenor.com/images/d5d2b8703922df9d25da6aded6eaf2f3/tenor.gif?itemid=7666840\" alt=\"John Travolta\">\n".getBytes());
            os.write("</body></html>\n".getBytes());
        }

        // If it's not text lets serve it as a binary file and let the browser handle the rest.
        else if (!contentType.split("/")[0].equals("text")) {
            writeBinary(os, file_path);
        }

        // otherwise it must be a text file so let's serve it line by line as strings to bytes.
        else {
            writeText(os, file_path);
        }

    }

    /**
     * Used to serve binary non-text files to the browser.
     *
     * @param os        the output stream
     * @param file_path the path to the binary file
     * @throws IOException
     */
    private void writeBinary(OutputStream os, String file_path) throws IOException {
        InputStream istream = new FileInputStream(file_path);
        int byteRead;
        while ((byteRead = istream.read()) != -1) {
            os.write(byteRead);
        }
    }

    /**
     * Serves a text or html file to the browser.
     *
     * @param os        the output stream
     * @param file_path the file path to the text file
     */
    private void writeText(OutputStream os, String file_path) {

        // Get the current date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        // render the file from the path
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file_path));
            String line = reader.readLine();
            while (line != null) {
                // read next line
                line = reader.readLine();
                if (line == null) {
                    break;
                }

                // Replace all our tags with the respective info.
                line = line.replaceAll("<cs371server>", "This is Ziad's server");
                line = line.replaceAll("<cs371date>", date);
                os.write(line.getBytes());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
