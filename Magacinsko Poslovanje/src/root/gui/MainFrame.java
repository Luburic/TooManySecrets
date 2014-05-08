package root.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import root.dbConnection.DBConnection;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 3706293806794500307L;

	private static MainFrame instance = null;

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

		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}
}
