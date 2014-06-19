package gui.dialogs;

import gui.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import banks.Bank;
import banks.RegisteredBanks;

@SuppressWarnings("serial")
public class FirmsMenuDialog extends JDialog {
	
	private JLabel lbl1 = new JLabel("Bank:");
	private JComboBox<String> cmbBox;


	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	private RegisteredBanks registeredBanks;

	public FirmsMenuDialog() {

		setModal(true);
		setLayout(new MigLayout());
		setTitle("Choose bank...");

		JPanel panel = new JPanel(new MigLayout("","[][]","10[][][]10"));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Choose bank:"));
		panel.add(lbl1);



		registeredBanks = RegisteredBanks.load();
		Vector<String> banks = new Vector<>();
		
		for(Bank b : registeredBanks.getBank()) {
			banks.add(b.getName());
		}
		
		cmbBox = new JComboBox<>(banks);
		panel.add(cmbBox);
		
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
				dispose();
			}			
		});

		btnOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				String bankName = (String)cmbBox.getSelectedItem();
				FirmsDialog firmsDialog = new FirmsDialog(MainFrame.getInstance(), bankName);
				firmsDialog.setVisible(true);
				

			}			
		});
	}


}
