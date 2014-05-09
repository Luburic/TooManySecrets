package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.DrzavaStandardForm;
import root.gui.form.GenericForm;
import root.gui.tablemodel.GenericTableModel;

public class NextFormAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;

	public NextFormAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/nextform.gif"));
		putValue(SHORT_DESCRIPTION, "Sledeća forma");
		this.standardForm = standardForm;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (standardForm instanceof DrzavaStandardForm) {

			standardForm = (DrzavaStandardForm) standardForm;
			GenericTableModel tableModel = (GenericTableModel) standardForm.getTblGrid().getModel();
			int index = standardForm.getTblGrid().getSelectedRow();

			// ako nije nista selektovano
			if (index < 0)
				return;

			String qsifra = (String) tableModel.getValueAt(index, 0);

			if (standardForm.getSelectedForm().equalsIgnoreCase("Naseljena mesta")) {
				// NaseljenoMestoStandardForm naseljenoMestoForm = new NaseljenoMestoStandardForm();
				// naseljenoMestoForm.setQsifra(qsifra);
				// naseljenoMestoForm.setUpTable();

				// naseljenoMestoForm.setVisible(true);

			} else {
				// druga child forma za drzavu
			}
			// ...

		}

	}
}
