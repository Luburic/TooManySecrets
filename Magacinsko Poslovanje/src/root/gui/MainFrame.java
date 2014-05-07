package root.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;

import root.dbConnection.DBConnection;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 3706293806794500307L;

	public MainFrame() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					DBConnection.getConnection().close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
