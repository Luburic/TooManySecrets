package security;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.DocumentTransform;
import util.Validation;

//Potpisuje dokument, koristi se enveloped tip
public class SecurityClass {

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/tipovi";
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; 

	static {
		//staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public Document addTimestampAndSign(String alias, String password, String keystoreFile, String keystorePassword, String inputFile, String outputFile, int lastNo, String schemaLocation, String element){
		//ucitava se dokument
		Document doc;
		try {
			doc = loadDocument(inputFile);
		} catch (Exception e1) {
			throw Validation.generateSOAPFault("Bad created request.", SoapFault.FAULT_CODE_CLIENT,null);
		}

		Document forSign = reserialize(doc);

		Element faktura = (Element) forSign.getElementsByTagNameNS(TARGET_NAMESPACE, element).item(0);

		faktura.removeAttribute("xsi:schemaLocation");
		faktura.setAttribute("xsi:schemaLocation", TARGET_NAMESPACE+ schemaLocation);

		Element timestamp = forSign.createElementNS(TARGET_NAMESPACE, "timestamp");
		faktura.appendChild(timestamp);

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		String reportDate = sdf.format(date);
		timestamp.appendChild(forSign.createTextNode(reportDate));

		Element redniBrojPoruke = forSign.createElementNS(TARGET_NAMESPACE, "redniBrojPoruke");
		faktura.appendChild(redniBrojPoruke);
		redniBrojPoruke.appendChild(forSign.createTextNode(String.valueOf(lastNo)));

		//ucitava privatni kljuc
		PrivateKey pk = readPrivateKey(alias, password, keystoreFile, keystorePassword);
		//ucitava sertifikat
		Certificate cert = readCertificate(alias, password, keystoreFile, keystorePassword);


		//potpisuje
		System.out.println("Signing....");
		forSign = signDocument(forSign, pk, cert);

		if(verifySignature(forSign)) {
			//snima se dokument
			saveDocument(forSign, outputFile);
			return forSign;
		} else {

			return null;
		}
	}

	/**
	 * Kreira DOM od XML dokumenta
	 */
	public Document loadDocument(String file) throws Exception{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(file));

			return document;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Snima DOM u XML fajl 
	 */
	public void saveDocument(Document doc, String fileName) {
		try {
			File outFile = new File(fileName);
			FileOutputStream f = new FileOutputStream(outFile);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);

			transformer.transform(source, result);

			f.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ucitava sertifikat is KS fajla
	 */
	public Certificate readCertificate(String alias, String password, String keystoreFile, String keystorePassword) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keystoreFile));
			ks.load(in, keystorePassword.toCharArray());

			if(ks.isKeyEntry(alias)) {
				Certificate cert = ks.getCertificate(alias);
				return cert;

			}
			else
				return null;

		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public Certificate readCertificateFromFile(File f) throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		Certificate cert = null;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		while (in.available() > 0) {
			cert = cf.generateCertificate(in);
		}
		in.close();

		return cert;
	}

	/**
	 * Ucitava privatni kljuc is KS fajla
	 */
	public PrivateKey readPrivateKey(String alias, String password, String keystoreFile, String keystorePassword) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keystoreFile));
			ks.load(in, keystorePassword.toCharArray());

			if(ks.isKeyEntry(alias)) {
				PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
				return pk;
			}
			else
				return null;

		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return null;
		} 
	}

	private Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) {

		try {
			Element rootEl = doc.getDocumentElement();

			//kreira se signature objekat
			XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			//kreiraju se transformacije nad dokumentom
			Transforms transforms = new Transforms(doc);

			//iz potpisa uklanja Signature element
			//Ovo je potrebno za enveloped tip po specifikaciji
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			//normalizacija
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

			//potpisuje se citav dokument (URI "")
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

			//U KeyInfo se postavalja Javni kljuc samostalno i citav sertifikat
			sig.addKeyInfo(cert.getPublicKey());
			sig.addKeyInfo((X509Certificate) cert);

			//poptis je child root elementa
			rootEl.appendChild(sig.getElement());

			//potpisivanje
			sig.sign(privateKey);

			return doc;

		} catch (TransformationException e) {
			e.printStackTrace();
			return null;
		} catch (XMLSignatureException e) {
			e.printStackTrace();
			return null;
		} catch (DOMException e) {
			e.printStackTrace();
			return null;
		} catch (XMLSecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean verifySignature(Document doc) {

		try {
			//Pronalazi se prvi Signature element 
			NodeList signatures = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
			Element signatureEl = (Element) signatures.item(0);

			//kreira se signature objekat od elementa
			XMLSignature signature = new XMLSignature(signatureEl, null);
			//preuzima se key info
			KeyInfo keyInfo = signature.getKeyInfo();
			//ako postoji
			if(keyInfo != null) {
				//registruju se resolver-i za javni kljuc i sertifikat
				keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
				keyInfo.registerInternalKeyResolver(new X509CertificateResolver());

				//ako sadrzi sertifikat
				if(keyInfo.containsX509Data() && keyInfo.itemX509Data(0).containsCertificate()) { 
					Certificate cert = keyInfo.itemX509Data(0).itemCertificate(0).getX509Certificate();
					//ako postoji sertifikat, provera potpisa
					if(cert != null) 
						return signature.checkSignatureValue((X509Certificate) cert);
					else
						return false;
				}
				else
					return false;
			}
			else
				return false;

		} catch (XMLSignatureException e) {
			e.printStackTrace();
			return false;
		} catch (XMLSecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Document reserialize(Node request) {
		Document r = null;
		try{
			DocumentBuilder db = DocumentTransform.getDocumentBuilder();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			DOMSource src = new DOMSource(request);
			transformer.transform(src, result);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			r = db.parse(bais);
		}catch(Exception e){
			e.printStackTrace();
		}
		return r;
	}

	/**
	 * Generise tajni kljuc
	 */
	public static SecretKey generateDataEncryptionKey() {

		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			return keyGenerator.generateKey();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Kriptuje sadrzaj prvog elementa
	 */
	public Document encrypt(Document doc, SecretKey key, Certificate certificate, String namespace, String node) {

		try {
			//cipher za kriptovanje tajnog kljuca,
			//Koristi se Javni RSA kljuc za kriptovanje
			XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
			//inicijalizacija za kriptovanje tajnog kljuca javnim RSA kljucem
			keyCipher.init(XMLCipher.WRAP_MODE, certificate.getPublicKey());
			EncryptedKey encryptedKey = keyCipher.encryptKey(doc, key);

			//cipher za kriptovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
			//inicijalizacija za kriptovanje
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, key);

			//u EncryptedData elementa koji se kriptuje kao KeyInfo stavljamo kriptovan tajni kljuc
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			//kreira se KeyInfo
			KeyInfo keyInfo = new KeyInfo(doc);
			keyInfo.addKeyName("Kriptovani tajni kljuc");
			//postavljamo kriptovani kljuc
			keyInfo.add(encryptedKey);
			//postavljamo KeyInfo za element koji se kriptuje
			encryptedData.setKeyInfo(keyInfo);

			//trazi se element ciji sadrzaj se kriptuje
			NodeList elements = doc.getElementsByTagNameNS(namespace,node);

			Element element = (Element) elements.item(0);

			xmlCipher.doFinal(doc, element, true); //kriptuje sa sadrzaj

			return doc;

		} catch (XMLEncryptionException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public Document decrypt(Document doc, PrivateKey privateKey) {

		try {
			//cipher za dekritpovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance();
			//inicijalizacija za dekriptovanje
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
			//postavlja se kljuc za dekriptovanje tajnog kljuca
			xmlCipher.setKEK(privateKey);

			//trazi se prvi EncryptedData element
			NodeList encDataList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
			Element encData = (Element) encDataList.item(0);

			//dekriptuje se
			//pri cemu se prvo dekriptuje tajni kljuc, pa onda njime podaci
			xmlCipher.doFinal(doc, encData); 

			return doc;
		} catch (XMLEncryptionException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getOwner(Document doc) {
		try {
			//pronalazi se zeljeni ds:KeyInfo element
			Element keyInfoEl = (Element)doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);

			//kreira se KeyInfo objekat na osnovu ds:KeyInfo elementa
			KeyInfo keyInfo = new KeyInfo(keyInfoEl, null);
			//registracija resolvera
			keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
			keyInfo.registerInternalKeyResolver(new X509CertificateResolver());
			//citanje sertifikata
			X509Certificate cert = keyInfo.itemX509Data(0).itemCertificate(0).getX509Certificate();
			
			String dn = cert.getSubjectX500Principal().getName();
			String ownerName = null;
			
			LdapName ln = new LdapName(dn);

	        for (Rdn rdn : ln.getRdns()) {
	            if (rdn.getType().equalsIgnoreCase("CN")) {
	            	ownerName = rdn.getValue().toString();
	                break;
	            }
	        }
			
			if(ownerName != null)
				return ownerName;
			
		} catch (XMLSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
