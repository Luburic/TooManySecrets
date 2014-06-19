package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import actions.LoadCertificateAction;
import actions.NewCertificateAction;
import actions.ViewBanksAction;
import actions.ViewCertificateAction;
import actions.ViewFirmsAction;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	
	private JMenu centralBank;
	private JMenuItem newSelfSignedCertificate;
	private JMenuItem viewSelfSignedCertificate;
	private JMenuItem loadSelfSignedCertificate;
	private JMenuItem close;
	
	private JMenu registration;
	private JMenuItem registerBank;
	private JMenuItem registerFirm;

	
	public MenuBar() {
		
		centralBank = new JMenu("Central bank");
		newSelfSignedCertificate = new JMenuItem(new NewCertificateAction());
		viewSelfSignedCertificate = new JMenuItem(new ViewCertificateAction());
		loadSelfSignedCertificate = new JMenuItem(new LoadCertificateAction());
		close = new JMenuItem("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(MainFrame.getInstance(),
						"Are you sure?", "Application closing",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				
			}
		});
		
		centralBank.add(newSelfSignedCertificate);
		centralBank.add(viewSelfSignedCertificate);
		centralBank.add(loadSelfSignedCertificate);
		centralBank.addSeparator();
		centralBank.add(close);
		add(centralBank);
		
		registration = new JMenu("Registration");
		registerBank = new JMenuItem(new ViewBanksAction());
		registerFirm = new JMenuItem(new ViewFirmsAction());
		
		registration.add(registerBank);
		registration.add(registerFirm);
		add(registration);
		
		loadSelfSignedCertificate.setVisible(false);
	}
	

}
