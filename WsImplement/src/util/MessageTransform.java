package util;

import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import provider.firma.FakturaProvider;
import security.SecurityClass;
import basexdb.RESTUtil;

public class MessageTransform {
	
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";

	
	public static Document unpack(Document document, String serviceAdress, String schemaPrefix, String TARGET_NAMESPACE, Properties propSender, Properties propReceiver){
		
		SecurityClass security = new SecurityClass();
		Reader reader = Validation.createReader(document);
		Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=../shema/"+schemaPrefix+"Crypt.xsd","http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=xenc-schema.xsd"});
		
		if( doc == null )
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument nije validan po Crypt semi.", TARGET_NAMESPACE);
		
		URL url = FakturaProvider.class.getClassLoader().getResource(propSender.getProperty("jks"));
		
		Document decrypt = security.decrypt(doc, security.readPrivateKey(propSender.getProperty("naziv"), propSender.getProperty("pass"), url.toString().substring(6), propSender.getProperty("passKS")));
		Reader reader1 = Validation.createReader(decrypt);
		decrypt = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=../shema/"+schemaPrefix+"Signed.xsd","http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=xmldsig-core-schema.xsd"});
		
		if(decrypt==null)
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument nije validan po Signed semi.",TARGET_NAMESPACE);
		
	
		
		if(!security.verifySignature(decrypt)) 
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument nije dobro potpisan.",TARGET_NAMESPACE);
	
	
		/*int dbCounter=0; //database
	
		DOMSource timestampOk= Validation.validateTimestamp(TARGET_NAMESPACE, decrypt, "20.02.2014",dbCounter);
		if(timestampOk==null)
			return DocumentTransform.createNotificationResponse(schemaPrefix+" dokument ne odgovara prema vremenu primanja.",TARGET_NAMESPACE);
		
		
		decrypt = DocumentTransform.convertToDocument(timestampOk);*/
		
		Document forSave = Validation.buildDocumentWithValidation(Validation.createReader(decrypt), new String[]{ "http://localhost:8080/ws_style/services/Faktura?xsd=../shema/FakturaSigned.xsd","http://localhost:8080/ws_style/services/Faktura?xsd=xmldsig-core-schema.xsd"});
		
		//ovde ce ici provera za timestamp
		Element timestamp = (Element) decrypt.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
		String dateString = timestamp.getTextContent();
		
		Date date = null;
		try {
			date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Date dateFromXml = RESTUtil.getTimestampPoslednjePrimljene(propSender.getProperty("naziv"), propReceiver.getProperty("naziv"));
		//skidanje taga
		timestamp.getParentNode().removeChild(timestamp);
		
		//ovde ce ici provera za redni broj poruke
		Element redniBrojPoruke = (Element) decrypt.getElementsByTagNameNS(NAMESPACE_XSD, "redniBrojPoruke").item(0);
		
		int rbrPoruke = Integer.parseInt(redniBrojPoruke.getTextContent());
		int rbrPorukeFromXml = RESTUtil.getBrojPoslednjePrimljene(propSender.getProperty("naziv"), propReceiver.getProperty("naziv"));
		//skidanje taga
		redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);
		
		if(rbrPoruke <= rbrPorukeFromXml || dateFromXml.after(date) || dateFromXml.equals(date)) {
			return DocumentTransform.createNotificationResponse(schemaPrefix +" pokusaj napada.", TARGET_NAMESPACE);
		}
		///////////////////////////////////////////////
		
		Element signature = (Element) decrypt.getElementsByTagName("ds:Signature").item(0);
		signature.getParentNode().removeChild(signature);
		
		Reader reader2 = Validation.createReader(decrypt);
		decrypt = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=../shema/"+schemaPrefix+"Raw.xsd"});
		
		if( decrypt == null )
			return DocumentTransform.createNotificationResponse(schemaPrefix +" dokument nije validan po Raw semi.",TARGET_NAMESPACE);
		
		
		RESTUtil.sacuvajFakturu(forSave, propSender.getProperty("naziv"), propReceiver.getProperty("naziv"), false, date);
		
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
	
	
	
	
	
	
	
	
	
	
	//faktura klijent
	public static Document packS(String serviceAdress,String schemaPrefix,String inputFile,String alias,String password,String keystoreFile,String keystorePassword, String NAMESPACE_XSD){
	
		SecurityClass security =new SecurityClass();
		Document document = Validation.buildDocumentWithoutValidation("./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
		Element mt = (Element) document.getElementsByTagName(schemaPrefix.toLowerCase()).item(0);
		mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		
		security.saveDocument(document, "./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
		
		
		
		String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
		
		int brojac = RESTUtil.getBrojPoslednjePoslate(alias);
		
		Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, brojac, " http://localhost:8080/ws_style/services/"+serviceAdress+"?xsd=../shema/"+schemaPrefix+"Signed.xsd", schemaPrefix.toLowerCase());
		
		if( signed == null ) {
			System.out.println("Greska u potpisivanju"+schemaPrefix+" dokumenta.");
			return null;
		}
		
		RESTUtil.sacuvajFakturu(signed, alias, null, true, null);
		
		
		Document encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, schemaPrefix.toLowerCase());
		
		if(encrypted == null) {
			System.out.println("Greska u enkripciji"+schemaPrefix+" dokumenta.");
			return null;
		
		}
		security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
		
		
		
		return encrypted;
	
	
	}
}
