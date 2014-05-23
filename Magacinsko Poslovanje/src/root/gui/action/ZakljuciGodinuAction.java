package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import root.gui.form.GenericForm;
import root.gui.form.PoslovnaGodinaStandardForm;

public class ZakljuciGodinuAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public ZakljuciGodinuAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/complete.png"));
		putValue(SHORT_DESCRIPTION, "Zaključi godinu");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();

		if (i != -1) {
			if (JOptionPane
					.showConfirmDialog(
							standardForm,
							"Da li ste sigurni da želite da zaključite godinu? Kada se jednom zaključa, godina se više ne može ažurirati.",
							"Zaključujete godinu", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				((PoslovnaGodinaStandardForm) standardForm).zakljuciGodinu();
			}

		}
	}
}
