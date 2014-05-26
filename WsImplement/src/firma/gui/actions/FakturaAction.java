package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import firma.gui.MainFrame;
import firma.gui.dialogs.ZaglavljeDialog;

@SuppressWarnings("serial")
public class FakturaAction extends AbstractAction{

	public FakturaAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Kreiranje nove fakture");
		putValue(NAME, "Faktura");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ZaglavljeDialog fakturaDialog = new ZaglavljeDialog(MainFrame.getInstance());
		fakturaDialog.setVisible(true);
	}

}
