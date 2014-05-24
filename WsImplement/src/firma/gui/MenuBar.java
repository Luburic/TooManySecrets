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
	
	private JMenu slanje;
	private JMenuItem posaljiFakturu;
	private JMenuItem posaljiNalog;
	private JMenuItem posaljiZizvod;
	private JMenuItem close;
	
	private JMenu kreirano;
	private JMenuItem kreiraneFakture;
	private JMenuItem kreiraniNalozi;
	private JMenuItem kreiraniIzvodi;
	
	private JMenu primljeno;
	private JMenuItem primljeneFakture;
	private JMenuItem primljeniNalozi;
	private JMenuItem primljeniIzvodi;
	
	
	public MenuBar() {
		
		slanje = new JMenu("Novo slanje");
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
		slanje.add(posaljiFakturu);
		slanje.add(posaljiNalog);
		slanje.add(posaljiZizvod);
		slanje.addSeparator();
		slanje.add(close);
		add(slanje);
		
		
		
		kreirano = new JMenu("Kreirano");
		
		kreiraneFakture = new JMenuItem();
		kreiraniNalozi = new JMenuItem();
		kreiraniIzvodi = new JMenuItem();
		
		
		kreiraneFakture.setText("Fakture");
		kreiraniNalozi.setText("Nalozi");
		kreiraniIzvodi.setText("Izvodi");
		
		kreirano.add(kreiraneFakture);
		kreirano.add(kreiraniNalozi);
		kreirano.add(kreiraniIzvodi);
		add(kreirano);
		
		
		
		
		
		
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
