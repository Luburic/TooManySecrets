package gui.tables;

import java.security.cert.X509Certificate;
import java.util.List;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class CertificatesTableModel extends DefaultTableModel {
	
	public CertificatesTableModel(List<X509Certificate> certs){
		super(new String[] {"Serial number", "Certificate"},0);
		fillData(certs);		
	}
	
	public void fillData(List<X509Certificate> certs){
		setRowCount(0);
		for(X509Certificate cert : certs){
			addRow(new Object[]{cert.getSerialNumber(),cert});
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

}
