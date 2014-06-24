package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import client.firma.ZahtevZaIzvodClient;
import provider.banka.IzvodProvider;
import provider.banka.NalogProvider;
import provider.centrala.MT102Provider;
import provider.centrala.MT103Provider;
import provider.firma.FakturaProvider;
import security.SecurityClass;
import basexdb.banka.BankeSema;
import basexdb.centralna.CentralnaSema;
import basexdb.firma.FirmeSema;
import basexdb.util.BankaDBUtil;
import basexdb.util.CentralnaDBUtil;
import basexdb.util.FirmaDBUtil;
import crlBanks.CrlBank;
import crlBanks.CrlBank.Firm;
import crlCentralna.CrlCentralna;
import crlCentralna.CrlCentralna.Bank;

public class MessageTransform {

	//public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	public static Document unpack(Document document, String serviceAdress, String schemaPrefix, String TARGET_NAMESPACE, Properties propReceiver, String entity, String type){

		FirmeSema semaFirma = null;
		BankeSema semaBanka = null;
		CentralnaSema semaCentralna = null;
		String dbType = propReceiver.getProperty("type");
		if(dbType.equalsIgnoreCase("firma")) {
			semaFirma = FirmaDBUtil.loadFirmaDatabase(propReceiver.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("banka")) {
			semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("centralnabanka")) {
			semaCentralna = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
		} else {
			DocumentTransform.createNotificationResponse("470", "Greska u bazi", TARGET_NAMESPACE);
			return null;
		}
		SecurityClass security = new SecurityClass();
		Reader reader = Validation.createReader(document);
		Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/"+schemaPrefix+"Crypt.xsd","http://localhost:8080/xenc-schema.xsd"});

		if( doc == null ){
			DocumentTransform.createNotificationResponse("420", schemaPrefix+" dokument nije validan po Crypt semi.", TARGET_NAMESPACE);
			return null;
		}

		boolean algo = checkAlgorithm(doc);
		if(algo == false) {
			DocumentTransform.createNotificationResponse("450", schemaPrefix+" dokument nije kriptovan odgovarajucim algoritmom.", TARGET_NAMESPACE);
			return null;
		}
		URL url=null;

		if(schemaPrefix.toLowerCase().equals("faktura") || schemaPrefix.toLowerCase().equals("notification")) {
			url = FakturaProvider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		} else if(schemaPrefix.toLowerCase().equals("nalog") || schemaPrefix.toLowerCase().equals("notification")){
			url = NalogProvider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		} else if(schemaPrefix.toLowerCase().equals("izvod") || schemaPrefix.toLowerCase().equals("notification")){
			url = IzvodProvider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		} else if(schemaPrefix.toLowerCase().equals("presek")){
			url = ZahtevZaIzvodClient.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		} else if(schemaPrefix.toLowerCase().equals("mt103") || schemaPrefix.toLowerCase().equals("mt900") || schemaPrefix.toLowerCase().equals("mt910")){
			url = MT103Provider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		} else if(schemaPrefix.toLowerCase().equals("mt102")){
			url = MT102Provider.class.getClassLoader().getResource(propReceiver.getProperty("jks"));
		}


		Document decrypt = security.decrypt(doc, security.readPrivateKey(propReceiver.getProperty("naziv"), propReceiver.getProperty("pass"), url.toString().substring(6), propReceiver.getProperty("passKS")));
		Reader reader1 = Validation.createReader(decrypt);
		decrypt = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/"+schemaPrefix+"Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});

		if(decrypt==null){
			DocumentTransform.createNotificationResponse("421", schemaPrefix+" dokument nije validan po Signed semi.",TARGET_NAMESPACE);
			return null;
		}

		if(!security.verifySignature(decrypt)) {
			DocumentTransform.createNotificationResponse("422", schemaPrefix+" dokument nije dobro potpisan.",TARGET_NAMESPACE);
			return null;
		}

		Document forSave = Validation.buildDocumentWithValidation(Validation.createReader(decrypt), new String[]{ "http://localhost:8080/"+schemaPrefix+"Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});

		DocumentTransform.printDocument(forSave);

		Element timestamp = (Element) decrypt.getElementsByTagNameNS(TARGET_NAMESPACE, "timestamp").item(0);
		String dateString = timestamp.getTextContent();

		Date date = null;
		try {
			date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String senderName = SecurityClass.getOwner(decrypt).toLowerCase();
		Date dateFromXml = getTimeStampPoslednjePrimljenePoruke(semaFirma, semaBanka, semaCentralna, type, senderName, entity);

		Element redniBrojPoruke = (Element) decrypt.getElementsByTagNameNS(TARGET_NAMESPACE, "redniBrojPoruke").item(0);
		int rbrPoruke = Integer.parseInt(redniBrojPoruke.getTextContent());
		int rbrPorukeFromXml = getBrojPoslednjePrimljenePoruke(semaFirma, semaBanka, semaCentralna, type, senderName, entity);

		if(dbType.equalsIgnoreCase("firma")) {
			FirmaDBUtil.storeFirmaDatabase(semaFirma, propReceiver.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("banka")) {
			BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("centralnabanka")) {
			CentralnaDBUtil.storeCentralnaDatabase(semaCentralna, propReceiver.getProperty("address"));
		} else {
			return DocumentTransform.createNotificationResponse("470", "Greska u bazi", TARGET_NAMESPACE);
		}

		if(rbrPoruke <= rbrPorukeFromXml || dateFromXml.after(date) || dateFromXml.equals(date)) {
			DocumentTransform.createNotificationResponse("451", schemaPrefix +" pokusaj napada.", TARGET_NAMESPACE);
			return null;
		}

		//provera lanca i crl
		Document temp = Validation.buildDocumentWithValidation(Validation.createReader(decrypt), new String[]{ "http://localhost:8080/"+schemaPrefix+"Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
		X509Certificate cert = SecurityClass.getCertFromDocument(temp);
		String issuerName = SecurityClass.getIssuer(temp);
		Certificate certCentralna = null;
		Certificate certIssuer = null;

		//provera za firme prvo
		if(!issuerName.equalsIgnoreCase("centralnabanka")) {
			try {
				if(SecurityClass.isSelfSigned(cert)){
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				}
				
				downloadUsingStream("http://localhost:8080/"+issuerName+"/"+issuerName+".cer", issuerName+".cer");
				downloadUsingStream("http://localhost:8080/"+"centralnabanka"+"/"+"centralnabanka"+".cer", "centralnabanka"+".cer");
				SecurityClass sc = new SecurityClass();
				certIssuer = sc.readCertificateFromFile(new File(issuerName+".cer"));
				System.out.println(certIssuer);
				certCentralna = sc.readCertificateFromFile(new File("centralnabanka"+".cer"));

				try {
					cert.verify(((X509Certificate)certIssuer).getPublicKey());
				} catch (SignatureException e1) {
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				} catch (CertificateException e1) {
					e1.printStackTrace();
				}

				try {
					((X509Certificate)certIssuer).verify(((X509Certificate)certCentralna).getPublicKey());
				} catch (SignatureException e1) {
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				} catch (CertificateException e1) {
					e1.printStackTrace();
				}

				File file = new File("temp.xml");
				if(file.exists())
					file.delete();
				file.createNewFile();

				downloadUsingStream("http://localhost:8080/"+issuerName+"/crl"+issuerName+".xml", "temp.xml");

				Document crlDoc = Validation.buildDocumentWithoutValidation("temp.xml");
				X509Certificate certCrl = SecurityClass.getCertFromDocument(crlDoc);
				try {
					certCrl.verify(((X509Certificate)certCentralna).getPublicKey());
				} catch (SignatureException e1) {
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				} catch (CertificateException e1) {
					e1.printStackTrace();
				}
				crlDoc = removeSignature(crlDoc);
				DocumentTransform.transform(crlDoc, "temp.xml");
				DocumentTransform.printDocument(crlDoc);
				CrlBank crlBank = CrlBank.load("temp.xml");
				for(Firm f : crlBank.getFirm()) {
					for(String s: f.getCertificateID()){
						if(Integer.parseInt(s) == Integer.parseInt(cert.getSerialNumber().toString())){
							DocumentTransform.createNotificationResponse("424", schemaPrefix +" nalazi se u CRL listi banke.", TARGET_NAMESPACE);
							return null;
						}
					}
				}

				cert = (X509Certificate)certIssuer;
				issuerName = SecurityClass.getIssuerFromCert((X509Certificate)certIssuer);

			} catch (CertificateException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchProviderException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		//provera za banku
		if(issuerName.equalsIgnoreCase("centralnabanka")) {
			try {

				downloadUsingStream("http://localhost:8080/"+"centralnabanka"+"/"+"centralnabanka"+".cer", "centralnabanka"+".cer");
				SecurityClass sc = new SecurityClass();
				certCentralna = sc.readCertificateFromFile(new File("centralnabanka"+".cer"));

				try {
					cert.verify(((X509Certificate)certCentralna).getPublicKey());
				} catch (SignatureException e1) {
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				} catch (CertificateException e1) {
					e1.printStackTrace();
				}

				if(!SecurityClass.isSelfSigned((X509Certificate)certCentralna)){
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				}

				File file = new File("temp2.xml");
				if(file.exists())
					file.delete();
				file.createNewFile();

				downloadUsingStream("http://localhost:8080/"+issuerName+"/crl"+issuerName+".xml", "temp2.xml");

				Document crlDoc = Validation.buildDocumentWithoutValidation("temp2.xml");
				X509Certificate certCrl = SecurityClass.getCertFromDocument(crlDoc);
				try {
					certCrl.verify(((X509Certificate)certCentralna).getPublicKey());
				} catch (SignatureException e1) {
					DocumentTransform.createNotificationResponse("423", schemaPrefix +" neispravan sertifikat.", TARGET_NAMESPACE);
					return null;
				} catch (CertificateException e1) {
					e1.printStackTrace();
				}
				crlDoc = removeSignature(crlDoc);
				DocumentTransform.transform(crlDoc, "temp2.xml");
				DocumentTransform.printDocument(crlDoc);
				CrlCentralna crlCentralna = CrlCentralna.load("temp2.xml");
				for(Bank b : crlCentralna.getBank()) {
					for(String s: b.getCertificateID()){
						if(Integer.parseInt(s) == Integer.parseInt(cert.getSerialNumber().toString())){
							DocumentTransform.createNotificationResponse("424", schemaPrefix +" nalazi se u CRL listi centralne banke.", TARGET_NAMESPACE);
							return null;
						}
					}

				}


			} catch (CertificateException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchProviderException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return decrypt;
	}


	public static Document packS(String serviceAdress,String schemaPrefix,String inputFile,Properties propSender, String receiver, String NAMESPACE_XSD, String type){

		FirmeSema semaFirma = null;
		BankeSema semaBanka = null;
		CentralnaSema semaCentralna = null;
		String dbType = propSender.getProperty("type");
		if(dbType.equalsIgnoreCase("firma")) {
			semaFirma = FirmaDBUtil.loadFirmaDatabase(propSender.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("banka")) {
			semaBanka = BankaDBUtil.loadBankaDatabase(propSender.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("centralnabanka")) {
			semaCentralna = CentralnaDBUtil.loadCentralnaDatabase(propSender.getProperty("address"));
		} else {
			return DocumentTransform.createNotificationResponse("470", "Greska u bazi", NAMESPACE_XSD);
		}
		Document document = null;
		SecurityClass security =new SecurityClass();
		if(!serviceAdress.equalsIgnoreCase("Notifikacija")) {
			//document = Validation.buildDocumentWithoutValidation("./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
			document = Validation.buildDocumentWithoutValidation(inputFile);
		} else {
			document = Validation.buildDocumentWithoutValidation(inputFile);
		}
		System.out.println("****DOKUMENT :  "+ document);

		Element mt = null;
		if(schemaPrefix.equals("MT103") || schemaPrefix.equals("MT102") || schemaPrefix.equals("MT900") || schemaPrefix.equals("MT910")) {
			mt = (Element) document.getElementsByTagName(schemaPrefix).item(0);
		} else {
			mt = (Element) document.getElementsByTagName(schemaPrefix.toLowerCase()).item(0);
		}
		mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");

		if(!serviceAdress.equalsIgnoreCase("Notifikacija")){
			//security.saveDocument(document, "./"+schemaPrefix+"Test/"+schemaPrefix+".xml");
			security.saveDocument(document, inputFile);
		} else {
			security.saveDocument(document, inputFile);
		}

		String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";

		//int brojac = RESTUtil.getBrojPoslednjePoslate(propSender.getProperty("naziv"), type);
		int brojac = getBrojPoslednjePoslatePoruke(semaFirma, semaBanka, semaCentralna, type);

		if(dbType.equalsIgnoreCase("firma")) {
			FirmaDBUtil.storeFirmaDatabase(semaFirma, propSender.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("banka")) {
			BankaDBUtil.storeBankaDatabase(semaBanka, propSender.getProperty("address"));
		} else if (dbType.equalsIgnoreCase("centralnabanka")) {
			CentralnaDBUtil.storeCentralnaDatabase(semaCentralna, propSender.getProperty("address"));
		} else {
			return DocumentTransform.createNotificationResponse("470", "Greska u bazi", NAMESPACE_XSD);
		}

		URL url = MessageTransform.class.getClassLoader().getResource(propSender.getProperty("jks"));

		URL urlReceiver = MessageTransform.class.getClassLoader().getResource(propSender.getProperty(receiver));

		System.out.println("URL: "+url);
		System.out.println("URL RECEIVER: "+urlReceiver);
		System.out.println("INPUT FILE: "+inputFile);


		Document signed = security.addTimestampAndSign(propSender.getProperty("naziv"), propSender.getProperty("pass"), url.toString().substring(6), propSender.getProperty("passKS"), inputFile, outputFile, brojac, " http://localhost:8080/"+schemaPrefix+"Signed.xsd", schemaPrefix.toLowerCase(), NAMESPACE_XSD);

		if( signed == null ) {
			System.out.println("Greska u potpisivanju"+schemaPrefix+" dokumenta.");
			return null;
		}

		DocumentTransform.printDocument(signed);
		//RESTUtil.sacuvajEntitet(signed, propSender.getProperty("naziv"), true, propSender.getProperty("naziv"), null, type, null);


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

	public static Document removeRedniBrojPoruke(Document doc, String TARGET_NAMESPACE) {

		Element redniBrojPoruke = (Element) doc.getElementsByTagNameNS(TARGET_NAMESPACE, "redniBrojPoruke").item(0);
		redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);

		return doc;

	}

	public static Document removeTimestamp(Document doc, String TARGET_NAMESPACE) {

		Element timestamp = (Element) doc.getElementsByTagNameNS(TARGET_NAMESPACE, "timestamp").item(0);
		timestamp.getParentNode().removeChild(timestamp);

		return doc;

	}

	public static boolean checkAlgorithm(Document doc) {

		Element encMethodEl = (Element)doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
		if(encMethodEl.getAttribute("Algorithm").equalsIgnoreCase("http://www.w3.org/2001/04/xmlenc#aes128-cbc")) {
			return true;
		}

		return false;
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

	private static void downloadUsingStream(String urlStr, String file) throws IOException{
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count=0;
		while((count = bis.read(buffer,0,1024)) != -1)
		{
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}

	public static int getBrojPoslednjePrimljenePoruke(FirmeSema semaFirma, BankeSema semaBanka, CentralnaSema semaCentralna, String type, String senderName, String entity) {
		int rbrPorukeFromXml = 0;
		if(semaFirma != null) {
			switch(type.toLowerCase()) {
			case "faktura":
				FirmeSema.BrojacPoslednjePrimljeneFakture.Firma fFaktura = semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(senderName);
				rbrPorukeFromXml = fFaktura.getBrojac();
				break;
			case "presek":
				FirmeSema.BrojacPoslednjegPrimljenogPreseka.Banka bPresek = semaFirma.getBrojacPoslednjegPrimljenogPreseka().getBankaByNaziv(senderName);
				rbrPorukeFromXml = bPresek.getBrojac();
				break;
			case "notifikacija":
				if(entity.equals("firma")) {
					FirmeSema.BrojacPoslednjePrimljeneNotifikacije.Firma fNotifikacija = semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getFirmaByNaziv(senderName);
					rbrPorukeFromXml = fNotifikacija.getBrojac();
				} else {
					FirmeSema.BrojacPoslednjePrimljeneNotifikacije.Banka fNotifikacija = semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getBankaByNaziv(senderName);
					rbrPorukeFromXml = fNotifikacija.getBrojac();
				}
				break;
			}
		} else if (semaBanka != null) {
			switch(type.toLowerCase()) {
			case "nalog":
				BankeSema.BrojacPoslednjegPrimljenogNaloga.Firma bNalog = semaBanka.getBrojacPoslednjegPrimljenogNaloga().getFirmaByNaziv(senderName);
				rbrPorukeFromXml = bNalog.getBrojac();
				break;
			case "izvod":
				BankeSema.BrojacPoslednjegPrimljenogZahtevaZaIzvod.Firma bIzvod = semaBanka.getBrojacPoslednjegPrimljenogZahtevaZaIzvod().getFirmaByNaziv(senderName);
				rbrPorukeFromXml = bIzvod.getBrojac();
				break;
			case "notifikacija":
				rbrPorukeFromXml = semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().getBrojac();
				break;
			}

		} else if (semaCentralna != null) {
			System.out.println("USAO KOD CENTRALNE U BAZU");
			CentralnaSema.BrojacPoslednjegPrimljenogMTNaloga.Banka cMTNalog = semaCentralna.getBrojacPoslednjegPrimljenogMTNaloga().getBankaByNaziv(senderName);
			rbrPorukeFromXml = cMTNalog.getBrojac();
		}

		return rbrPorukeFromXml;
	}

	public static Date getTimeStampPoslednjePrimljenePoruke(FirmeSema semaFirma, BankeSema semaBanka, CentralnaSema semaCentralna, String type, String senderName, String entity) {
		Date date = null;
		String dateFromXml = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		if(semaFirma != null) {
			switch(type.toLowerCase()) {
			case "faktura":
				FirmeSema.BrojacPoslednjePrimljeneFakture.Firma fFaktura = semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(senderName);
				dateFromXml = fFaktura.getTimestamp();
				break;
			case "presek":
				FirmeSema.BrojacPoslednjegPrimljenogPreseka.Banka bPresek = semaFirma.getBrojacPoslednjegPrimljenogPreseka().getBankaByNaziv(senderName);
				dateFromXml = bPresek.getTimestamp();
				break;
			case "notifikacija":
				if(entity.equals("firma")) {
					FirmeSema.BrojacPoslednjePrimljeneNotifikacije.Firma fNotifikacija = semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getFirmaByNaziv(senderName);
					dateFromXml = fNotifikacija.getTimestamp();
				} else {
					FirmeSema.BrojacPoslednjePrimljeneNotifikacije.Banka fNotifikacija = semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getBankaByNaziv(senderName);
					dateFromXml = fNotifikacija.getTimestamp();
				}
				break;
			}
		} else if (semaBanka != null) {
			switch(type.toLowerCase()) {
			case "nalog":
				BankeSema.BrojacPoslednjegPrimljenogNaloga.Firma bNalog = semaBanka.getBrojacPoslednjegPrimljenogNaloga().getFirmaByNaziv(senderName);
				dateFromXml = bNalog.getTimestamp();
				break;
			case "izvod":
				BankeSema.BrojacPoslednjegPrimljenogZahtevaZaIzvod.Firma bIzvod = semaBanka.getBrojacPoslednjegPrimljenogZahtevaZaIzvod().getFirmaByNaziv(senderName);
				dateFromXml = bIzvod.getTimestamp();
				break;
			case "notifikacija":
				dateFromXml = semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().getTimestamp();
				break;
			}

		} else if (semaCentralna != null) {
			CentralnaSema.BrojacPoslednjegPrimljenogMTNaloga.Banka cMTNalog = semaCentralna.getBrojacPoslednjegPrimljenogMTNaloga().getBankaByNaziv(senderName);
			dateFromXml = cMTNalog.getTimestamp();
		}

		try {
			date = sdf.parse(dateFromXml);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static int getBrojPoslednjePoslatePoruke(FirmeSema semaFirma, BankeSema semaBanka, CentralnaSema semaCentralna, String type) {
		int rbrPorukeFromXml = 0;
		if(semaFirma != null) {
			switch(type.toLowerCase()) {
			case "faktura":
				rbrPorukeFromXml = semaFirma.getBrojacPoslednjePoslateFakture();
				System.out.println("SEMA FIRMA BROJAC ZA FAKTURU: "+rbrPorukeFromXml);
				break;
			case "nalog":
				rbrPorukeFromXml = semaFirma.getBrojacPoslednjegPoslatogNaloga();
				break;
			case "notifikacija":
				rbrPorukeFromXml = semaFirma.getBrojacPoslednjePoslateNotifikacije();
				break;
			case "izvod":
				rbrPorukeFromXml = semaFirma.getBrojacPoslednjegZahtevaZaIzvod();
				break;
			}
		} else if (semaBanka != null) {
			switch(type.toLowerCase()) {
			case "presek":
				rbrPorukeFromXml = semaBanka.getBrojacPoslednjegPoslatogPreseka();
				break;
			case "mtnalog":
				rbrPorukeFromXml = semaBanka.getBrojacPoslednjegPoslatogMTNaloga();
				break;
			case "notifikacija":
				rbrPorukeFromXml = semaBanka.getBrojacPoslednjePoslateNotifikacije();
				break;
			}

		} else if (semaCentralna != null) {
			switch(type.toLowerCase()) {
			case "notifikacija":
				rbrPorukeFromXml = semaCentralna.getBrojacPoslednjePoslateNotifikacije();
				break;
			case "mtnalog":
				rbrPorukeFromXml = semaCentralna.getBrojacPoslednjegPoslatogMTNaloga();
				break;
			}
		}

		return rbrPorukeFromXml;
	}



}
