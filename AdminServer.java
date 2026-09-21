import com.sun.net.httpserver.*;

import java.io.*;

import java.net.*;

import java.nio.charset.StandardCharsets;

public class AdminServer {

	public static void main(String[] args) throws Exception {

		//new http server
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 7001), 0);

		server.createContext("/admin", AdminServer::showAdminData);

		System.out.println("Admin Server running at http://127.0.0.1:7001");

		server.start();
	}

	private static void showAdminData(HttpExchange exchange) throws IOException {

		System.out.println(
		"[AdminServer] *** REQUEST RECEIVED *** "
		+ exchange.getRequestMethod()
		+ " "
		+ exchange.getRequestURI()
		);

		String response =
		"***************************\n" +
		"*       ADMIN SERVER      *\n" +
		"***************************\n" +
		"Environment: Restricted\n" +
		"Access Level: Administrator\n" +
		"Confidential Data: TRUE\n";

		exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

		exchange.sendResponseHeaders(200, responseBytes.length);

		try (OutputStream output = exchange.getResponseBody()) {

		output.write(responseBytes);
		}
	}
}
