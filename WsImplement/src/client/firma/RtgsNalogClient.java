package client.firma;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;

import security.SecurityClass;
import util.DocumentTransform;

public class RtgsNalogClient {

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
					"http://www.toomanysecrets.com/bankaServis", "MT103Port");

			Service service = Service.create(wsdlLocation, serviceName);
			Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);
			SecurityClass security = new SecurityClass();
			String outputFile = inputFile.substring(0, inputFile.length() - 4)+ "-signed.xml";

			
			Document signed = security
					.addTimestampAndSign(
							alias,
							password,
							keystoreFile,
							keystorePassword,
							inputFile,
							outputFile,
							0,
							" http://localhost:8080/ws_style/services/Rtgs?xsd=../shema/NalogSigned.xsd",
							"nalog");

			Document encrypted = null;

			if (signed == null) {
				System.out.println("Greska u potpisivanju.");
				return;
			}

			encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "nalog");

			if (encrypted == null) {
				System.out.println("Greska u kriptovanju.");
				return;
			}

			security.saveDocument(encrypted,inputFile.substring(0, inputFile.length() - 4)+ "-crypted.xml");

			DOMSource response = dispatch.invoke(new DOMSource(encrypted));

			System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
			DocumentTransform.printDocument(DocumentTransform.convertToDocument(response));
			System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();

		}

	}

	public static void main(String[] args) {
		RtgsNalogClient cl = new RtgsNalogClient();
		cl.testIt("bankaa", "bankaa", "./WEB-INF/keystores/bankaa.jks",
				"bankaa", "./NalogTest/Nalog.xml");

	}

}
