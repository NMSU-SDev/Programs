package edu.nmsu.cs.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

	private ServerSocket serverSocket;

	public void start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Listening on port " + port + "...");
		while (true) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection from " + clientSocket.getInetAddress());
			Thread t = new Thread(() -> {
				try {
					handleRequest(clientSocket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			t.start();
		}
	}

	public void stop() throws IOException {
		serverSocket.close();
	}

	private void handleRequest(Socket clientSocket) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader("index.html"))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			String response = sb.toString();
			response = response.replace("{{serverName}}", "My Web Server");

			OutputStream out = clientSocket.getOutputStream();
			out.write("HTTP/1.1 200 OK\n".getBytes());
			out.write("Content-Type: text/html\n".getBytes());
			out.write(("Content-Length: " + response.length() + "\n").getBytes());
			out.write("\n".getBytes());
			out.write(response.getBytes());
			out.flush();
		} catch (IOException e) {
			OutputStream out = clientSocket.getOutputStream();
			out.write("HTTP/1.1 404 Not Found\n".getBytes());
			out.write("Content-Type: text/plain\n".getBytes());
			out.write("\n".getBytes());
			out.write("404 Not Found".getBytes());
			out.flush();
		} finally {
			clientSocket.close();
		}
	}

	public static void main(String[] args) throws IOException {
		WebServer server = new WebServer();
		server.start(8080);
	}
}
