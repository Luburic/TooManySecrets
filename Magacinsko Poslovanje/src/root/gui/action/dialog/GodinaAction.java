package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.PoslovnaGodinaStandardForm;

public class GodinaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public GodinaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Poslovna godina");
		putValue(NAME, "Poslovna godina");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PoslovnaGodinaStandardForm form = null;
		form = new PoslovnaGodinaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
