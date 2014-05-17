package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.ArtikalStandardForm;

public class ArtikalAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public ArtikalAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Artikli");
		putValue(NAME, "Artikli");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ArtikalStandardForm form = null;
		form = new ArtikalStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
