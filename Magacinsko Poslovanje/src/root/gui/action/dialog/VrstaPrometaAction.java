package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.VrstaPrometaStandardForm;

public class VrstaPrometaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public VrstaPrometaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Vrsta prometa");
		putValue(NAME, "Vrsta prometa");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		VrstaPrometaStandardForm form = null;
		form = new VrstaPrometaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
