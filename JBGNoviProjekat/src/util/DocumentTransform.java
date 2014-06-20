package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import security.SecurityClass;
import basexdb.RESTUtil;
import beans.notification.Notification;

public class DocumentTransform {

	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";

	public static DocumentBuilder getDocumentBuilder() {
		try {
			// Setup document builder
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			// validacija XML scheme
			docBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			return builder;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Document convertToDocument(DOMSource request){
		Document r = null;

		try {
			DocumentBuilder db = getDocumentBuilder();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			transformer.transform(request, result);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			r = db.parse(bais);
		} 
		catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}



	public static void printDocument(Document document) {

		try {

			//Kreira se TransformerFactory
			TransformerFactory tFactory = TransformerFactory.newInstance();
			//Kreiramo transformer
			Transformer transformer = tFactory.newTransformer();
			//uvlacenje
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//Posto je ulaz za transformaciju DOM kreiramo DOMSource
			DOMSource source = new DOMSource(document);
			//Izlaz je std.out, odnosno stream
			StreamResult result = new StreamResult(System.out);
			//Vrsi se transformacija
			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}



	public static Document createNotificationResponse(String notificationMessage,String TARGET_NAMESPACE) {
		Document doc = null;

		try {
			Notification notification = new Notification();
			notification.setNotificationstring(notificationMessage);

			JAXBContext context = JAXBContext.newInstance("beans.notification");
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
			System.out.println("NOTIFICATION RESPONSE URL: "+apsolute);
			marshaller.marshal(notification, new File(apsolute));
			doc = Validation.buildDocumentWithoutValidation(apsolute);
		
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}
	
	//serializuje DOM u XML
	public static void transform(Document doc, String file) {
		try {

			//Kreira se TransformerFactory
			TransformerFactory tFactory = TransformerFactory.newInstance();
			//Kreiramo transformer
			Transformer transformer = tFactory.newTransformer();
			//uvlacenje
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//Posto je ulaz za transformaciju DOM kreiramo DOMSource
			DOMSource source = new DOMSource(doc);
			//Izlaz je std.out, odnosno stream
			StreamResult result = new StreamResult(new FileOutputStream(new File(file)));
			//Vrsi se transformacija
			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//propSender - properties onog ko je slao
	public static Document postDecryptTransform(Document decryptedDocument,Properties propSender, String entity, String type) {
		try {
			
			Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD,"timestamp").item(0);
			String dateString = timestamp.getTextContent();
			Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
			String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();
			RESTUtil.sacuvajEntitet(decryptedDocument,propSender.getProperty("naziv"), false, owner, date,type, entity);
			decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument);
			decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument);
			decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
			
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			}
		return decryptedDocument;
	}
	
}
