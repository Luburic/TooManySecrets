package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.PrometniDokumentStandardForm;

public class PrometniDokumentAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public PrometniDokumentAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Prometni dokument");
		putValue(NAME, "Prometni dokument");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PrometniDokumentStandardForm form = null;
		form = new PrometniDokumentStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
