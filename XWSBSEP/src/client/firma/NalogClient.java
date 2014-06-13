package client.firma;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;

import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.Validation;

public class NalogClient {
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

			Document encrypted = MessageTransform.packS("Nalog", "Nalog", inputFile, propSender, cert,
					ConstantsXWS.NAMESPACE_XSD);
			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				DocumentTransform.printDocument(DocumentTransform.convertToDocument(response));
				System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NalogClient fc = new NalogClient();
		fc.testIt("firmaa", "bankaa", "cerBankaa", "./TestXMLi/nalog-example1.xml");
	}
}