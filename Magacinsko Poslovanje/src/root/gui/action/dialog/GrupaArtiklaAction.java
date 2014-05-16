package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.GrupaArtiklaStandardForm;

public class GrupaArtiklaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public GrupaArtiklaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Grupa artikla");
		putValue(NAME, "Grupa artikla");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GrupaArtiklaStandardForm form = null;
		form = new GrupaArtiklaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
