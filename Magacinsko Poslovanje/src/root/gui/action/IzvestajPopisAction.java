package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.gui.form.PopisniDokumentStandardForm;

public class IzvestajPopisAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public IzvestajPopisAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/report.png"));
		putValue(SHORT_DESCRIPTION, "Popisni list");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();
		if (i != -1) {
			((PopisniDokumentStandardForm) standardForm).izvestaj();
		}
	}
}
