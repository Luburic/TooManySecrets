package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMUtils {


	public static Document createNewDocument() {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			//kreira se novi dokument
			document = builder.newDocument();

		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return document;
	}

	//pravi sa DOM
	public static Document createDomTree(Document document) {

		//kreira se root element
		Element root = document.createElement("registeredFirms"); 
		//postavlja se kao root
		document.appendChild(root);

		//kreira se atribut
		root.setAttribute("xmlns", "http://www.toomanysecrets.com/firme");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");


		return document;
	}

	//pravi sa DOM
	public static Document createDomTreeCrl(Document document) {

		//kreira se root element
		Element root = document.createElement("crlBank"); 
		//postavlja se kao root
		document.appendChild(root);

		//kreira se atribut
		root.setAttribute("xmlns", "http://www.toomanysecrets.com/bankecrl");

		return document;
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

}
