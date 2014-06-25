package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import firma.gui.dialogs.AbstractViewDialog;
import firma.gui.dialogs.ViewFakturaDialog;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ApproveAction extends AbstractAction {
	private AbstractViewDialog dialog;

	public ApproveAction(AbstractViewDialog dialog) {
		this.dialog = dialog;
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Odobri fakturu");
		putValue(NAME, "Odobri fakturu");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = dialog.getTable().getSelectedRow();
		if (i != -1) {
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);
		}
		// TODO: Kod za slanje fakture, kao i brisanje iz liste šefa/direktora. Ova akcija se koristi i kod naloga, tako
		// da treba i taj slučaj srediti.
		if (dialog instanceof ViewFakturaDialog) {

		} else {

		}
	}

}
