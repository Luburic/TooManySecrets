package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import root.gui.form.GenericForm;
import root.gui.form.OrganizacionaJedinicaStandardForm;

public class NovoStanjeAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public NovoStanjeAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/complete.png"));
		putValue(SHORT_DESCRIPTION, "Otvori magacinske kartice za narednu godinu");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();
		if (i != -1) {
			if (JOptionPane
					.showConfirmDialog(
							standardForm,
							"Da li ste sigurni da želite da otvorite magacin za narednu godinu? Ova operacija se ne može opozvati.",
							"Otvaranje novog stanja", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				((OrganizacionaJedinicaStandardForm) standardForm).otvoriNovoStanje();
			}
		}
	}
}
