package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.util.Constants;

public class SearchAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;

	public SearchAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/search.gif"));
		putValue(SHORT_DESCRIPTION, "Pretraga");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		standardForm.setMode(Constants.MODE_SEARCH);
	}
}
