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
	
	private JMenu novo;
	private JMenuItem posaljiFakturu;
	private JMenuItem posaljiNalog;
	private JMenuItem posaljiZizvod;
	private JMenuItem close;

	private JMenu primljeno;
	private JMenuItem primljeneFakture;
	private JMenuItem primljeniNalozi;
	private JMenuItem primljeniIzvodi;
	
	
	public MenuBar() {
		
		novo = new JMenu("Novo");
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
		novo.add(posaljiFakturu);
		novo.add(posaljiNalog);
		novo.add(posaljiZizvod);
		novo.addSeparator();
		novo.add(close);
		add(novo);
	
		
		primljeno = new JMenu("Primljeno");
		
		primljeneFakture = new JMenuItem();
		primljeniNalozi = new JMenuItem();
		primljeniIzvodi = new JMenuItem();
		
		
		primljeneFakture.setText("Fakture");
		primljeniNalozi.setText("Nalozi");
		primljeniIzvodi.setText("Izvodi");
		
		primljeno.add(primljeneFakture);
		primljeno.add(primljeniNalozi);
		primljeno.add(primljeniIzvodi);
		add(primljeno);
		
	
	}
	

}
