package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import root.gui.action.dialog.DialogAction;
import root.gui.form.GenericForm;
import root.gui.tablemodel.GenericTableModel;

public class NextFormAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;
	private JPopupMenu popup;

	public NextFormAction(GenericForm standardForm, JPopupMenu popup) {
		putValue(SMALL_ICON, new ImageIcon("img/nextform.gif"));
		putValue(SHORT_DESCRIPTION, "Sledeća forma");
		this.standardForm = standardForm;
		this.popup = popup;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GenericTableModel tableModel = (GenericTableModel) standardForm.getTblGrid().getModel();
		int index = standardForm.getTblGrid().getSelectedRow();
		// ako nije nista selektovano
		if (index < 0)
			return;
		Integer id = (int) tableModel.getValueAt(index, 0);
		for (int i = 0; i < popup.getComponentCount(); i++) {
			JMenuItem jmt = (JMenuItem) popup.getComponent(i);
			DialogAction da = (DialogAction) jmt.getAction();
			da.setWhereClause(((GenericTableModel) standardForm.getTblGrid().getModel()).getWhereClause() + id);
			if (popup.getComponentCount() == 1) {
				da.actionPerformed(e);
				return;
			}
		}

	}
}
