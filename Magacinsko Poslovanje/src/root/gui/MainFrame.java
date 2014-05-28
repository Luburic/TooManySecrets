package root.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import root.dbConnection.DBConnection;
import root.gui.action.dialog.ArtikalAction;
import root.gui.action.dialog.ClanKomisijeAction;
import root.gui.action.dialog.DrzaveAction;
import root.gui.action.dialog.GodinaAction;
import root.gui.action.dialog.GrupaArtiklaAction;
import root.gui.action.dialog.MagacinskaKarticaAction;
import root.gui.action.dialog.NaseljenoMestoAction;
import root.gui.action.dialog.OrganizacionaJedinicaAction;
import root.gui.action.dialog.PopisniDokumentAction;
import root.gui.action.dialog.PoslovniPartnerAction;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.PreduzeceGodinaAction;
import root.gui.action.dialog.PrometniDokumentAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.action.dialog.StavkaPopisaAction;
import root.gui.action.dialog.VrstaPrometaAction;
import root.util.Constants;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 3706293806794500307L;

	private static MainFrame instance = null;
	private JMenuBar menuBar;

	private MainFrame() {
		MigLayout migLayout = new MigLayout("wrap 2");
		setLayout(migLayout);

		setTitle("Magacinsko poslovanje - Tim 4");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Da li ste sigurni?",
						"Izlazite iz aplikacije", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					DBConnection.close();
					System.exit(0);
				}
			}
		});

		setUpFrame();

		this.setSize(430, 430);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}

	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	public void setUpFrame() {
		for (Component c : this.getContentPane().getComponents()) {
			if (c instanceof JButton) {
				this.remove(c);
			}
		}

		menuBar = new JMenuBar();
		JMenu orgSemaMenu = new JMenu("Organizaciona šema");
		orgSemaMenu.setMnemonic(KeyEvent.VK_O);
		orgSemaMenu.add(new JMenuItem(new DrzaveAction()));
		orgSemaMenu.add(new JMenuItem(new NaseljenoMestoAction()));
		orgSemaMenu.add(new JMenuItem(new PreduzeceAction()));
		orgSemaMenu.add(new JMenuItem(new GodinaAction()));
		menuBar.add(orgSemaMenu);
		if (Constants.idPreduzeca != 0) {
			orgSemaMenu.add(new JMenuItem(new RadnikAction()));
			orgSemaMenu.add(new JMenuItem(new OrganizacionaJedinicaAction()));

			orgSemaMenu.add(new JMenuItem(new GrupaArtiklaAction(false)));
			orgSemaMenu.add(new JMenuItem(new ArtikalAction()));
			orgSemaMenu.add(new JMenuItem(new PopisniDokumentAction()));
			orgSemaMenu.add(new JMenuItem(new StavkaPopisaAction()));
			orgSemaMenu.add(new JMenuItem(new MagacinskaKarticaAction()));
			orgSemaMenu.add(new JMenuItem(new PoslovniPartnerAction()));
			orgSemaMenu.add(new JMenuItem(new VrstaPrometaAction()));
			orgSemaMenu.add(new JMenuItem(new PrometniDokumentAction()));
			orgSemaMenu.add(new JMenuItem(new ClanKomisijeAction()));
		} else {
			JButton btnDrzava = new JButton(new DrzaveAction());
			JButton btnMesto = new JButton(new NaseljenoMestoAction());
			JButton btnPreduzece = new JButton(new PreduzeceAction());
			JButton btnGodina = new JButton(new GodinaAction());

			btnDrzava.setPreferredSize(new Dimension(200, 200));
			btnMesto.setPreferredSize(new Dimension(200, 200));
			btnPreduzece.setPreferredSize(new Dimension(200, 200));
			btnGodina.setPreferredSize(new Dimension(200, 200));

			this.add(btnDrzava, "pad 15 15 -15 -15, dock center");
			this.add(btnMesto, "wrap, pad 15 15 -15 -15, dock center");
			this.add(btnPreduzece, "pad 15 15 -15 -15, dock center");
			this.add(btnGodina, "pad 15 15 -15 -15, dock center");
		}
		JMenu podesavanjaMenu = new JMenu("Podešavanja");
		podesavanjaMenu.add(new PreduzeceGodinaAction());
		podesavanjaMenu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(podesavanjaMenu);
		setJMenuBar(menuBar);

		this.validate();
		this.repaint();
	}
}
