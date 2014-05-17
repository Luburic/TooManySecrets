package root.gui;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import root.dbConnection.DBConnection;
import root.gui.action.dialog.ArtikalAction;
import root.gui.action.dialog.DrzaveAction;
import root.gui.action.dialog.GodinaAction;
import root.gui.action.dialog.GrupaArtiklaAction;
import root.gui.action.dialog.NaseljenoMestoAction;
import root.gui.action.dialog.OrganizacionaJedinicaAction;
import root.gui.action.dialog.PopisniDokumentAction;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.RadnikAction;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 3706293806794500307L;

	private static MainFrame instance = null;
	private JMenuBar menuBar;

	private MainFrame() {
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

		setUpMenu();
		setJMenuBar(menuBar);

		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}

	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	private void setUpMenu() {
		menuBar = new JMenuBar();

		JMenu orgSemaMenu = new JMenu("Organizaciona sema");
		orgSemaMenu.setMnemonic(KeyEvent.VK_O);
		orgSemaMenu.add(new JMenuItem(new DrzaveAction()));
		orgSemaMenu.add(new JMenuItem(new NaseljenoMestoAction()));
		orgSemaMenu.add(new JMenuItem(new PreduzeceAction()));
		orgSemaMenu.add(new JMenuItem(new RadnikAction()));
		orgSemaMenu.add(new JMenuItem(new GodinaAction()));
		orgSemaMenu.add(new JMenuItem(new GrupaArtiklaAction()));
		orgSemaMenu.add(new JMenuItem(new ArtikalAction()));
		orgSemaMenu.add(new JMenuItem(new OrganizacionaJedinicaAction()));
		orgSemaMenu.add(new JMenuItem(new PopisniDokumentAction()));
		menuBar.add(orgSemaMenu);
	}
}
