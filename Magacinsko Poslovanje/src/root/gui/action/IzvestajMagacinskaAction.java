package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.MagacinskaKarticaIzvestaj;
import root.gui.form.GenericForm;
import root.gui.tablemodel.GenericTableModel;

public class IzvestajMagacinskaAction extends AbstractAction {
	private static final long serialVersionUID = 152144296572370143L;

	private GenericForm standardForm;

	public IzvestajMagacinskaAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/report.png"));
		putValue(SHORT_DESCRIPTION, "Izveštaj prometa robe");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();
		GenericTableModel t = (GenericTableModel) standardForm.getTblGrid().getModel();
		MagacinskaKarticaIzvestaj form = null;
		if (i != -1) {
			form = new MagacinskaKarticaIzvestaj(t.getValueAt(i, 3), t.getValueAt(i, 2));
		} else {
			form = new MagacinskaKarticaIzvestaj(null, null);
		}
		form.setVisible(true);
	}
}
