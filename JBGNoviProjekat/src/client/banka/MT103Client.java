package client.banka;

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

public class MT103Client {


	
	public static String testIt(Properties propSender, String receiver, String cert,String inputFile) {
	
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/CentralnaRTGSNalog?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/CentralnaRTGSNalog", "CentralnaRTGSNalog");
			QName portName = new QName("http://www.toomanysecrets.com/CentralnaRTGSNalog","CentralnaRTGSNalogPort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.",
						SoapFault.FAULT_CODE_CLIENT, null);
			}
			Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);


			Document encrypted = MessageTransform.packS("Nalog", "Nalog",inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD,"MT103");
			if (encrypted == null)
				return "Greska prilikom kriptovanja MT103";
				
			DOMSource response = dispatch.invoke(new DOMSource(encrypted));	
			if (response == null)
				return "Neuspesna obrada MT103(odgovor web servisa centrale = null).";
					
			Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG, propSender,"firma", "Notif");
			if(decryptedDocument==null)	
				return "Neuspesno raspakovana poruka na servisu centrale.";

			
			//decryptedDocument = DocumentTransform.postDecryptTransform(decryptedDocument, propSender, "firma", "Notif");
			if(decryptedDocument==null)
				return "Neuspesna obrada dekriptovanog odgovora koji je stigao od web servisa centrale.";
			

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "MT103 uspesno obradjen";
	}

	public static void main(String[] args){ 
	
	}

}
