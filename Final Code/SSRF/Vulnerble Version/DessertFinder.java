import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DessertFinder
{

	public static void main(String[] args) throws Exception 
	{
		//creates a new HTTP server
		//https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/package-summary.html
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8000), 0);

		//creates context for home page
		server.createContext( "/", DessertFinder::showHomePage);

		//creates context for searching
		server.createContext("/search", DessertFinder::searchShops);

		//print to terminal to show it's running
		System.out.println("DessertFinder running at http://127.0.0.1:8000");

		server.start();
	}

	private static void showHomePage(HttpExchange exchange) throws IOException 
	{

		//html code that will be displayed in browser
		String html =
		"<!DOCTYPE html>" +
		"<html>" +
		"<head>" +
		"<title>DessertFinder</title>" +
		"<style>" +
		"body { font-family:Arial Rounded MT Bold, sans-serif; margin: 40px; }" +
		"input { padding: 8px 15px; margin: 5px; border-radius: 10px; border: 1px solid #aaa; box-sizing: border-box; }" +
		"button { padding: 8px 15px; border-radius: 10px; border: 1px solid #aaa; box-sizing: border-box; }" +
		"</style>" +
		"</head>" +
		"<body>" +
		"<h1>DESSERT FINDER 🍰</h1>" +
		"<p>Find dessert shops near you!</p>" +
		"<form action='/search' method='GET'>" +
		"<label>Suburb:</label>" +
		"<input type='text' name='suburb' value='Bentley'>" +
		"<input type='hidden' name='url' " +
		"value='http://127.0.0.1:7000/shops'>" +
		"<button type='submit'>Find Dessert Shops</button>" +
		"</form>" +
		"</body>" +
		"</html>";

		sendResponse(exchange, html);
	}

	//handles requests send to /search endpoint
	private static void searchShops(HttpExchange exchange) throws IOException 
	{
		//gets query request from URL
		String query = exchange.getRequestURI().getQuery();
		String suburb = getQueryData(query, "suburb");

		//checks if suburb ioput is missing -> perth is default
		if (suburb == null || suburb.isEmpty()) 
		{
			suburb = "Perth";
		}

		//gets url val from query string
		String url = getQueryData(query, "url");

		//if url is null or empty, default server destination
		if (url == null || url.isEmpty()) 
		{
			url = "http://127.0.0.1:7000/shops";
		}

		//creates URL that DessertFinder will request
		String remoteUrl = url
						+ "?suburb="
						+ URLEncoder.encode(suburb, StandardCharsets.UTF_8);

		//useful to see which server is being requested
		System.out.println("[DessertFinder] Fetching remote resource: " + remoteUrl);

		//makes the server request to the URl 
		String result = fetchUrl(remoteUrl);

		//html page that displays search result
		String html =
		"<!DOCTYPE html>" +
		"<html>" +
		"<head>" +
		"<title>DessertFinder Results</title>" +
		"</head>" +
		"<body>" +
		"<h1>Dessert shops near " + 
		cleanHtml(suburb) +
		"</h1>" +
		"<pre>" +
		cleanHtml(result) +
		"</pre>" +
		"<a href='/'>Search again</a>" +
		"</body>" +
		"</html>";

		sendResponse(exchange, html);
	}
	
	

	//makes server-side request
	private static String fetchUrl(String url) throws IOException 
	{
		//url object made with inpput
		URL targetUrl = new URL(url);

		//opens connection to url for DessertFiner  to communicate with server
		URLConnection connection = targetUrl.openConnection();

		//buffered reader to read response from server
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		//stores response from server
		StringBuilder response = new StringBuilder();
		String line;

		//reads response one line at a time
		while ((line = reader.readLine()) != null) 
		{
			response.append(line);
			response.append("\n");
		}
		reader.close();

		return response.toString();
	}

	//gets specific data from url query
	private static String getQueryData(String query, String parameterName) 
	{
		//null when theres no parameter to get
		if (query == null) 
		{
			return null;
		}

		//splits query string at &
		String[] parameters = query.split("&");

		for (String parameter : parameters) 
		{
			//splits at = , turns into a key and a value
			String[] pair = parameter.split("=", 2);

			if (pair.length == 2) 
			{
				String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
				String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);

				if (key.equals(parameterName)) 
				{
					return value;
				}
			}	
		}

		return null;
	}

	//sends http response back to web browser
	private static void sendResponse(HttpExchange exchange, String response) throws IOException 
	{
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		exchange.sendResponseHeaders(200, responseBytes.length);

		try (OutputStream output = exchange.getResponseBody()) 
		{

			output.write(responseBytes);
		}
	}

	//converts html characters into safe text before data is displayed
	private static String cleanHtml(String text) 
	{

		StringBuilder escaped = new StringBuilder();

		for (char character : text.toCharArray())
		{
			switch (character) 
			{
				case '&':
				escaped.append("&amp;");
				break;

				case '<':
				escaped.append("&lt;");
				break;

				case '>':
				escaped.append("&gt;");
				break;

				case '"':
				escaped.append("&quot;");
				break;

				case '\'':
				escaped.append("&#39;");
				break;

				default:
				escaped.append(character);
				break;
			}
		}
		return escaped.toString();
	}
}