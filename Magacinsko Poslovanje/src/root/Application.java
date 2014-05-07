package root;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import root.gui.LoginDialog;

public class Application {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		LoginDialog lg = new LoginDialog();
	}

}
