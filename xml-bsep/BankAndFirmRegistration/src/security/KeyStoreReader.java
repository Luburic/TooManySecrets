package security;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import util.Constants;

/**
 * 
 * Cita is keystore fajla
 */
public class KeyStoreReader {

	Certificate cert = null;
	
	private char[] password = null;
	
	public KeyStoreReader() {
		
		
	}	
	
	public void testIt() {
		try {
			readKeyStore();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PublicKey readPublicKey() {
		return cert.getPublicKey();
	}
	
	private void readKeyStore() throws ParseException{
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(Constants.file));
			ks.load(in, password);

		
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public KeyStore readKeyStore(File f, char[] p) throws IOException{
		try{
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
			ks.load(in, p);
			return ks;
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
		}
		return null;
	}
	
	public IssuerData readKeyStoreForIssuer(boolean isCentralBank, String alias, char[] pass) throws ParseException {
		IssuerData issuer = null;
		try {
			// kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			// ucitavamo podatke
			BufferedInputStream in = null;
			
			if(isCentralBank) {
				in = new BufferedInputStream(new FileInputStream(Constants.file));
			} else {
				in = new BufferedInputStream(new FileInputStream("./keystores/"+alias+".jks"));
			}
			ks.load(in, pass);
			// citamo par sertifikat privatni kljuc
			System.out.println("Cita se Sertifikat...");
			
			System.out.println("Ucitani ertifikat:");
			cert = ks.getCertificate(FilenameUtils.getBaseName(alias));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(cert);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			PrivateKey privKey = null;
			if(!isCentralBank) {
				
				/*LoadIssuerDialog issuerDialog = new LoadIssuerDialog();
				issuerDialog.setVisible(true);
				if(!issuerDialog.isOk()){
					issuerDialog.dispose();
				}*/
				
				privKey = (PrivateKey) ks.getKey(FilenameUtils.getBaseName(alias), alias.toCharArray());
			} else {
				
				privKey = (PrivateKey) ks.getKey(FilenameUtils.getBaseName(alias), pass);
			}

			X500Name issuerName = new JcaX509CertificateHolder(
					(X509Certificate) cert).getSubject();
			issuer = new IssuerData(privKey, issuerName);
			

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+". Wrong password?","ERROR",JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return issuer;

	}

	
	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}
}
