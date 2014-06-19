package util;

import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import provider.banka.NalogProvider;
import provider.firma.FakturaProvider;
import security.SecurityClass;
import basexdb.RESTUtil;

public class MessageTransform {

	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	public static Document unpack(Document document, String serviceAdress, String schemaPrefix, String TARGET_NAMESPACE, Properties propReceiver, String entity, String type){

		SecurityClass security = new SecurityClass();
		Reader reader = Validation.createReader(document);
		Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/"+schemaPrefix+"Crypt.xsd","http://localhost:8080/xenc-schema.xsd"});

		if( doc == null )
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument nije validan po Crypt semi.", TARGET_NAMESPACE);

		URL url=null;
		
		if(schemaPrefix.toLowerCase().equals("faktura")) {
			url = FakturaProvider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		}
		
		else if(schemaPrefix.toLowerCase().equals("nalog") || schemaPrefix.toLowerCase().equals("notification")){
			url = NalogProvider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		}
		

		Document decrypt = security.decrypt(doc, security.readPrivateKey(propReceiver.getProperty("naziv"), propReceiver.getProperty("pass"), url.toString().substring(6), propReceiver.getProperty("passKS")));
		Reader reader1 = Validation.createReader(decrypt);
		decrypt = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/"+schemaPrefix+"Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});

		if(decrypt==null)
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument nije validan po Signed semi.",TARGET_NAMESPACE);

		if(!security.verifySignature(decrypt)) 
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument nije dobro potpisan.",TARGET_NAMESPACE);

		Document forSave = Validation.buildDocumentWithValidation(Validation.createReader(decrypt), new String[]{ "http://localhost:8080/"+schemaPrefix+"Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});

		DocumentTransform.printDocument(forSave);

		Element timestamp = (Element) decrypt.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
		String dateString = timestamp.getTextContent();

		Date date = null;
		try {
			date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String senderName = SecurityClass.getOwner(decrypt).toLowerCase();

		Date dateFromXml = RESTUtil.getTimestampPoslednjePrimljene(senderName, propReceiver.getProperty("naziv"), entity, type);
		//skidanje taga
		//timestamp.getParentNode().removeChild(timestamp);

		Element redniBrojPoruke = (Element) decrypt.getElementsByTagNameNS(NAMESPACE_XSD, "redniBrojPoruke").item(0);

		int rbrPoruke = Integer.parseInt(redniBrojPoruke.getTextContent());
		int rbrPorukeFromXml = RESTUtil.getBrojPoslednjePrimljene(senderName, propReceiver.getProperty("naziv"), entity, type);
		//skidanje taga
		//redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);

		if(rbrPoruke <= rbrPorukeFromXml || dateFromXml.after(date) || dateFromXml.equals(date)) {
			return DocumentTransform.createNotificationResponse(schemaPrefix +" pokusaj napada.", TARGET_NAMESPACE);
		}

		return decrypt;
	}


	public static Document pack(String serviceAdress,String schemaPrefix,String inputFile,String alias,String password,String keystoreFile,String keystorePassword,String TARGET_NAMESPACE, String NAMESPACE_XSD){

		SecurityClass security =new SecurityClass();
		Document document = Validation.buildDocumentWithoutValidation("./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
		Element mt = (Element) document.getElementsByTagName(schemaPrefix).item(0);
		mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");

		security.saveDocument(document, "./"+schemaPrefix+"Test/"+schemaPrefix+".xml");



		String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";

		Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=../shema/"+schemaPrefix+"Signed.xsd", schemaPrefix.toLowerCase());

		if( signed == null )
			return DocumentTransform.createNotificationResponse("Greska u potpisivanju"+schemaPrefix+" dokumenta.",TARGET_NAMESPACE);



		Document encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, schemaPrefix.toLowerCase());

		if(encrypted == null)
			return DocumentTransform.createNotificationResponse("Greska u enkripciji"+schemaPrefix+" dokumenta.",TARGET_NAMESPACE);



		security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
		//snimanje u bazu
		return encrypted;
	}

	
	public static Document packS(String serviceAdress,String schemaPrefix,String inputFile,Properties propSender, String receiver, String NAMESPACE_XSD, String type){
		
		Document document = null;
		SecurityClass security =new SecurityClass();
		if(!serviceAdress.equalsIgnoreCase("Notifikacija")) {
			document = Validation.buildDocumentWithoutValidation("./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
		} else {
			document = Validation.buildDocumentWithoutValidation(inputFile);
		}
		System.out.println("****DOKUMENT :  "+ document);
		
		Element mt = (Element) document.getElementsByTagName(schemaPrefix.toLowerCase()).item(0);
		mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");

		if(!serviceAdress.equalsIgnoreCase("Notifikacija")){
			security.saveDocument(document, "./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
		} else {
			security.saveDocument(document, inputFile);
		}

		String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";

		int brojac = RESTUtil.getBrojPoslednjePoslate(propSender.getProperty("naziv"), type);

		URL url = MessageTransform.class.getClassLoader().getResource(propSender.getProperty("jks"));

		URL urlReceiver = MessageTransform.class.getClassLoader().getResource(propSender.getProperty(receiver));

		Document signed = security.addTimestampAndSign(propSender.getProperty("naziv"), propSender.getProperty("pass"), url.toString().substring(6), propSender.getProperty("passKS"), inputFile, outputFile, brojac, " http://localhost:8080/"+schemaPrefix+"Signed.xsd", schemaPrefix.toLowerCase());

		if( signed == null ) {
			System.out.println("Greska u potpisivanju"+schemaPrefix+" dokumenta.");
			return null;
		}

		RESTUtil.sacuvajEntitet(signed, propSender.getProperty("naziv"), true, propSender.getProperty("naziv"), null, type, null);


		Document encrypted = null;
		try {
			encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificateFromFile(new File(urlReceiver.toString().substring(6))),NAMESPACE_XSD, schemaPrefix.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(encrypted == null) {
			System.out.println("Greska u enkripciji"+schemaPrefix+" dokumenta.");
			return null;

		}
		security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");

		return encrypted;


	}
	
	public static Document removeSignature(Document doc) {
		
		Element signature = (Element) doc.getElementsByTagName("ds:Signature").item(0);
		signature.getParentNode().removeChild(signature);
		
		return doc;
	}
	
	public static Document removeRedniBrojPoruke(Document doc) {
		
		Element redniBrojPoruke = (Element) doc.getElementsByTagNameNS(NAMESPACE_XSD, "redniBrojPoruke").item(0);
		redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);
		
		return doc;
		
	}
	
	public static Document removeTimestamp(Document doc) {
		
		Element timestamp = (Element) doc.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
		timestamp.getParentNode().removeChild(timestamp);
		
		return doc;
		
	}





	public static String randomString(int len) 
	{
		StringBuilder sb = new StringBuilder(len);

		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt(rnd.nextInt(AB.length())));


		return sb.toString();
	}


	public static String checkBank(String prefix) {
		String ans="";

		switch(prefix) {
		case "335": ans="bankaa"; break; 
		case "221": ans="bankab"; break; 

		}
		return ans;
	}




}
