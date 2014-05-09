package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;

public class FirstAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private GenericForm standardForm;

	public FirstAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/first.gif"));
		putValue(SHORT_DESCRIPTION, "Pocetak");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		standardForm.goFirst();
	}

}
