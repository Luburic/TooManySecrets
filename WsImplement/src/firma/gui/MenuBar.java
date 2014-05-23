package firma.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import firma.gui.actions.FakturaAction;
import firma.gui.actions.IzvodAction;
import firma.gui.actions.NalogAction;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
	
	private JMenu firma;
	private JMenuItem firmaInfo;
	private JMenuItem posaljiFakturu;
	private JMenuItem posaljiNalog;
	private JMenuItem posaljiZizvod;
	private JMenuItem close;

	
	public MenuBar() {
		
		firma = new JMenu("Servisi");
		posaljiFakturu = new JMenuItem(new FakturaAction());
		posaljiNalog = new JMenuItem(new NalogAction());
		posaljiZizvod = new JMenuItem(new IzvodAction());
		
		close = new JMenuItem("Zatvori");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(MainFrame.getInstance(),
						"Da li ste sigurni?", "Zatvaranje aplikacije",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				
			}
		});
		
		firma.add(posaljiFakturu);
		firma.add(posaljiNalog);
		firma.add(posaljiZizvod);
		firma.addSeparator();
		firma.add(close);
		add(firma);
		
		
	}
	

}
