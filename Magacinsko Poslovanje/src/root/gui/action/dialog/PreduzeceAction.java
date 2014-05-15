package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.PreduzeceStandardForm;

public class PreduzeceAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public PreduzeceAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Preduzeće");
		putValue(NAME, "Preduzeće");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PreduzeceStandardForm form = null;
		form = new PreduzeceStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
