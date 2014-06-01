package util;

import java.io.Reader;

import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.ws.rm.Servant;
import org.basex.query.value.item.Str;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;

public class RecivedMessage {

	
	
	
	public static DOMSource unpack(Document document, String serviceName, String schemaName, String TARGET_NAMESPACE){
		
		SecurityClass security = new SecurityClass();
		Reader reader = Validation.createReader(document);
		Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/"+serviceName+"?xsd=../shema/"+schemaName+"Crypt.xsd","http://localhost:8080/ws_style/services/"+serviceName+"?xsd=xenc-schema.xsd"});
		
		if( doc == null )
			return new DOMSource(DocumentTransform.createNotificationResponse(schemaName+" dokument nije validan po Crypt semi.", TARGET_NAMESPACE));
		
		
		Document decrypt = security.decrypt(doc, security.readPrivateKey("", "", "", ""));
		Reader reader1 = Validation.createReader(decrypt);
		decrypt = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/"+serviceName+"?xsd=../shema/"+schemaName+"Signed.xsd","http://localhost:8080/ws_style/services/"+serviceName+"?xsd=xmldsig-core-schema.xsd"});
		
		if(decrypt==null)
			return new DOMSource(DocumentTransform.createNotificationResponse(schemaName+" dokument nije validan po Signed semi.",TARGET_NAMESPACE));
		
	
		
		if(!security.verifySignature(decrypt)) 
			return new DOMSource(DocumentTransform.createNotificationResponse(schemaName+" dokument nije dobro potpisan.",TARGET_NAMESPACE));
	
	
		int rbr=0; //database
		DOMSource timestampOk= Validation.validateTimestamp(TARGET_NAMESPACE, decrypt, "",rbr);
		if(timestampOk==null)
			return new DOMSource(DocumentTransform.createNotificationResponse(schemaName+" dokument ne odgovara prema vremenu primanja.",TARGET_NAMESPACE));
		
		
		decrypt = DocumentTransform.convertToDocument(timestampOk);
		
		
		//skidanje taga
		Element signature = (Element) decrypt.getElementsByTagName("ds:Signature").item(0);
		signature.getParentNode().removeChild(signature);
		
		Reader reader2 = Validation.createReader(decrypt);
		decrypt = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/"+serviceName+"?xsd=../shema/"+schemaName+"Raw.xsd"});
		
		if( decrypt == null )
			return new DOMSource(DocumentTransform.createNotificationResponse(schemaName +" dokument nije validan po Raw semi.",TARGET_NAMESPACE));
		
		
		
		return new DOMSource(decrypt);
	}
	
	
	public static DOMSource pack(String serviceName, String schemaName,String inputFile,String alias,String password,String keystoreFile,String keystorePassword,String TARGET_NAMESPACE, String NAMESPACE_XSD){
		SecurityClass security =new SecurityClass();
		Document document = Validation.buildDocumentWithoutValidation("./"+schemaName+"Test/"+schemaName+".xml");
		Element mt = (Element) document.getElementsByTagName(schemaName).item(0);
		mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		
		security.saveDocument(document, "./"+schemaName+"Test/"+schemaName+".xml");
		
		
		//potpisivanje od providera
		String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
		
		Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/"+serviceName+"?xsd=../shema/"+schemaName+"Signed.xsd", schemaName);
		
		if( signed == null )
			return new DOMSource(DocumentTransform.createNotificationResponse("Greska u potpisivanju.",TARGET_NAMESPACE));
		
		
		
		Document encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, schemaName);
		
		if(encrypted == null)
			return new DOMSource(DocumentTransform.createNotificationResponse("Greska u enkripciji.",TARGET_NAMESPACE));
		
		security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
		
		
		
		return new DOMSource(encrypted);
	}
	
	
	
	
	
}
