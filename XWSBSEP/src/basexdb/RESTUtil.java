package basexdb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.basex.BaseXHTTP;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import util.DocumentTransform;
import basexdb.RequestMethod;

/**
 * Klasa demonstrira upotrebu REST API-a BaseX XML baze podataka.
 * Sadrzi set reusable CRUD operacija, sa primerom njihove upotrebe. 
 * 
 * @author Igor Cverdelj-Fogarasi
 *
 */
public class RESTUtil {

	public static final String REST_URL = ResourceBundle.getBundle("basex").getString("rest.url");

	public static void main(String[] args) throws Exception {

		BaseXHTTP http = null;
		if (!isRunning())
			http = new BaseXHTTP("-Uadmin", "-Padmin");

		dropSchema("firmaa");
		dropSchema("firmab");
		dropSchema("bankaa");
		dropSchema("bankab");

		createSchema("firmaa");
		createSchema("firmab");
		createSchema("bankaa");
		createSchema("bankab");

		DOMSource counterA = brojacPoslatihDocument("","rbrPoslateFakture", 0);
		DOMSource counterB = brojacPoslatihDocument("","rbrPoslateFakture", 0);
		
		
		DOMSource counterRcvdA = brojacPrimljenihDocument("","primljeneFakture","firmab", new Date(), 0);
		DOMSource counterRcvdB = brojacPrimljenihDocument("","primljeneFakture", "firmaa",  new Date(), 0);
		createResource("firmaa", "brojacPoslatihFaktura", DOM2InputStream(counterA));
		createResource("firmab", "brojacPoslatihFaktura", DOM2InputStream(counterB));
		createResource("firmaa", "brojacPrimljenihFaktura", DOM2InputStream(counterRcvdA));
		createResource("firmab", "brojacPrimljenihFaktura", DOM2InputStream(counterRcvdB));

		DOMSource accountA = createAccount("firmaa", "111111111111111111",
				"111111111", "100.00");
		DOMSource accountB = createAccount("firmab", "222222222222222222",
				"222222222", "100.00");
		createResource("bankaa", "firmaa", DOM2InputStream(accountA));
		createResource("bankab", "firmab", DOM2InputStream(accountB));

		if (http instanceof BaseXHTTP)
			http.stop();

	}

	public static int createSchema(String schemaName) throws Exception {
		System.out.println("=== PUT: create a new database: " + schemaName + " ===");
		URL url = new URL(REST_URL + schemaName);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(RequestMethod.PUT);
		int responseCode = printResponse(conn);
		conn.disconnect();
		return responseCode;
	}

	public static int createResource(String schemaName, String resourceId, InputStream resource) throws Exception {
		System.out.println("=== PUT: create a new resource: " + resourceId + " in database: " + schemaName + " ===");
		URL url = new URL(REST_URL + schemaName + "/" + resourceId);
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

	public static int updateResource(String schemaName, String resourceId, InputStream resource) throws Exception {
		return createResource(schemaName, resourceId, resource);
	}

	public static InputStream retrieveResource(String query, String schemaName, String method) throws Exception {
		if (method == RequestMethod.GET)
			return retrieveResource(query, schemaName, "UTF-8", true);
		else if (method == RequestMethod.POST)
			return retrieveResourcePost(query, schemaName);
		return null;
	}

	public static InputStream retrieveResourcePost(String xquery, String schemaName) throws Exception {
		System.out.println("=== POST: execute a query ===");
		URL url = new URL(REST_URL + schemaName);
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

	public static InputStream retrieveResource(String query,
			String schemaName, String encoding, boolean wrap) throws Exception {
		System.out.println("=== GET: execute a query: " + query + " ===");

		StringBuilder builder = new StringBuilder(REST_URL);
		builder.append(schemaName);
		builder.append(query);
		builder.append("?query=*");
		builder.append("&encoding=");
		builder.append(encoding);
		if (wrap)
			builder.append("&wrap=yes");

		System.out.println(builder.toString());
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

	public static int deleteResource(String schemaName, String resourceId) throws Exception {
		if (resourceId != null && !resourceId.equals(""))
			System.out.println("=== DELETE: delete resource: " + resourceId + " from database: " + schemaName + " ===");
		else 
			System.out.println("=== DELETE: delete existing database: " + schemaName + " ===");

		URL url = new URL(REST_URL + schemaName + "/" + resourceId);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(RequestMethod.DELETE);
		int responseCode = printResponse(conn);
		conn.disconnect();
		return responseCode;
	}

	public static int dropSchema(String schemaName) throws Exception {
		return deleteResource(schemaName, "");
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

	public static boolean isRunning() throws Exception {
		try {
			((HttpURLConnection) new URL(REST_URL).openConnection()).getResponseCode();
		} catch (ConnectException e) {
			return false;
		}
		return true;
	}

	private static DOMSource createAccount(String name, String accountNo,
			String PIB, String balance) {
		try {
			Document account = null;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();

			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder builder;

			builder = docBuilderFactory.newDocumentBuilder();

			account = builder.newDocument();

			Element accountElement = account.createElementNS("", "racun");

			accountElement.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");

			account.appendChild(accountElement);

			Element nameElement = account.createElement("naziv");
			nameElement.setTextContent(name);
			nameElement.setAttribute("xmlns", "");

			accountElement.appendChild(nameElement);

			Element numberElement = account.createElement("brojRacuna");
			numberElement.setTextContent(accountNo);
			numberElement.setAttribute("xmlns", "");

			accountElement.appendChild(numberElement);

			Element pibElement = account.createElement("pib");
			pibElement.setTextContent(PIB);
			pibElement.setAttribute("xmlns", "");

			accountElement.appendChild(pibElement);

			Element balanceElement = account.createElement("stanje");
			balanceElement.setTextContent(balance);
			balanceElement.setAttribute("xmlns", "");

			accountElement.appendChild(balanceElement);

			DOMSource response = new DOMSource(account);

			return response;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static DOMSource brojacPoslatihDocument(String namespace, String elementName, int number) {
		try {
			Document document = null;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder builder;

			builder = docBuilderFactory.newDocumentBuilder();

			document = builder.newDocument();

			Element CounterDoc = document.createElementNS(namespace,
					elementName);

			CounterDoc.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");

			document.appendChild(CounterDoc);
			Element counter = document.createElement("brojac");
			counter.setTextContent(Integer.toString(number));
			counter.setAttribute("xmlns", "");

			CounterDoc.appendChild(counter);

			DOMSource response = new DOMSource(document);

			return response;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static DOMSource brojacPrimljenihDocument(String namespace, String elementName, String firmName, Date date, int number) {
		try {
			Document document = null;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder builder;

			builder = docBuilderFactory.newDocumentBuilder();

			document = builder.newDocument();

			Element CounterDoc = document.createElementNS(namespace,
					elementName);

			CounterDoc.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");

			document.appendChild(CounterDoc);

			Element firma = document.createElement("firma");
			firma.setAttribute("xmlns", "");

			CounterDoc.appendChild(firma);

			Element naziv = document.createElement("naziv");
			naziv.setTextContent(firmName);
			naziv.setAttribute("xmlns", "");

			firma.appendChild(naziv);

			Element timestamp = document.createElement("timestamp");
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String formatedDate = sdf.format(date);
			
			timestamp.setTextContent(formatedDate);
			timestamp.setAttribute("xmlns", "");

			firma.appendChild(timestamp);

			Element counter = document.createElement("brojac");
			counter.setTextContent(Integer.toString(number));
			counter.setAttribute("xmlns", "");

			firma.appendChild(counter);

			DOMSource response = new DOMSource(document);

			return response;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream DOM2InputStream(DOMSource source)
			throws TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = source;
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer()
		.transform(xmlSource, outputTarget);
		InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
		return is;
	}

	public static int getBrojPoslednjePoslate(String nazivFirme) {

		int brojacInt = 0;
		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Dobavljanje streama brojackog dokumenta
			InputStream counterInput = retrieveResource("/brojacPoslatihFaktura",
					nazivFirme, RequestMethod.GET);

			// Parsiranje strama brojackog dokumenta u dokument
			DocumentBuilderFactory docBF = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = (DocumentBuilder) docBF
					.newDocumentBuilder();
			InputSource inSource = new InputSource(new InputStreamReader(
					counterInput, "UTF-8"));
			Document counterDoc = docBuilder.parse(inSource);

			DocumentTransform.printDocument(counterDoc);

			// Uzimanje podataka iz brojaca
			NodeList counters = counterDoc.getElementsByTagName("brojac");
			String brojac = counters.item(0).getChildNodes().item(0).getTextContent();
			System.out.println(brojac);

			// Povecavanje brojaca
			brojacInt = Integer.parseInt(brojac);
			brojacInt++;

			if (http instanceof BaseXHTTP)
				http.stop();


		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return brojacInt;

	}
	
	public static Date getTimestampPoslednjePrimljene(String nazivFirme, String nazivScheme) {

		String dateStr = null;
		Date date = null;
		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Dobavljanje streama brojackog dokumenta
			InputStream counterInput = retrieveResource("/brojacPrimljenihFaktura",
					nazivScheme, RequestMethod.GET);

			// Parsiranje strama brojackog dokumenta u dokument
			DocumentBuilderFactory docBF = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = (DocumentBuilder) docBF
					.newDocumentBuilder();
			InputSource inSource = new InputSource(new InputStreamReader(
					counterInput, "UTF-8"));
			Document counterDoc = docBuilder.parse(inSource);

			System.out.println("PRE");
			DocumentTransform.printDocument(counterDoc);
			System.out.println("POSLE");

			// Uzimanje podataka iz brojaca
			NodeList counters = counterDoc.getElementsByTagName("firma");
			for(int i = 0; i < counters.getLength(); ++i) {
				if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(nazivFirme))
					dateStr = counters.item(i).getChildNodes().item(3).getTextContent();
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			date = sdf.parse(dateStr);
			

			if (http instanceof BaseXHTTP)
				http.stop();
			
			if(date != null)
				return date;


		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	
	public static int getBrojPoslednjePrimljene(String nazivFirme, String nazivScheme) {

		int brojacInt = 0;
		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Dobavljanje streama brojackog dokumenta
			InputStream counterInput = retrieveResource("/brojacPrimljenihFaktura",
					nazivScheme, RequestMethod.GET);

			// Parsiranje strama brojackog dokumenta u dokument
			DocumentBuilderFactory docBF = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = (DocumentBuilder) docBF
					.newDocumentBuilder();
			InputSource inSource = new InputSource(new InputStreamReader(
					counterInput, "UTF-8"));
			Document counterDoc = docBuilder.parse(inSource);

			System.out.println("PRE");
			DocumentTransform.printDocument(counterDoc);
			System.out.println("POSLE");

			// Uzimanje podataka iz brojaca
			NodeList counters = counterDoc.getElementsByTagName("firma");
			String brojac = null;
			for(int i = 0; i < counters.getLength(); ++i) {
				if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(nazivFirme))
					brojac = counters.item(i).getChildNodes().item(5).getTextContent();	
			}
			
			brojacInt = Integer.parseInt(brojac);
			

			if (http instanceof BaseXHTTP)
				http.stop();
			
			if(brojacInt != 0)
				return brojacInt;


		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return brojacInt;

	}

	public static void sacuvajFakturu(Document document, String nazivEntiteta, String nazivScheme, boolean poslata, Date date) {

		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Inicijalizacija streama u koji ce se dokument prebaciti
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Source xmlSource = new DOMSource(document);
			Result outputTarget = new StreamResult(outputStream);

			// Prebacivanje dokumenta u output stream i potom inicijalizacija output
			// streama u input stream
			TransformerFactory.newInstance().newTransformer()
			.transform(xmlSource, outputTarget);
			InputStream is = new ByteArrayInputStream(
					outputStream.toByteArray());

			if(poslata) {
				// Dobavljanje streama brojackog dokumenta
				InputStream counterInput = retrieveResource("/brojacPoslatihFaktura",
						nazivEntiteta, RequestMethod.GET);

				// Parsiranje strama brojackog dokumenta u dokument
				DocumentBuilderFactory docBF = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = (DocumentBuilder) docBF
						.newDocumentBuilder();
				InputSource inSource = new InputSource(new InputStreamReader(
						counterInput, "UTF-8"));
				Document counterDoc = docBuilder.parse(inSource);

				System.out.println("PRE");
				DocumentTransform.printDocument(counterDoc);
				System.out.println("POSLE");

				// Uzimanje podataka iz brojaca
				NodeList counters = counterDoc.getElementsByTagName("brojac");
				String brojac = counters.item(0).getChildNodes().item(0).getTextContent();
				System.out.println(brojac);

				createResource(nazivEntiteta, "faktura_poslata" + brojac, is);

				// Povecavanje brojaca
				int brojacInt = Integer.parseInt(brojac);
				brojacInt++;

				updateResource(nazivEntiteta, "brojacPoslatihFaktura", DOM2InputStream(brojacPoslatihDocument("","rbrPoslateFakture", brojacInt)));
			} else {

				// Dobavljanje streama brojackog dokumenta
				InputStream counterInput = retrieveResource("/brojacPrimljenihFaktura",
						nazivScheme, RequestMethod.GET);

				// Parsiranje strama brojackog dokumenta u dokument
				DocumentBuilderFactory docBF = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = (DocumentBuilder) docBF
						.newDocumentBuilder();
				InputSource inSource = new InputSource(new InputStreamReader(
						counterInput, "UTF-8"));
				Document counterDoc = docBuilder.parse(inSource);

				System.out.println("PRE");
				DocumentTransform.printDocument(counterDoc);
				System.out.println("POSLE");

				// Uzimanje podataka iz brojaca
				NodeList counters = counterDoc.getElementsByTagName("firma");
				String brojac = null;
				for(int i = 0; i < counters.getLength(); ++i) {
					if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(nazivEntiteta))
						brojac = counters.item(i).getChildNodes().item(5).getTextContent();	
				}

				System.out.println(brojac);

				createResource(nazivScheme, "faktura_"+nazivEntiteta+"_primljena" + brojac, is);

				// Povecavanje brojaca
				int brojacInt = Integer.parseInt(brojac);
				brojacInt++;

				updateResource(nazivScheme, "brojacPrimljenihFaktura", DOM2InputStream(brojacPrimljenihDocument("","primljeneFakture", nazivEntiteta, date, brojacInt)));

			}
			if (http instanceof BaseXHTTP)
				http.stop();

			// Kraj snimanja u bazu

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
