package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import root.gui.form.GenericForm;

public class ZoomFormAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	// private ColumnList cl;
	private GenericForm dialog;
	private Integer id;

	public void setId(Integer id) {
		this.id = id;
	}

	// called from child form
	public ZoomFormAction(GenericForm dialog) {
		putValue(SHORT_DESCRIPTION, "Zoom");
		putValue(NAME, "...");
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (dialog == null)
			return;
		dialog.getBtnPickup().setEnabled(true);
		int n = dialog.getTblGrid().getModel().getRowCount();
		for (int i = 0; i < n; i++) {
			if (id == dialog.getTblGrid().getModel().getValueAt(i, 0)) {
				dialog.getTblGrid().getSelectionModel().setSelectionInterval(i, i);
				break;
			}
		}
		dialog.setVisible(true);
	}

}
