package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import firma.gui.dialogs.AbstractViewDialog;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class DenyAction extends AbstractAction {
	private AbstractViewDialog dialog;

	public DenyAction(AbstractViewDialog dialog) {
		this.dialog = dialog;
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Poništi fakturu");
		putValue(NAME, "Poništi fakturu");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = dialog.getTable().getSelectedRow();
		if (i != -1) {
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);
		}
		// TODO: Obriši fakturu iz liste šefa/direktora (baza)
	}

}
