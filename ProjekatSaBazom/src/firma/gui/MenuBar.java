package firma.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import util.ConstantsXWS;
import util.accessControl.TAkcija;
import firma.gui.actions.FakturaAction;
import firma.gui.actions.FakturaViewAction;
import firma.gui.actions.IzvodAction;
import firma.gui.actions.NalogAction;
import firma.gui.actions.NalogViewAction;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	private JMenu novo;
	private JMenuItem posaljiFakturu;
	private JMenuItem posaljiNalog;
	private JMenuItem posaljiZizvod;
	private JMenuItem pregledFaktura;
	private JMenuItem pregledNaloga;
	private JMenuItem close;

	public MenuBar() {
		novo = new JMenu("Fakture i nalozi");
		posaljiFakturu = new JMenuItem(new FakturaAction());
		posaljiNalog = new JMenuItem(new NalogAction());
		posaljiZizvod = new JMenuItem(new IzvodAction());
		pregledNaloga = new JMenuItem(new NalogViewAction());
		pregledFaktura = new JMenuItem(new FakturaViewAction());

		close = new JMenuItem("Zatvori");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Da li ste sigurni?",
						"Zatvaranje aplikacije", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		refreshMenu();
		novo.add(posaljiZizvod);
		novo.addSeparator();
		novo.add(close);
		add(novo);
	}

	public void refreshMenu() {
		novo.remove(posaljiFakturu);
		novo.remove(posaljiNalog);
		novo.remove(pregledFaktura);
		novo.remove(pregledNaloga);
		for (TAkcija akcija : ConstantsXWS.AKTIVNA_ROLA.getAkcije().getAkcija()) {
			if (akcija.getNazivAkcije().equals("unosFakture")) {
				novo.add(posaljiFakturu);
			}
			if (akcija.getNazivAkcije().equals("unosNaloga")) {
				novo.add(posaljiNalog);
			}
			if (akcija.getNazivAkcije().equals("odbijanjeFakture")) {
				novo.add(pregledFaktura);
			}
			if (akcija.getNazivAkcije().equals("odbijanjeNaloga")) {
				novo.add(pregledNaloga);
			}
		}
	}
}
