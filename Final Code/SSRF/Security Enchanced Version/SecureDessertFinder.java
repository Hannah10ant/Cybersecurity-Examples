import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant; //[H]
import java.util.UUID;//[I]
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SecureDessertFinder
{
    //Security, destination URL is now hardcoded and not user controlled, preventing SSRF attacks
    private static final String SHOP_SERVER_URL = "http://127.0.0.1:7000/shops";

	public static void main(String[] args) throws Exception 
	{
		//creates a new HTTP server
		//https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/package-summary.html
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8000), 0);

		//creates context for home page
		server.createContext( "/", SecureDessertFinder::showHomePage);

		//creates context for searching
		server.createContext("/search", SecureDessertFinder::searchShops);

		//print to terminal to show it's running
		System.out.println("SecureDessertFinder running at http://127.0.0.1:8000");

		server.start();
	}

	private static void showHomePage(HttpExchange exchange) throws IOException 
	{

		//html code that will be displayed in browser
		String html =
		"<!DOCTYPE html>" +
		"<html>" +
		"<head>" +
		"<title>SecureDessertFinder</title>" +
		"<style>" +
		"body { font-family:Arial Rounded MT Bold, sans-serif; margin: 40px; }" +
		"input { padding: 8px 15px; margin: 5px; border-radius: 10px; border: 1px solid #aaa; box-sizing: border-box; }" +
		"button { padding: 8px 15px; border-radius: 10px; border: 1px solid #aaa; box-sizing: border-box; }" +
		"</style>" +
		"</head>" +
		"<body>" +
		"<h1>SECURE DESSERT FINDER 🍰</h1>" +
		"<p>Find dessert shops near you!</p>" +
		"<form action='/search' method='GET'>" +
		"<label>Suburb:</label>" +
		"<input type='text' name='suburb' value='Bentley'>" +
		//Security, backend url isnt sent to the client anymore, preventing user manipulation
		"<button type='submit'>Find Dessert Shops</button>" +
		"</form>" +
		"</body>" +
		"</html>";

		sendResponse(exchange, html, 200);
	}

	//handles requests send to /search endpoint
	private static void searchShops(HttpExchange exchange) throws IOException 
	{
		String tracId = UUID.randomUUID().toString().substring(0,8); //[I]
		log("INFO", tracId, "Incoming request | method=" + exchange.getRequestMethod()+ "| uri="+exchange.getRequestURI());

		//gets query request from URL
		String query = exchange.getRequestURI().getQuery();
		String suburb = getQueryData(query, "suburb");

		//checks if suburb ioput is missing -> perth is default
		if (suburb == null || suburb.isEmpty()) 
		{
			suburb = "Perth";
		}

		//Security, checks wether user is attempting to supply a custom URL,
        // legit requests should only have suburb param
        String suppliedUrl = getQueryData(query, "url");
        log("INFO", tracId, "User supplied URL: " + suppliedUrl);

        if(suppliedUrl != null ) 
        {
            log("WARN", tracId, "Block request due to user-supplied URL: " + suppliedUrl);
            sendResponse(exchange, "<h1>Invalid request: User-supplied URL is not allowed.</h1>"
                                    +"<p>Server destination cant be reached.</p>", 400);
            return;
        }
        //Security, use the hardcoded SHOP_SERVER_URL instead of user-supplied URL
        String remoteUrl = SHOP_SERVER_URL + "?suburb=" + URLEncoder.encode(suburb, StandardCharsets.UTF_8);

		log(
			"TRACE",
			tracId,
			"Preparing outbound request=" + remoteUrl
		);
		//useful to see which server is being requested
		System.out.println("[DessertFinder] Fetching remote resource: " + remoteUrl);

		//Security, only server controlled ShopServer url passed to fetchUrl
		String result = fetchUrl(remoteUrl, tracId);

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

		sendResponse(exchange, html, 200);
	}
	
	

	//makes server-side request
	private static String fetchUrl(String url, String traceId) throws IOException 
	{
		//url object made with inpput
		URL targetUrl = new URL(url);

        if(!targetUrl.getHost().equals("127.0.0.1")
        || targetUrl.getPort() != 7000
        || !targetUrl.getPath().equals("/shops"))
        {
            log("SECURITY-WARN", traceId, "Blocked request to unexpected server destination: " + url);
            throw new IOException("Blocked request to unexpected server destination: " + url);
        }

		//opens connection to url for DessertFiner  to communicate with server
		URLConnection connection = targetUrl.openConnection();

		//pass same trace id to beackend serever for consistent logs matching
		connection.setRequestProperty("X-Trace-Id", traceId); //[K]
		log("TRACE", traceId, "Outbound request sent to remote server=" + targetUrl);

		//buffered reader to read response from server
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		log("TRACE", traceId, "remote server connection established=" + targetUrl);

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
		log("TRACE", traceId, "Response received from remote server=" + targetUrl);

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
	private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException 
	{
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		exchange.sendResponseHeaders(statusCode, responseBytes.length);

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
			Files.writeString( //[L]
				Path.of("SecureDessertFinderLogs.txt"),
				logEntry,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		}
		catch (IOException e)
		{
			System.err.println("Could not write DessertFinder log: " + e.getMessage());
		}
	}
}