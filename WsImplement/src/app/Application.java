package app;

import java.util.Locale;

import firma.gui.MainFrame;

public class Application {

	public static void main (String[] args){
		Locale.setDefault(Locale.GERMAN);
		MainFrame.getInstance().setVisible(true);
	}

}
