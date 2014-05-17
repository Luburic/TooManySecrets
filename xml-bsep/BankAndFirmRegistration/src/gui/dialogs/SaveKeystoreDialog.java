package gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SaveKeystoreDialog extends JDialog {

	private JLabel lbl1 = new JLabel("Password:");
	private JPasswordField jpf1 = new JPasswordField();

	private JLabel lbl2 = new JLabel("Retype password:");
	private JPasswordField jpf2 = new JPasswordField();

	private JLabel lbfile = new JLabel("File name:");
	private JTextField tffile = new JTextField();

	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	private char[] pass = null;
	private String file = null;

	public SaveKeystoreDialog(String alias) {

		setModal(true);
		setLayout(new MigLayout());
		setTitle("Save keystore as..");

		JPanel panel = new JPanel(new MigLayout("","[][]","10[][][]10"));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Type data KeyStore file:"));
		panel.add(lbfile);
		panel.add(tffile,"wrap, w 150!");
		panel.add(lbl1);
		panel.add(jpf1,"wrap, w 150!");
		panel.add(lbl2);
		panel.add(jpf2,"wrap, w 150!");


		JPanel okP = new JPanel(new MigLayout("","[]","[][]"));
		okP.add(panel, "wrap");
		okP.add(btnOk, "split 2");
		okP.add(btnCancel);		

		add(panel, "wrap");
		add(okP, "gapleft 55, wrap");
		pack();

		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		File tempFile = new File("./keystores/"+alias+".jks");
		if(!tempFile.exists()) {
			tffile.setText(alias);
			tffile.setEditable(false);
		}

		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}			
		});

		btnOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String sfr1 = new String(jpf1.getPassword());
				String sfr2 = new String(jpf2.getPassword());				
				if(sfr1.equals("")){
					JOptionPane.showMessageDialog(null, "Enter the password for key store!");
					jpf1.requestFocus();
					return;
				}
				if(!sfr1.equals(sfr2)){
					JOptionPane.showMessageDialog(null, "Passwords don't match!");
					jpf1.requestFocus();
					return;
				}
				file = tffile.getText().trim();
				pass = jpf1.getPassword();
				
				setVisible(false);
				dispose();
			}			
		});

	}

	public char[] getPass() {
		return pass;
	}

	public void setPass(char[] pass) {
		this.pass = pass;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}





}
