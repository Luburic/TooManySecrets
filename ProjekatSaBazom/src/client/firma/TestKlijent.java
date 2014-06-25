package client.firma;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.Validation;
import basexdb.banka.BankeSema;
import basexdb.util.BankaDBUtil;
import beans.mt910.MT910;
import beans.notification.Notification;

public class TestKlijent {
	
	private static String apsolute = DocumentTransform.class.getClassLoader().getResource("mt910.xml").toString().substring(6);
	
	public boolean testIt103(Properties propSender, String receiver, String cert,String inputFile) {

		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/MT910Response?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/MT910Response", "MT910Response");
			QName portName = new QName("http://www.toomanysecrets.com/MT910Response","MT910ResponsePort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.",
						SoapFault.FAULT_CODE_CLIENT, null);
			}
			Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);

			Document encrypted = MessageTransform.packS("MT910", "MT910", inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD_MT910, "MT910");

			DocumentTransform.printDocument(encrypted);

			BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propSender.getProperty("address"));
			if(encrypted != null) {
				semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
				BankaDBUtil.storeBankaDatabase(semaBanka, propSender.getProperty("address"));
			}



			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));

				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification", ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender,"banka", "Notifikacija");

					DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

					if(decryptedDocument != null){

						decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

						
						SecurityClass sc = new SecurityClass();
						sc.saveDocument(decryptedDocument, apsolute);
						JAXBContext context = JAXBContext.newInstance("beans.notification");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						Notification notification = (Notification) unmarshaller.unmarshal(new File(apsolute));
						
						System.out.println("Notification message: " + notification.getNotificationstring());
						
						return true;


					}else{
						return false;
					}

				}else {
					return false;
				}
			}else {
				return false;
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}
	
	private void createMT910() {
		MT910 mt = null;
		try {
			mt = new MT910();
			mt.setIdPorukeNaloga("123");
			mt.setDatumValute(null);
			mt.setIdPoruke(MessageTransform.randomString(50));
			mt.setIznos(new BigDecimal(100));
			mt.setObracunskiRacunBankePoverioca("asdad");
			mt.setSifraValute("asdasd");
			mt.setSwiftBankePoverioca("asdasdas");
			
			JAXBContext context = JAXBContext.newInstance("beans.mt910");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			String apsolutePathTemp = DocumentTransform.class.getClassLoader().getResource("mt910.xml").toString().substring(6);
			marshaller.marshal(mt, new File(apsolutePathTemp));
			
			Document doc = Validation.buildDocumentWithoutValidation(apsolutePathTemp);
			Element mt910 = (Element) doc.getElementsByTagName("MT910").item(0);
			mt910.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt910.setAttribute("idPoruke", "MT910");
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolutePathTemp);
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		TestKlijent fc = new TestKlijent();
		fc.createMT910();
		Properties propReceiver;
		InputStream inputStreamReceiver = TestKlijent.class.getClassLoader().getResourceAsStream("bankaa.properties");
		propReceiver = new Properties();
		try {
			propReceiver.load(inputStreamReceiver);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fc.testIt103(propReceiver, "bankab", "cerbankab", apsolute);
	}
}
