package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import root.gui.form.GenericForm;
import root.gui.form.PopisniDokumentStandardForm;

public class StornirajPopisniAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public StornirajPopisniAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/storn.png"));
		putValue(SHORT_DESCRIPTION, "Storniraj popisni dokument");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();

		if (i != -1) {
			if (JOptionPane.showConfirmDialog(standardForm,
					"Da li ste sigurni da želite da stornirate dokument? Ova operacija se ne može opozvati.",
					"Storniranje dokumenta", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				((PopisniDokumentStandardForm) standardForm).stornirajDokument();
			}

		}
	}
}
