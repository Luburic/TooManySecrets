package client.firma;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;

import util.DocumentTransform;
import util.MessageTransform;

public class ClearingNalogClient {

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";

	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	
	
	public void testIt(String alias, String password, String keystoreFile,
			String keystorePassword, String inputFile) {

		try {

			URL wsdlLocation = new URL(
					"http://localhost:8080/ws_style/services/Banka?wsdl");

			QName serviceName = new QName(
					"http://www.toomanysecrets.com/bankaServis", "BankaServis");
			QName portName = new QName(
					"http://www.toomanysecrets.com/bankaServis", "MT102Port");

			Service service = Service.create(wsdlLocation, serviceName);
			Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);
			
		    Document encrypted= MessageTransform.packS("Clearing", "Nalog", inputFile, null, null, NAMESPACE_XSD);
		    if(encrypted!=null) {
			DOMSource response = dispatch.invoke(new DOMSource(encrypted));

			System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
			DocumentTransform.printDocument(DocumentTransform.convertToDocument(response));
			System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

		    }
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();

		}

	}

	public static void main(String[] args) {
		ClearingNalogClient cl = new ClearingNalogClient();
		cl.testIt("bankaa", "bankaa", "./WEB-INF/keystores/bankaa.jks",
				"bankaa", "./NalogTest/Nalog.xml");

	}
}
