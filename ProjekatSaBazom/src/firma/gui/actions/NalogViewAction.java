package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class NalogViewAction extends AbstractAction {

	public NalogViewAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Pregled faktura");
		putValue(NAME, "Pregled faktura");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// ZaglavljeDialog fakturaDialog = new ZaglavljeDialog(null);
		// fakturaDialog.setVisible(true);
	}

}
