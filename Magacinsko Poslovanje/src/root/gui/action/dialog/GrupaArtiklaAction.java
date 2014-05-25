package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.GrupaArtiklaStandardForm;

public class GrupaArtiklaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	private boolean recursive;

	public GrupaArtiklaAction(boolean recursive) {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Grupe artikla");
		putValue(NAME, "Grupe artikla");
		this.recursive = recursive;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (recursive) {
			String where = getWhereClause();
			String id = where.substring(where.indexOf("=") + 1, where.length());
			setWhereClause(" WHERE Grupa_artikla1.Gru_id_grupe=" + id);
		}
		GrupaArtiklaStandardForm form = null;
		form = new GrupaArtiklaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
