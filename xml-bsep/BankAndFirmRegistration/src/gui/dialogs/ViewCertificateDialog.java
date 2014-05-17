package gui.dialogs;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ViewCertificateDialog extends JDialog {

	JLabel version = new JLabel("Version :");
	JTextField versionData = new JTextField();	
	JLabel issuer = new JLabel("ISSUER :");
	JTextField issuerData;	
	JLabel subject = new JLabel("SUBJECT :");
	JTextField  subjectData;
	JLabel validFrom = new JLabel("Valid From :");
	JTextField validFromData;
	JLabel validTo = new JLabel("Valid To :");
	JTextField validToData;
	JLabel serial = new JLabel("Serial Number :");
	JTextField serialData;
	JLabel pk = new JLabel("Public Key :");
	JTextField pkData;
	JLabel sigAlg = new JLabel("Signature Algorithm :");
	JTextField sigAlgData;


	public ViewCertificateDialog (Certificate c){
		
		setModal(true);
		setTitle("Certificate");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		MigLayout layout = new MigLayout(
				"",
				"20[]10[]20",
				"20[]20[]10[]10[]10[]10[]10[]10[]20");
		setLayout(layout);

		X509Certificate cert = (X509Certificate) c;

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy , hh:mm");

		add(version);
		versionData.setText(" X.509 v"+cert.getVersion()+" ");
		versionData.setEditable(false);
		add(versionData,"wrap");

		add(issuer);
		issuerData = new JTextField(" "+cert.getIssuerDN().toString()+" ");
		issuerData.setEditable(false);
		add(issuerData,"wrap");

		add(subject);
		subjectData = new JTextField(" "+cert.getSubjectDN().toString()+" ");
		subjectData.setEditable(false);
		add(subjectData,"wrap");

		add(validFrom);
		validFromData = new JTextField(" "+sdf.format(cert.getNotBefore())+" ");
		validFromData.setEditable(false);
		add(validFromData,"wrap");

		add(validTo);
		validToData = new JTextField(" "+sdf.format(cert.getNotAfter())+" ");
		validToData.setEditable(false);
		add(validToData,"wrap");

		add(serial);
		serialData = new JTextField(" "+cert.getSerialNumber().toString()+" ");
		serialData.setEditable(false);
		add(serialData,"wrap");

		add(pk);
		pkData = new JTextField(" "+cert.getPublicKey().getAlgorithm()+" ");
		pkData.setEditable(false);
		add(pkData,"wrap");

		add(sigAlg);
		sigAlgData = new JTextField(" "+cert.getSigAlgName()+" ");
		sigAlgData.setEditable(false);
		add(sigAlgData,"wrap");

		pack();
		setLocationRelativeTo(null);

	}


}
