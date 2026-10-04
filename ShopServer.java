import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant; //[H]

public class ShopServer 
{

	public static void main(String[] args) throws Exception 
	{
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 7000),0);
		server.createContext("/shops", ShopServer::showShopData);
		System.out.println("Shop Server running at http://127.0.0.1:7000");

		server.start();
	}

	private static void showShopData(HttpExchange exchange) throws IOException 
	{
		String query = exchange.getRequestURI().getQuery();
		String suburb = "Unknown";

		if (query != null) 
		{
			String[] parameters = query.split("&");
			for (String parameter : parameters) 
			{
				String[] pair = parameter.split("=", 2);
				if (pair.length == 2 && pair[0].equals("suburb")) 
				{
					suburb = URLDecoder.decode(pair[1],StandardCharsets.UTF_8);
				}
			}
		}

		String traceId =
			exchange.getRequestHeaders().getFirst("X-Trace-ID");  // [J]

		if (traceId == null)
		{
			traceId = "none";
		}

		log(
			"INFO", traceId, "ShopServer request"
			+ " | method=" + exchange.getRequestMethod()
			+ " | uri=" + exchange.getRequestURI()
		);

		String response;

		if (suburb.equalsIgnoreCase("Bentley")) 
		{
			response =
			"Yo-Chi | 2.1 km\n" +
			"Cuccini Gelato | 3.4 km\n" +
			"Gusto Gelato | 5.2 km";
		} 
		else if (suburb.equalsIgnoreCase("Baldivis")) 
		{
			response =
			"Get Chunky | 1.8 km\n" +
			"Homm Dessert | 2.7 km\n" +
			"Brown Spoon | 4.1 km";
		} 
		else if (suburb.equalsIgnoreCase("Armadale")) 
		{
			response =
			"Gelare | 2.0 km\n" +
			"Baskin Robbins | 3.2 km\n" +
			"San Churros | 4.6 km";
		} 
		else 
		{
			//Vulnerability, an invalid seach returns too much info including debugging info to user
			//this leaks Admin server address + gives an attacker an internal SSRF target
			response =
			"No dessert shops found for " + suburb + "\n\n DEBUG INFORMATION\nInternal Admin Service: http://127.0.0.1:7001/admin";
		}

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
			Instant.now() //[H]
			+ " [" + level + "]"
			+ " [trace=" + traceId + "] "
			+ message+ "\n";

		System.out.print(logEntry);
		
		try
		{
			Files.writeString( //[L]
				Path.of("ShopLogs.txt"),
				logEntry,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		}
		catch (IOException e)
		{
			System.err.println("Could not write Shop log: " + e.getMessage());
		}
	}
}
