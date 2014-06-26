package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
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
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import beans.fault.Fault;




public class Validation {

	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	//public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";

	private static int errorCount = 0;

	//kreira DOM stablo, bez validacije
	public static Document buildDocumentWithoutValidation(String fileName) {
		Document document = null;
		
		try {
			
			//Kreira se DocumentBuilderFactory klasa, metoda je staticka
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setNamespaceAware(true);

			//Kreira DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			//Parsiramo ulaz, tj. XML file
			document = builder.parse(new File(fileName));

			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			
		} catch (SAXException e) {
			//e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return document;
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
			//e.printStackTrace();
			return null;
			
		} catch (SAXException e) {
			//e.printStackTrace();
			return null;
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
			

		}
		return document;
	}


	public static Reader createReader(Node node) {
		StringWriter strWriter=null;
		try {
			//Kreira se TransformerFactory
			TransformerFactory tFactory = TransformerFactory.newInstance();
			//Kreiramo transformer
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			//Posto je ulaz za transformaciju DOM kreiramo DOMSource
			DOMSource source = new DOMSource(node);

			//serijalizacija DOM-a u string
			strWriter = new StringWriter();
			StreamResult result = new StreamResult(new BufferedWriter(strWriter));
			//Vrsi se transformacija
			transformer.transform(source, result);

			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			
		} catch (TransformerException e) {
			e.printStackTrace();
			
		}
		//od stringa dobijenog serijalizacijom formira se StringReader
		return new BufferedReader(new StringReader(strWriter.getBuffer().toString()));

	}


	public static SOAPFaultException generateSOAPFault(String faultString, QName faultCodeQName, String faultActor) {

		try {
			SOAPFactory f = SOAPFactory.newInstance(); 
			SOAPFault soapFault = f.createFault();

			Fault fault = new Fault();

			soapFault.setFaultCode(faultCodeQName);
			soapFault.setFaultString(faultString);

			/* if (detailNode != null) {
	                Detail detail = soapFault.addDetail(); 
	                detail.appendChild(detailNode);
	            }*/

			if (faultActor != null) {
				soapFault.setFaultActor(faultActor);
				fault.setFaultactor(faultActor);
			}


			fault.setFaultstring(faultString);
			fault.setFaultcode(faultCodeQName);




			JAXBContext context = JAXBContext.newInstance("beans.fault");
			Marshaller marshaller= context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper("fault"));
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(fault,System.out);

			return new SOAPFaultException(soapFault); 
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
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
			errorCount++;
			System.out.println(e.getMessage());
		}

	}

}
