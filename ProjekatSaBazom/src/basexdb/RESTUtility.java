package basexdb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;
/**
 * Klasa demonstrira upotrebu REST API-a BaseX XML baze podataka.
 * Sadrzi set reusable CRUD operacija, sa primerom njihove upotrebe. 
 * 
 * @author Igor Cverdelj-Fogarasi
 *
 */
public class RESTUtility {
	
	public static int createSchema(String databaseUrl) throws Exception {
		System.out.println("=== PUT: create a new database: " + databaseUrl + " ===");
		URL url = new URL(databaseUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(RequestMethod.PUT);
		int responseCode = printResponse(conn);
		conn.disconnect();
		return responseCode;
	}
	
	public static int createResource(String databaseUrl, String resourceId, InputStream resource) throws Exception {
		System.out.println("=== PUT: create a new resource: " + resourceId + " in database: " + databaseUrl + " ===");
		URL url = new URL(databaseUrl + "/" + resourceId);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(RequestMethod.PUT);
		
		/* Preuzimanje output stream-a iz otvorene konekcije */
		OutputStream out = new BufferedOutputStream(conn.getOutputStream());

		/* Slanje podataka kroz stream */
		System.out.println("\n* Send document...");
		IOUtils.copy(resource, out);
		IOUtils.closeQuietly(resource);
		IOUtils.closeQuietly(out);
		
		int responseCode = printResponse(conn);
		conn.disconnect();
		return responseCode;
	}
	
	public static int updateResource(String databaseUrl, String resourceId, InputStream resource) throws Exception {
		return createResource(databaseUrl, resourceId, resource);
	}
	
	public static InputStream retrieveResource(String query, String databaseUrl, String method) throws Exception {
		if (method == RequestMethod.GET)
			return retrieveResource(query, databaseUrl, "UTF-8", true);
		else if (method == RequestMethod.POST)
			return retrieveResourcePost(query, databaseUrl);
		return null;
	}
	
	public static InputStream retrieveResourcePost(String xquery, String databaseUrl) throws Exception {
		System.out.println("=== POST: execute a query ===");
		URL url = new URL(databaseUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(RequestMethod.POST);
		conn.setRequestProperty("Content-Type", "application/query+xml");
		OutputStream out = conn.getOutputStream();
		out.write(xquery.getBytes("UTF-8"));
		out.close();

		/* Response kod vracen od strane servera */
		int responseCode = printResponse(conn);

		/* Ako je sve proslo kako treba... */
		if (responseCode == HttpURLConnection.HTTP_OK) 
			return conn.getInputStream();
		
		conn.disconnect();
		return null;
	}
	
	public static InputStream retrieveResource(String query, String databaseUrl, String encoding, boolean wrap) throws Exception {
		System.out.println("=== GET: execute a query: " + query + " ===");
		
		StringBuilder builder = new StringBuilder(databaseUrl);
		builder.append("?query=");
		builder.append(query.replace(" ", "+"));
		builder.append("&encoding=");
		builder.append(encoding);
		//if (wrap) builder.append("&wrap=yes");

		URL url = new URL(builder.substring(0));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		/* Response kod vracen od strane servera */
		int responseCode = printResponse(conn);

		/* Ako je sve proslo kako treba... */
		if (responseCode == HttpURLConnection.HTTP_OK) 
			return conn.getInputStream();
		
		conn.disconnect();
		return null;
	}

	public static int deleteResource(String databaseUrl, String resourceId) throws Exception {
		if (resourceId != null && !resourceId.equals(""))
			System.out.println("=== DELETE: delete resource: " + resourceId + " from database: " + databaseUrl + " ===");
		else 
			System.out.println("=== DELETE: delete existing database: " + databaseUrl + " ===");
		
		URL url = new URL(databaseUrl + "/" + resourceId);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(RequestMethod.DELETE);
		int responseCode = printResponse(conn);
		conn.disconnect();
		return responseCode;
	}
	
	public static int dropSchema(String databaseUrl) throws Exception {
		return deleteResource(databaseUrl, "");
	}

	public static int printResponse(HttpURLConnection conn) throws Exception {
		int responseCode = conn.getResponseCode();
		String message = conn.getResponseMessage();
		System.out.println("\n* HTTP response: " + responseCode + " (" + message + ')');
		return responseCode;
	}
	
	public static void printStream(InputStream input) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		for (String line; (line = br.readLine()) != null;) {
			System.out.println(line);
		}
	}
	
	public static boolean isRunning(String databaseUrl){
		try {
			((HttpURLConnection) new URL(databaseUrl).openConnection()).getResponseCode();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String getDatabaseUrl(String schemaUrl){
		String[] parts=schemaUrl.split("/");
		String url="";
		for(int i=0;i<parts.length-1;i++){
			url+=parts[i]+"/";
		}
		url=url.substring(0, url.length()-1);
		return url;
	}
	
	public static String getDatabaseName(String schemaUrl){
		String[] parts=schemaUrl.split("/");
		return parts[parts.length-1];
	}

}
