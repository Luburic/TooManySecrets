package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.RadnikStandardForm;

public class RadnikAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public RadnikAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Radnik");
		putValue(NAME, "Radnik");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		RadnikStandardForm form = null;
		form = new RadnikStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
