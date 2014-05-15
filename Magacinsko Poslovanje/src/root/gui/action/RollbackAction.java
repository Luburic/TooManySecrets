package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.util.Constants;

public class RollbackAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private GenericForm standardForm;

	public RollbackAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/remove.gif"));
		putValue(SHORT_DESCRIPTION, "Poništi");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (standardForm.getMode() != Constants.MODE_EDIT && standardForm.getTblGrid().getRowCount() > 0) {
			standardForm.getTblGrid().getSelectionModel().setSelectionInterval(0, 0);
		}
	}
}
