package firma.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import basexdb.firma.FirmeSema;
import basexdb.util.FirmaDBUtil;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static MainFrame instance = null;
	private MenuBar menuBar;
	private FirmeSema semaFirma;

	private MainFrame() {
		setSize(new Dimension(700, 500));
		setLocationRelativeTo(null);
		setTitle("Firma");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUpMenu();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), "Da li ste sigurni?",
						"Zatvaranje aplikacije", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
		semaFirma = FirmaDBUtil.loadFirmaDatabase("http://localhost:8081/BaseX75/rest/firmaa");

		setJMenuBar(menuBar);
		setVisible(true);
	}

	private void setUpMenu() {
		menuBar = new MenuBar();
	}

	public FirmeSema getBaza() {
		return semaFirma;
	}

	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}
}