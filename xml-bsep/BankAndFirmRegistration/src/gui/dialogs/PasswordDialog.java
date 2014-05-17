package gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class PasswordDialog extends JDialog {

	private JLabel lbl1 = new JLabel("Pass:");
	private JPasswordField jpf1 = new JPasswordField();

	private JLabel lbl2 = new JLabel("Retype pass:");
	private JPasswordField jpf2 = new JPasswordField();

	private JLabel lbalias = new JLabel("Alias:");
	private JTextField tfalias = new JTextField();

	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");
	private boolean ok =  false;
	private char[] pass = null;
	private String alias = null;

	public PasswordDialog() {

		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new MigLayout());		
		ok =  false;
		setTitle("Data for key pair");

		JPanel panel = new JPanel(new MigLayout("","[][]","10[][][]10"));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Type data for key pair :"));
		panel.add(lbalias);
		panel.add(tfalias,"wrap, w 150!");
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

		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}			
		});

		btnOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String sfr1 = new String(jpf1.getPassword());
				String sfr2 = new String(jpf2.getPassword());
				String alis = tfalias.getText().trim();
				if(alis.equals("")){
					JOptionPane.showMessageDialog(null, "Enter alias for the key pair!");
					tfalias.requestFocus();
					return;
				}	
				if(sfr1.equals("")){
					JOptionPane.showMessageDialog(null, "Type the password!");
					jpf1.requestFocus();
					return;
				}
				if(!sfr1.equals(sfr2)){
					JOptionPane.showMessageDialog(null, "Passwords don't match!");
					jpf1.requestFocus();
					return;
				}
				alias = alis;
				pass = jpf1.getPassword();
				ok = true;
				setVisible(false);
			}			
		});

	}		

	public boolean isOk() {
		return ok;
	}


	public char[] getPass() {
		return pass;
	}

	public String getAlias() {
		return alias;
	}


}
