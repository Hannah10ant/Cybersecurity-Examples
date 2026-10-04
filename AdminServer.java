import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;//[H]

public class AdminServer 
{
	public static void main(String[] args) throws Exception 
	{
		//new http server
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 7001), 0);
		server.createContext("/admin", AdminServer::showAdminData);
		System.out.println("Admin Server running at http://127.0.0.1:7001");

		server.start();
	}

	private static void showAdminData(HttpExchange exchange) throws IOException 
	{
		String traceId =
			exchange.getRequestHeaders().getFirst("X-Trace-ID");  // [J]

		if (traceId == null)
		{
			traceId = "none";
		}

		log(
			"SECURITY",
			traceId,
			"Restricted AdminServer accessed"
			+ " | method=" + exchange.getRequestMethod()
			+ " | uri=" + exchange.getRequestURI()
		);

		System.out.println(
		"[AdminServer] *** REQUEST RECEIVED *** "
		+ exchange.getRequestMethod()
		+ " "
		+ exchange.getRequestURI()
		);

		//Simualtes restricted internall service containing info that shouldn't be 
		//normally returned through the public DessertFinder application
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

		try (OutputStream output = exchange.getResponseBody()) 
		{
			output.write(responseBytes);
		}
	}
		private static void log(String level, String traceId, String message)
	{
		String logEntry =
			Instant.now()
			+ " [" + level + "]"
			+ " [trace=" + traceId + "] "
			+ message+ "\n";

		System.out.print(logEntry);
		
		try
		{
			Files.writeString(//[L]
				Path.of("AdminLogs.txt"),
				logEntry,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		}
		catch (IOException e)
		{
			System.err.println("Could not write Admin log: " + e.getMessage());
		}
	}
}
