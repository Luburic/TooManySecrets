package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.util.ComboBoxPair;

public class PickupAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;
	private Integer nameColumnIndex;

	public PickupAction(GenericForm standardForm, Integer nameColumnIndex) {
		putValue(SMALL_ICON, new ImageIcon("img/zoom-pickup.gif"));
		putValue(SHORT_DESCRIPTION, "Zoom pickup");
		this.standardForm = standardForm;
		this.nameColumnIndex = nameColumnIndex;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int index = standardForm.getTblGrid().getSelectedRow();
		if (index == -1) {
			return;
		}
		Integer id = (Integer) standardForm.getTblGrid().getModel().getValueAt(index, 0);
		int n = standardForm.getReturning().getModel().getSize();
		for (int i = 0; i < n; i++) {
			if (((ComboBoxPair) standardForm.getReturning().getItemAt(i)).getId() == id) {
				standardForm.getReturning().setSelectedIndex(i);
				break;
			}
			if (i == n - 1) {
				ComboBoxPair cbp = new ComboBoxPair(id, standardForm.getTblGrid().getModel()
						.getValueAt(index, nameColumnIndex));
				standardForm.getReturning().insertItemAt(cbp, n);
				standardForm.getReturning().setSelectedIndex(n);
				break;
			}
		}
		if (n == 0) {
			ComboBoxPair cbp = new ComboBoxPair(id, standardForm.getTblGrid().getModel()
					.getValueAt(index, nameColumnIndex));
			standardForm.getReturning().insertItemAt(cbp, n);
			standardForm.getReturning().setSelectedIndex(n);
		}
		standardForm.dispose();
	}
}
