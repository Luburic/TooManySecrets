package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.PoslovniPartnerStandardForm;

public class PoslovniPartnerAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public PoslovniPartnerAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Popisni dokumenti");
		putValue(NAME, "Popisni dokumenti");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PoslovniPartnerStandardForm form = null;
		form = new PoslovniPartnerStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
