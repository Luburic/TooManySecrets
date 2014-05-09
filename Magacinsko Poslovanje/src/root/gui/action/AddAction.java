package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.util.Constants;

public class AddAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private GenericForm standardForm;

	public AddAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/add.gif"));
		putValue(SHORT_DESCRIPTION, "Dodavanje");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		standardForm.setMode(Constants.MODE_ADD);
	}
}
