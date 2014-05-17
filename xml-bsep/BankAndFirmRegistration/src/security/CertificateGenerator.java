package security;

import gui.dialogs.PasswordDialog;
import gui.dialogs.SaveKeystoreDialog;

import java.io.File;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import util.Constants;

public class CertificateGenerator {
	
	//registracija providera
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public X509Certificate generateCertificate(IssuerData issuerData, SubjectData subjectData) {
		 try {
			 
			 //posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc
			 //pravi se builder za objekat koji ce sadrzati privatni kljuc i koji 
			 //ce se koristitit za potpisivanje sertifikata
			 //parametar je koji algoritam se koristi za potpisivanje sertifiakta
			 JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA1withRSA");
			 //koji provider se koristi
			 builder = builder.setProvider("BC");
			 
			 //objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
			 ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
			 
			 //postavljaju se podaci za generisanje sertifiakta
			 X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
					 															new BigInteger(subjectData.getSerialNumber()),
					 															subjectData.getStartDate(),
					 															subjectData.getEndDate(),
					 															subjectData.getX500name(),
					 															subjectData.getPublicKey());
			 //generise se sertifikat
			 X509CertificateHolder certHolder = certGen.build(contentSigner);
			 
			 //certGen generise sertifikat kao objekat klase X509CertificateHolder
			 //sad je potrebno certHolder konvertovati u sertifikat
			 //za to se koristi certConverter
			 JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			 certConverter = certConverter.setProvider("BC");
			 
			 //konvertuje objekat u sertifikat i vraca ga
			 return certConverter.getCertificate(certHolder);
			 
		 } catch (CertificateEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (OperatorCreationException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public KeyPair generateKeyPair() {
		try {
			//generator para kljuceva
			KeyPairGenerator   keyGen = KeyPairGenerator.getInstance("RSA");
			//generator pseudo slucajnih brojeva
			SecureRandom       random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			//inicijalizacija generatora, 1024 bitni kljuc
			keyGen.initialize(1024, random);
			
			//generise par kljuceva
			KeyPair pair = keyGen.generateKeyPair();
			
			return pair;
			
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void generateSelfSignedCertificate(String commonName, String orgName, String orgUnit, String country, String locality,
			String email, String uid, int days) {
		
		KeyStoreWriter ksWriter = new KeyStoreWriter();					
		KeyPair keyPair = generateKeyPair();

		Date startDate = Calendar.getInstance().getTime();

		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, days);
		Date endDate = now.getTime();

		String sn = String.valueOf((int) (100*Math.random()));
		
		//osnovni podaci za issuer-a i subject-a
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, commonName);
		//builder.addRDN(BCStyle.SURNAME, surname);
		//builder.addRDN(BCStyle.GIVENNAME, givenName);
		builder.addRDN(BCStyle.O, orgName);
		builder.addRDN(BCStyle.OU, orgUnit);
		builder.addRDN(BCStyle.C, country);
		builder.addRDN(BCStyle.L, locality);
		builder.addRDN(BCStyle.E, email);
		builder.addRDN(BCStyle.UID, uid);

		
		//kreiraju se podaci za issuer-a
		IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
		//kreiraju se podaci za vlasnika
		SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);

		X509Certificate cert = generateCertificate(issuerData, subjectData);

		if(Constants.alias == null)
			return;

		char[] pass =  null;
		PasswordDialog pd = new PasswordDialog(); //password za alias
		pd.setVisible(true);
		if(!pd.isOk()){
			pd.dispose();
			return;
		}
		String alias = pd.getAlias();
		pass = pd.getPass();
		pd.dispose();
		
		
		SaveKeystoreDialog sd = new SaveKeystoreDialog(alias);
		sd.setVisible(true);	
		
		File f = new File("./keystores/"+sd.getFile()+".jks");
		if(!f.exists()) {
			ksWriter.loadKeyStore(null, sd.getPass());
		} else {
			ksWriter.loadKeyStore(sd.getFile(), sd.getPass());
		}
		ksWriter.write(alias, keyPair.getPrivate(), pass, cert);
		ksWriter.saveKeyStore("./keystores/"+sd.getFile()+".jks", sd.getPass());
		JOptionPane.showMessageDialog(null, "Keystore "+sd.getFile()+".jks is saved in keystores/ folder.");
		

		//ovde bi trebalo da prodje
		try {
			cert.verify(keyPair.getPublic());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("VALIDACIJA USPESNA....");
	
	}
	
	//bio je void
	public X509Certificate generateChainedCertificate(String commonName, String orgName, String orgUnit, String country, String locality,
			String email, String uid, int days, boolean isCentralBank) {
		
		KeyStoreWriter ksWriter = new KeyStoreWriter();
		KeyStoreReader keyStoreReader = new KeyStoreReader();
		KeyPair keyPair = generateKeyPair();

		Date startDate = Calendar.getInstance().getTime();

		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, days);
		Date endDate = now.getTime();

		String sn = String.valueOf((int) (100*Math.random()));
		
		//osnovni podaci za issuer-a i subject-a
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, commonName);
		//builder.addRDN(BCStyle.SURNAME, surname);
		//builder.addRDN(BCStyle.GIVENNAME, givenName);
		builder.addRDN(BCStyle.O, orgName);
		builder.addRDN(BCStyle.OU, orgUnit);
		builder.addRDN(BCStyle.C, country);
		builder.addRDN(BCStyle.L, locality);
		builder.addRDN(BCStyle.E, email);
		builder.addRDN(BCStyle.UID, uid);

		
		

		char[] pass =  null;
		PasswordDialog pd = new PasswordDialog();
		pd.setVisible(true);
		if(!pd.isOk()){
			pd.dispose();
			return null;
		}
		String alias = pd.getAlias();
		pass = pd.getPass();
		pd.dispose();
		
		// ucitavaju se podaci za issuer-a iz keystore fajla
		IssuerData issuerData = null;
		try {
			if(isCentralBank) {
				issuerData = keyStoreReader.readKeyStoreForIssuer(isCentralBank, Constants.alias, Constants.ksPass);
			} else {
				PasswordDialog passDialog = new PasswordDialog();
				passDialog.setVisible(true);
				issuerData = keyStoreReader.readKeyStoreForIssuer(isCentralBank, passDialog.getAlias(), passDialog.getPass() ); //dodati dialog
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//kreiraju se podaci za vlasnika
		SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);

		X509Certificate cert = generateCertificate(issuerData, subjectData);

		
		SaveKeystoreDialog sd = new SaveKeystoreDialog(alias);
		sd.setVisible(true);	
		
		File f = new File("./keystores/"+sd.getFile()+".jks");
		if(!f.exists()) {
			ksWriter.loadKeyStore(null, sd.getPass());
		} else {
			ksWriter.loadKeyStore("./keystores/"+sd.getFile()+".jks", sd.getPass());
		}
		ksWriter.write(alias, keyPair.getPrivate(), pass, cert);
		ksWriter.saveKeyStore("./keystores/"+sd.getFile()+".jks", sd.getPass());
		JOptionPane.showMessageDialog(null, "Keystore "+sd.getFile()+".jks is saved in keystores/ folder.");
		
		
		// ispis na konzolu
		System.out
				.println("ISSUER: " + cert.getIssuerX500Principal().getName());
		System.out.println("SUBJECT: "
				+ cert.getSubjectX500Principal().getName());
		System.out.println("Sertifikat:");
		System.out
				.println("-------------------------------------------------------");
		System.out.println(cert);
		System.out
				.println("-------------------------------------------------------");
		//ovde bi trebalo da prodje
		try {
			cert.verify(keyStoreReader.readPublicKey());

		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("VALIDACIJA USPESNA....");	
		return cert;
	}
	
}
