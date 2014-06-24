package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import firma.gui.dialogs.FakturaDialog;

@SuppressWarnings("serial")
public class FakturaViewAction extends AbstractAction {

	public FakturaViewAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Pregled faktura");
		putValue(NAME, "Pregled faktura");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FakturaDialog fakturaDialog = new FakturaDialog();
		fakturaDialog.setVisible(true);
	}

}
