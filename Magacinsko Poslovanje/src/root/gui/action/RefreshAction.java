package root.gui.action;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.gui.tablemodel.GenericTableModel;

public class RefreshAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;

	public RefreshAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/refresh.gif"));
		putValue(SHORT_DESCRIPTION, "Refresh");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			((GenericTableModel) standardForm.getTblGrid().getModel()).open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
