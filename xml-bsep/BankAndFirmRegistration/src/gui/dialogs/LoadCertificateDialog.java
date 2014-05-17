package gui.dialogs;

import gui.MainFrame;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.bouncycastle.asn1.x500.X500Name;

import security.IssuerData;
import security.KeyStoreReader;
import util.Constants;

@SuppressWarnings("serial")
public class LoadCertificateDialog extends JDialog {

	private JLabel lbPath = new JLabel("KeyStore file :");
	private JLabel lbPassKs = new JLabel("KeyStore password :");
	private JLabel lbAlias = new JLabel("Alias :");
	private JLabel lbPassPk = new JLabel("PrivateKey password :");

	private JTextField tfPath;
	private JPasswordField ptfPassKs;
	private JTextField tfAlias;
	private JPasswordField ptfPassPk;

	private JButton btnBrowse = new JButton("Browse...");
	private JButton btnOK = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	private static IssuerData issuer;
	private static boolean initialized = false;


	public LoadCertificateDialog(MainFrame mf) {

		super(mf, "Load central bank certificate", Dialog.ModalityType.APPLICATION_MODAL);
		initializeIssuer();		
		setSize(420,240);
		setResizable(false);
		setLocationRelativeTo(mf);
		MigLayout layout = new MigLayout(
				"",
				"10[]10[]5[]10",
				"20[]10[]10[]10[]30[]20");
		setLayout(layout);

		add(lbPath);
		tfPath = new JTextField(Constants.file.getAbsolutePath());
		tfPath.setMinimumSize(new Dimension(150,20));
		add(tfPath);

		add(btnBrowse,"wrap");

		add(lbPassKs);
		String iksPass = new String(Constants.ksPass);
		ptfPassKs = new JPasswordField(iksPass);
		ptfPassKs.setMinimumSize(new Dimension(150,20));
		add(ptfPassKs,"wrap");	

		add(lbAlias);
		tfAlias = new JTextField(Constants.alias);
		tfAlias.setMinimumSize(new Dimension(150,20));
		add(tfAlias,"wrap");

		add(lbPassPk);
		String ipkPass = new String(Constants.pkPass);
		ptfPassPk = new JPasswordField(ipkPass);
		ptfPassPk.setMinimumSize(new Dimension(150,20));
		add(ptfPassPk,"wrap");

		add(btnOK, "gapleft 85");
		add(btnCancel);

		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}			
		});

		btnOK.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String path = tfPath.getText();	
				File iFile = new File(path);
				String iAlias = tfAlias.getText();
				char[] iPassKs = ptfPassKs.getPassword();
				char[] iPassPk = ptfPassPk.getPassword();

				if(!iFile.exists()){
					JOptionPane.showMessageDialog(null, "Specified file doesn't exist!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfPath.requestFocus();
					return;
				}
				KeyStoreReader ksRead = new KeyStoreReader();
				KeyStore ks = null;
				try {
					ks = ksRead.readKeyStore(iFile, iPassKs);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
					tfPath.requestFocus();
					return;
				}
				try {
					if(!ks.containsAlias(iAlias)){
						JOptionPane.showMessageDialog(null, "No entries found with given alias!","ERROR",JOptionPane.ERROR_MESSAGE);
						tfAlias.requestFocus();
						return;
					}
					if(!ks.isKeyEntry(iAlias)){
						JOptionPane.showMessageDialog(null, "Entry with alias "+iAlias+" doesn't contain private key!","ERROR",JOptionPane.ERROR_MESSAGE);
						tfAlias.requestFocus();
						return;
					}

					PrivateKey pk = (PrivateKey)ks.getKey(iAlias, iPassPk);
					X509Certificate cert = (X509Certificate) ks.getCertificate(iAlias);
					issuer = new IssuerData(pk,new X500Name(cert.getIssuerX500Principal().getName()));

				} catch (UnrecoverableKeyException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage()+". Wrong password?","ERROR",JOptionPane.ERROR_MESSAGE);
					ptfPassPk.requestFocus();
					return;
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (KeyStoreException e1) {
					e1.printStackTrace();
				}	
				Constants.file = iFile;
				Constants.alias = iAlias;
				Constants.ksPass = iPassKs;
				Constants.pkPass = iPassPk;				

				getIssuer();
				setVisible(false);
				dispose();
			}			
		});

		btnBrowse.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				FileNameExtensionFilter fileFIlter =new FileNameExtensionFilter("JKS files (.jks)", "jks");
				c.addChoosableFileFilter(fileFIlter);
				c.setCurrentDirectory(new File("./keystores"));
				int status = c.showOpenDialog(null);
				if (status == JFileChooser.APPROVE_OPTION) 
					if (c.getSelectedFile().exists()){
						tfPath.setText(c.getSelectedFile().getAbsolutePath());
						ptfPassKs.setText("");
						tfAlias.setText("");
						ptfPassPk.setText("");
						
					}
			}			
		});		
	}


	public static IssuerData getIssuer() {

		if(!initialized)
			initializeIssuer();	
		return issuer;
	}

	private static void initializeIssuer() {

		try {
			KeyStoreReader ksRead = new KeyStoreReader();
			KeyStore ks;	
			ks = ksRead.readKeyStore(Constants.file, Constants.ksPass);
			PrivateKey pk = (PrivateKey)ks.getKey(Constants.alias, Constants.pkPass);
			X509Certificate cert = (X509Certificate)ks.getCertificate(Constants.alias);
			issuer = new IssuerData(pk,new X500Name(cert.getSubjectDN().getName()));
			initialized = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public static X509Certificate getIssuerCertificate() {

		KeyStoreReader ksRead = new KeyStoreReader();
		KeyStore ks = null;
		X509Certificate cert = null;
		try {
			ks = ksRead.readKeyStore(Constants.file, Constants.ksPass);
			cert =  (X509Certificate) ks.getCertificate(Constants.alias);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return cert;
	}

	public static void main(String[] args) {
		initializeIssuer();
	}




}
