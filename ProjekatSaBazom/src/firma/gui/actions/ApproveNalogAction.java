package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import basexdb.util.FirmaDBUtil;
import beans.nalog.Nalog;
import firma.gui.MainFrame;
import firma.gui.dialogs.AbstractViewDialog;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ApproveNalogAction extends AbstractAction {
	private AbstractViewDialog dialog;

	public ApproveNalogAction(AbstractViewDialog dialog) {
		this.dialog = dialog;
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Odobri nalog");
		putValue(NAME, "Odobri nalog");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = dialog.getTable().getSelectedRow();
		if (i != -1) {
			Nalog nalog = (Nalog) ((ListTableModel) dialog.getTable().getModel()).getRow(i).get(0);
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);

			MainFrame.getInstance().getBaza().getNaloziZaSefa().getNalog().remove(nalog);
			MainFrame.getInstance().getBaza().getNaloziZaDirektora().getNalog().remove(nalog);
			FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
					"http://localhost:8081/BaseX75/rest/firmaa");
			// TODO: Kod za slanje naloga
		}
	}

}
