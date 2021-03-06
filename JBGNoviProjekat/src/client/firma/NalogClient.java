package client.firma;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
import util.Validation;
import beans.nalog.Nalog;

public class NalogClient {
	public void testIt(String sender, String receiver, String cert,
			String inputFile) {
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/BankaNalog?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/BankaNalog", "BankaNalog");
			QName portName = new QName("http://www.toomanysecrets.com/BankaNalog","BankaNalogPort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.",
						SoapFault.FAULT_CODE_CLIENT, null);
			}
			Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);

			InputStream inputStreamSender = this.getClass().getClassLoader().getResourceAsStream(sender + ".properties");
			Properties propSender = new java.util.Properties();
			propSender.load(inputStreamSender);

			Document encrypted = MessageTransform.packS("Nalog", "Nalog",inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD,"Nalog");

			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));

				if (response != null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG, propSender,"firma", "Notif");
					DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				}
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
		fc.testIt("firmaA", "bankaa", "cerbankaa", "./NalogTest/nalog.xml");
	}

	public Nalog createNalog(String idPoruke, String duznik, String primalac, String datumNaloga, String datumValute,
			boolean hitno,BigDecimal iznos, int modelZaduzenja, int modelOdobrenja, String oznakaValute,String pozBrZad,
			int pozBrOdo, String racDuznika, String racPrim,String svrha) {

		Nalog nalog = null;
		try {
			nalog = new Nalog();
			nalog.setIdPoruke(idPoruke);
			nalog.setDuznikNalogodavac(duznik);
			nalog.setPrimalacPoverilac(primalac);
			
			
			//iz main-a
			/*
			nalog.setDatumNaloga(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(datumNaloga)));
			nalog.setDatumValute(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(datumValute)));
			*/
			
			//iz gui
			nalog.setDatumNaloga(MyDatatypeConverter.parseDate(datumNaloga));
			nalog.setDatumValute(MyDatatypeConverter.parseDate(datumValute));
			
			
			nalog.setHitno(hitno);
			nalog.setIznos(iznos);
			nalog.setModelZaduzenja(modelZaduzenja);
			nalog.setModelOdobrenja(modelOdobrenja);
			nalog.setOznakaValute(oznakaValute);
			nalog.setPozivNaBrojZaduzenja(pozBrZad);
			nalog.setPozivNaBrojOdobrenja(pozBrOdo);
			nalog.setRacunDuznika(racDuznika);
			nalog.setRacunPoverioca(racPrim);
			nalog.setSvrhaPlacanja(svrha);
			

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(nalog, new File("./NalogTest/nalog.xml"));

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nalog;
	}
}