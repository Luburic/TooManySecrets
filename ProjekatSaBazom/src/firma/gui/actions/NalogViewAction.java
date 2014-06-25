package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import firma.gui.dialogs.ViewNalogDialog;

@SuppressWarnings("serial")
public class NalogViewAction extends AbstractAction {

	public NalogViewAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Pregled naloga");
		putValue(NAME, "Pregled naloga");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ViewNalogDialog nalogDialog = new ViewNalogDialog();
		nalogDialog.setVisible(true);
	}

}
