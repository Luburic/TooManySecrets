package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;




public class Validation {

	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	
	private static int errorCount = 0;

	//kreira DOM stablo, bez validacije
	public static Document buildDocumentWithoutValidation(String fileName) {
		try {
			Document document = null;
			//Kreira se DocumentBuilderFactory klasa, metoda je staticka
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setNamespaceAware(true);

			//Kreira DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			//Parsiramo ulaz, tj. XML file
			document = builder.parse(new File(fileName));

			return document;
		} catch (ParserConfigurationException e) {
			//e.printStackTrace();
			return null;
		} catch (SAXException e) {
			//e.printStackTrace();
			return null;
		} catch (IOException e) {
			//e.printStackTrace();
			return null;

		}
	}

	//kreira DOM stablo, sa validacijom, xsd fajl se zadaje programski
	public static Document buildDocumentWithValidation(Reader reader,String[] schemasLocations) {
		
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA);
			
			
			int lenght = schemasLocations.length;
			
			switch (lenght) {
			case 1:
				factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource(schemasLocations[0])}));
				break;
			case 2:
				factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource(schemasLocations[0]), 
																		new StreamSource(schemasLocations[1])}));
				break;
				
			case 3:
				factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource(schemasLocations[0]), 
																		new StreamSource(schemasLocations[1]),
																		new StreamSource(schemasLocations[2])}));
				break;
			default:
				break;
			}

			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setErrorHandler(new MyErrorHandler());

			//Parsiramo ulaz, tj. XML file
			document = builder.parse(new InputSource(reader));
			
			reader.close();
			
			if(errorCount > 0) {
				return null;
			}

			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;

		}
		return document;
	}
	
	
	
	
	
	
	public static DOMSource validateTimestamp(String TARGET_NAMESPACE, Document decrypted, String dbDate, int dbCounter) {
		
		
		Element timestamp = (Element) decrypted.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
		String dateString = timestamp.getTextContent();
		//if(dbDate i dateString)
			//return new DOMSource(null);
			
		timestamp.getParentNode().removeChild(timestamp);
		
		
		
		Element redniBrojPoruke = (Element) decrypted.getElementsByTagNameNS(NAMESPACE_XSD, "redniBrojPoruke").item(0);
		//if(dbRbr i redniBrojPoruke)
			//return new DOMSource(null);
		redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);
		
		return new DOMSource(decrypted);
	}
	
	

	public static Reader createReader(Node node) {

		try {
			//Kreira se TransformerFactory
			TransformerFactory tFactory = TransformerFactory.newInstance();
			//Kreiramo transformer
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			//Posto je ulaz za transformaciju DOM kreiramo DOMSource
			DOMSource source = new DOMSource(node);

			//serijalizacija DOM-a u string
			StringWriter strWriter = new StringWriter();
			StreamResult result = new StreamResult(new BufferedWriter(strWriter));
			//Vrsi se transformacija
			transformer.transform(source, result);

			//od stringa dobijenog serijalizacijom formira se StringReader
			return new BufferedReader(new StringReader(strWriter.getBuffer().toString()));

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			return null;
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	static class MyErrorHandler implements ErrorHandler {
		
		public void warning(SAXParseException e) throws SAXException {
			errorCount++;
	        System.out.println(e.getMessage());
	    }

	    public void error(SAXParseException e) throws SAXException {
	    	errorCount++;
	        System.out.println(e.getMessage());
	    }

	    public void fatalError(SAXParseException e) throws SAXException {
	        System.out.println(e.getMessage());
	    }

	}

}
