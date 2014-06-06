package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.gui.form.OrganizacionaJedinicaStandardForm;

public class IzvestajLagerListaAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public IzvestajLagerListaAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/report.png"));
		putValue(SHORT_DESCRIPTION, "Lager lista");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();
		if (i != -1) {
			((OrganizacionaJedinicaStandardForm) standardForm).lagerLista();
		}
	}
}
