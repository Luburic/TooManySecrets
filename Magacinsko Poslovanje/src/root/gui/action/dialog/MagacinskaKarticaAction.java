package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.MagacinskaKarticaStandardForm;

public class MagacinskaKarticaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public MagacinskaKarticaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Magacinska kartica");
		putValue(NAME, "Magacinska kartica");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		MagacinskaKarticaStandardForm form = null;
		form = new MagacinskaKarticaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
