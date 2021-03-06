package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.DrzavaStandardForm;

public class DrzaveAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public DrzaveAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Države");
		putValue(NAME, "Države");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		DrzavaStandardForm form = null;
		form = new DrzavaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}
}
