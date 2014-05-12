package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.util.Constants;

public class CommitAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private GenericForm standardForm;

	public CommitAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/commit.gif"));
		putValue(SHORT_DESCRIPTION, "Commit");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int mode = standardForm.getMode();

		switch (mode) {
		case Constants.MODE_ADD:
			standardForm.addRow();
			break;
		case Constants.MODE_EDIT:
			standardForm.updateRow();
			break;
		case Constants.MODE_SEARCH:
			break;
		}
	}
}
