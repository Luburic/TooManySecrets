package client.firma;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;

import util.DocumentTransform;
import util.MessageTransform;
import util.Validation;

public class NalogClient {
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	@SuppressWarnings("unused")
	private Marshaller marshaller;

	public void testIt(String sender, String receiver, String cert, String inputFile) {
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver + "/services/Nalog?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
			QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "NalogPort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.", SoapFault.FAULT_CODE_CLIENT, null);
			}

			Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
			InputStream inputStreamSender = this.getClass().getClassLoader()
					.getResourceAsStream(sender + ".properties");
			Properties propSender = new java.util.Properties();
			propSender.load(inputStreamSender);

			Document encrypted = MessageTransform.packS("Nalog", "Nalog", inputFile, propSender, cert, NAMESPACE_XSD);

			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				DocumentTransform.printDocument(DocumentTransform.convertToDocument(response));
				System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NalogClient fc = new NalogClient();
		fc.testIt("firmaa", "bankaa", "cerBankaa", "./NalogTest/nalog-example1.xml");
	}
}
