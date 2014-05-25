package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.ClanKomisijeStandardForm;

public class ClanKomisijeAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public ClanKomisijeAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Članovi komisija");
		putValue(NAME, "Članovi komisija");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ClanKomisijeStandardForm form = null;
		form = new ClanKomisijeStandardForm(null, getWhereClause());
		form.setVisible(true);
	}
}
