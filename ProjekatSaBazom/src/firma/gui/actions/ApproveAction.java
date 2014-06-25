package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import basexdb.util.FirmaDBUtil;
import beans.faktura.Faktura;
import firma.gui.MainFrame;
import firma.gui.dialogs.AbstractViewDialog;
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
			Faktura faktura = (Faktura) ((ListTableModel) dialog.getTable().getModel()).getRow(i).get(0);
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);

			MainFrame.getInstance().getBaza().getFaktureZaSefa().getFaktura().remove(faktura);
			MainFrame.getInstance().getBaza().getFaktureZaDirektora().getFaktura().remove(faktura);
			FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
					"http://localhost:8081/BaseX75/rest/firmaa");
			// TODO: Kod za slanje fakture
		}
	}

}
