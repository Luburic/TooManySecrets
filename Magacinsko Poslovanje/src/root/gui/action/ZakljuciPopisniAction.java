package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import root.gui.form.GenericForm;
import root.gui.form.PopisniDokumentStandardForm;

public class ZakljuciPopisniAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public ZakljuciPopisniAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/complete.png"));
		putValue(SHORT_DESCRIPTION, "Proknjiži popisni dokument");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();

		if (i != -1) {
			if (JOptionPane
					.showConfirmDialog(
							standardForm,
							"Da li ste sigurni da želite da proknjižite dokument? Kada se jednom proknjiži, dokument se više ne može ažurirati, izuzev storniranja.",
							"Proknjižavane dokumenta", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				((PopisniDokumentStandardForm) standardForm).proknjiziDokument();
			}

		}
	}
}
