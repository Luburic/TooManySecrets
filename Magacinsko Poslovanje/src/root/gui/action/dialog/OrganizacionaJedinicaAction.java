package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import root.gui.form.OrganizacionaJedinicaStandardForm;

public class OrganizacionaJedinicaAction extends DialogAction {
	private static final long serialVersionUID = 1L;

	public OrganizacionaJedinicaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Organizacione jedinice");
		putValue(NAME, "Organizacione jedinice");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		OrganizacionaJedinicaStandardForm form = null;
		form = new OrganizacionaJedinicaStandardForm(null, getWhereClause());
		form.setVisible(true);
	}

}
