package root.gui.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import root.gui.action.dialog.DialogAction;
import root.gui.form.GenericForm;
import root.gui.tablemodel.GenericTableModel;

public class NextFormButton extends JButton {
	private static final long serialVersionUID = -4370755269949631070L;

	public NextFormButton(final GenericForm standardForm, final JPopupMenu popup) {
		this.setIcon(new ImageIcon("img/nextform.gif"));
		this.setToolTipText("Sledeća forma");

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				GenericTableModel tableModel = (GenericTableModel) standardForm.getTblGrid().getModel();
				int index = standardForm.getTblGrid().getSelectedRow();
				// ako nije nista selektovano
				if (index < 0)
					return;
				Integer id = (int) tableModel.getValueAt(index, 0);
				for (int i = 0; i < popup.getComponentCount(); i++) {
					JMenuItem jmt = (JMenuItem) popup.getComponent(i);
					DialogAction da = (DialogAction) jmt.getAction();
					da.setWhereClause(tableModel.getWhereClause() + id);
					if (popup.getComponentCount() == 1) {
						da.actionPerformed(null);
						return;
					}
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
