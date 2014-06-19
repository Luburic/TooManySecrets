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
 *
 */
public class RESTUtil {

	//public static final String REST_URL = ResourceBundle.getBundle("basex").getString("rest.url");
	private static String REST_URL = RequestMethod.REST_URL;

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

		DOMSource cntPoslateFakA = brojacPoslatihDocument("","rbrPoslateFaktura", 0);
		DOMSource cntPoslateFakB = brojacPoslatihDocument("","rbrPoslateFaktura", 0);
		DOMSource ctnPrimljeneFakA = brojacPrimljenihDocument(null,"","primljeneFaktura","firma", "firmab", new Date(), 0);
		DOMSource ctnPrimljeneFakB = brojacPrimljenihDocument(null,"","primljeneFaktura", "firma", "firmaa",  new Date(), 0);
		
		
		
		DOMSource cntPoslateNotifA = brojacPoslatihDocument("","rbrPoslateNotifikacija", 0);
		DOMSource cntPoslateNotifB = brojacPoslatihDocument("","rbrPoslateNotifikacija", 0);
		DOMSource ctnPrimljeneNotifA = brojacPrimljenihDocument(null,"","primljeneNotifikacija","firma", "firmab", new Date(), 0);
		DOMSource ctnPrimljeneNotifB = brojacPrimljenihDocument(null,"","primljeneNotifikacija","firma", "firmaa", new Date(), 0);
		
		
		DOMSource cntPoslateNalogA = brojacPoslatihDocument("","rbrPoslateNalog", 0); //firmaA 
		DOMSource cntPoslateNalogB = brojacPoslatihDocument("","rbrPoslateNalog", 0); //firmaB 
		DOMSource ctnPrimljeneNalogA = brojacPrimljenihDocument(null,"","primljeneNalog","banka", "firmaa", new Date(), 0); //bankaA
		DOMSource ctnPrimljeneNalogB = brojacPrimljenihDocument(null,"","primljeneNalog", "banka", "firmab",  new Date(), 0); //bankaB
		
		

		
		
		
		//DocumentTransform.printDocument(DocumentTransform.convertToDocument(ctnPrimljeneFakA));
		ctnPrimljeneFakA = brojacPrimljenihDocument(DocumentTransform.convertToDocument(ctnPrimljeneFakA),"","primljeneFaktura", "firma", "firmac", new Date(), 0);
		ctnPrimljeneNotifA = brojacPrimljenihDocument(DocumentTransform.convertToDocument(ctnPrimljeneNotifA),"","primljeneNotifikacija", "firma", "firmac", new Date(), 0);
		
		
		
		
		
		createResource("firmaa", "brojacPoslatihFaktura", DOM2InputStream(cntPoslateFakA));
		createResource("firmab", "brojacPoslatihFaktura", DOM2InputStream(cntPoslateFakB));
		createResource("firmaa", "brojacPrimljenihFaktura", DOM2InputStream(ctnPrimljeneFakA));
		createResource("firmab", "brojacPrimljenihFaktura", DOM2InputStream(ctnPrimljeneFakB));
		
		
		
		createResource("firmaa", "brojacPoslatihNotifikacija", DOM2InputStream(cntPoslateNotifA));
		createResource("firmab", "brojacPoslatihNotifikacija", DOM2InputStream(cntPoslateNotifB));
		createResource("firmaa", "brojacPrimljenihNotifikacija", DOM2InputStream(ctnPrimljeneNotifA));
		createResource("firmab", "brojacPrimljenihNotifikacija", DOM2InputStream(ctnPrimljeneNotifB));
		
		
		
		
		
		createResource("firmaa", "brojacPoslatihNalog", DOM2InputStream(cntPoslateNalogA));
		createResource("firmab", "brojacPoslatihNalog", DOM2InputStream(cntPoslateNalogB));
		
		createResource("bankaa", "brojacPrimljenihNalog", DOM2InputStream(ctnPrimljeneNalogA));
		createResource("bankab", "brojacPrimljenihNalog", DOM2InputStream(ctnPrimljeneNalogB));
		
		

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
	
	public static InputStream retrieveResourceUnwrap(String query, String schemaName, String method) throws Exception {
		if (method == RequestMethod.GET)
			return retrieveResource(query, schemaName, "UTF-8", false);
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

	/**
	 * Kreira dokument za odrzavanje brojaca poslatih dokumenata
	 */
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

	/**
	 * Kreira se dokument za odrzavanje brojaca za svaki entitet pojedinacno (koji ucestvuje u komunikaciji)
	 */
	public static DOMSource brojacPrimljenihDocument(Document document, String namespace, String entity, String elementName, String senderName, Date date, int number) {

		try {
			Element counterDoc = null;
			if(document == null) {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				docBuilderFactory.setNamespaceAware(true);
				DocumentBuilder builder;

				builder = docBuilderFactory.newDocumentBuilder();

				document = builder.newDocument();

				counterDoc = document.createElementNS(namespace,
						entity);

				counterDoc.setAttribute("xmlns:xsi",
						"http://www.w3.org/2001/XMLSchema-instance");

				document.appendChild(counterDoc);
			}

			//CounterDoc = (Element) document.getElementsByTagNameNS(namespace, entity).item(0);


			Element firma = document.createElement(elementName);
			firma.setAttribute("xmlns", "");

			document.getChildNodes().item(0).appendChild(firma);
			//CounterDoc.appendChild(firma);

			Element naziv = document.createElement("naziv");
			naziv.setTextContent(senderName);
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
	
	/**
	 * Azurira se dokument za odrzavanje brojaca za svaki entitet pojedinacno (koji ucestvuje u komunikaciji)
	 */
	public static DOMSource updateBrojacITimestampPrimljenihDocument(Document document, String namespace, String elementName, String senderName, Date date, int number) {

		// Uzimanje podataka iz brojaca
		NodeList counters = document.getElementsByTagName(elementName);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String formatedDate = sdf.format(date);
		for(int i = 0; i<counters.item(0).getChildNodes().getLength(); ++i) {
			System.out.println("CHILD "+i+": "+counters.item(0).getChildNodes().item(i).getTextContent());
		}
		for(int i = 0; i < counters.getLength(); ++i) {
			if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(senderName)) {
				counters.item(i).getChildNodes().item(3).setTextContent(formatedDate);
				System.out.println("TIMESTAMP: "+counters.item(i).getChildNodes().item(3).getTextContent());
				counters.item(i).getChildNodes().item(5).setTextContent(String.valueOf(number));
				System.out.println("BROJAC: "+counters.item(i).getChildNodes().item(5).getTextContent());
			}
		}
		
		DOMSource response = new DOMSource(document);

		return response;
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

	/**
	 * 
	 * @param schemaName - naziv seme iz koje se preuzima podatak
	 * @param type - tip dokumenta (Faktura, Izvod...)
	 * @return vraca broj poslednjeg poslatog dokumenta
	 */
	public static int getBrojPoslednjePoslate(String schemaName, String type) {

		int brojacInt = 0;
		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Dobavljanje streama brojackog dokumenta
			InputStream counterInput = retrieveResource("/brojacPoslatih"+type,
					schemaName, RequestMethod.GET);

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
	
	/**
	 * 
	 * @param senderName - ime posiljaoca dokumenta
	 * @param nazivScheme - naziv seme baze u koju se cuva (naziv seme baze je isti kao i naziv samog entiteta)
	 * @param entity - tip entiteta (firma, banka), potrebno za preuzimanje podataka o brojacu
	 * @param type - tip dokumenta (Faktura, Izvod...)
	 * @return vraca datum poslednje primljene poruke od odgovarajuceg entiteta za odgovarajuci tip dokumenta
	 */
	public static Date getTimestampPoslednjePrimljene(String senderName, String nazivScheme, String entity, String type) {

		String dateStr = null;
		Date date = null;
		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Dobavljanje streama brojackog dokumenta
			InputStream counterInput = retrieveResource("/brojacPrimljenih"+type,
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
			NodeList counters = counterDoc.getElementsByTagName(entity);
			for(int i = 0; i < counters.getLength(); ++i) {
				System.out.println("SENDER NAME: "+senderName);
				for(int j = 0; j < counters.item(0).getChildNodes().getLength(); ++j) {
					if(counters.item(i).getChildNodes().item(j).getTextContent().equalsIgnoreCase(senderName)) {
						System.out.println("koji je red: "+j);
						System.out.println("red: " + counters.item(i).getChildNodes().item(j).getTextContent());
						System.out.println();
					}
				}
				if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(senderName)) {
					dateStr = counters.item(i).getChildNodes().item(3).getTextContent();
					System.out.println("DATE STRING: " +dateStr);
				}
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
	
	/**
	 * 
	 * @param senderName - ime posiljaoca dokumenta
	 * @param nazivScheme - naziv seme baze u koju se cuva (naziv seme baze je isti kao i naziv samog entiteta)
	 * @param entity - tip entiteta (firma, banka), potrebno za preuzimanje podataka o brojacu
	 * @param type - tip dokumenta (Faktura, Izvod...)
	 * @return vraca broj poslednje primljene poruke od odgovarajuceg entiteta za odgovarajuci tip dokumenta
	 */
	public static int getBrojPoslednjePrimljene(String senderName, String nazivScheme, String entity, String type) {

		int brojacInt = 0;
		try {
			// Pokretanje baze
			BaseXHTTP http = null;
			if (!isRunning())
				http = new BaseXHTTP("-Uadmin", "-Padmin");

			// Dobavljanje streama brojackog dokumenta
			InputStream counterInput = retrieveResource("/brojacPrimljenih"+type,
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
			NodeList counters = counterDoc.getElementsByTagName(entity);
			String brojac = null;
			for(int i = 0; i < counters.getLength(); ++i) {
				if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(senderName))
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

	/**
	 * 
	 * @param document - dokument koji se cuva
	 * @param nazivScheme - naziv seme baze u koju se cuva (naziv seme baze je isti kao i naziv samog entiteta)
	 * @param poslata - flag da se zna da li se cuva poslat ili primljen xml
	 * @param senderName - ime posiljaoca dokumenta (potrebno ukoliko je poslata == false)
	 * @param date - timestamp koji se azurira za replay napad (bezbednost)
	 * @param type - tip dokumenta (Faktura, Izvod...)
	 * @param entity - tip entiteta (firma, banka...) potreban za preuzimanje vrednosti brojaca za odgovarajuci entitet
	 */
	public static void sacuvajEntitet(Document document, String nazivScheme, boolean poslata, String senderName, Date date, String type, String entity) {

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
				InputStream counterInput = retrieveResource("/brojacPoslatih"+type,
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
				NodeList counters = counterDoc.getElementsByTagName("brojac");
				String brojac = counters.item(0).getChildNodes().item(0).getTextContent();
				System.out.println(brojac);

				createResource(nazivScheme, type+"_poslata" + brojac, is);

				// Povecavanje brojaca
				int brojacInt = Integer.parseInt(brojac);
				brojacInt++;

				updateResource(nazivScheme, "brojacPoslatih"+type, DOM2InputStream(brojacPoslatihDocument("","rbrPoslate"+type, brojacInt)));
			} else {

				// Dobavljanje streama brojackog dokumenta
				InputStream counterInput = retrieveResourceUnwrap("/brojacPrimljenih"+type,
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
				NodeList counters = counterDoc.getElementsByTagName(entity);
				String brojac = null;
				for(int i = 0; i < counters.getLength(); ++i) {
					if (counters.item(i).getChildNodes().item(1).getTextContent().equalsIgnoreCase(senderName))
						brojac = counters.item(i).getChildNodes().item(5).getTextContent();	
				}

				System.out.println(brojac);

				createResource(nazivScheme, type+"_"+senderName+"_primljena" + brojac, is);

				// Povecavanje brojaca
				int brojacInt = Integer.parseInt(brojac);
				brojacInt++;

				updateResource(nazivScheme, "brojacPrimljenih"+type, DOM2InputStream(updateBrojacITimestampPrimljenihDocument(counterDoc,"", entity, senderName, date, brojacInt)));

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
