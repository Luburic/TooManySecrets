package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.StavkaPopisaStandardForm;

public class StavkaPopisaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public StavkaPopisaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Stavke popisa");
		putValue(NAME, "Stavke popisa");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		StavkaPopisaStandardForm form = null;
		form = new StavkaPopisaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
