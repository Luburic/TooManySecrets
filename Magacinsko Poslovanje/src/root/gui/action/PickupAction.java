package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.util.ComboBoxPair;

public class PickupAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;

	public PickupAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/zoom-pickup.gif"));
		putValue(SHORT_DESCRIPTION, "Zoom pickup");
		this.standardForm = standardForm;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Integer id = (Integer) standardForm.getTblGrid().getModel()
				.getValueAt(standardForm.getTblGrid().getSelectedRow(), 0);
		int n = standardForm.getReturning().getModel().getSize();
		for (int i = 0; i < n; i++) {
			if (((ComboBoxPair) standardForm.getReturning().getItemAt(i)).getId() == id) {
				standardForm.getReturning().setSelectedIndex(i);
				break;
			}
		}
		standardForm.setVisible(false);
	}
}
