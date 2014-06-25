package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import basexdb.util.FirmaDBUtil;
import beans.faktura.Faktura;
import beans.nalog.Nalog;
import firma.gui.MainFrame;
import firma.gui.dialogs.AbstractViewDialog;
import firma.gui.dialogs.ViewFakturaDialog;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class DenyAction extends AbstractAction {
	private AbstractViewDialog dialog;

	public DenyAction(AbstractViewDialog dialog) {
		this.dialog = dialog;
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Poništi dokument");
		putValue(NAME, "Poništi dokument");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = dialog.getTable().getSelectedRow();
		if (i != -1) {
			if (dialog instanceof ViewFakturaDialog) {
				Faktura faktura = (Faktura) ((ListTableModel) dialog.getTable().getModel()).getRow(i).get(0);
				MainFrame.getInstance().getBaza().getFaktureZaSefa().getFaktura().remove(faktura);
				MainFrame.getInstance().getBaza().getFaktureZaDirektora().getFaktura().remove(faktura);
				FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
						"http://localhost:8081/BaseX75/rest/firmaa");
			} else {
				Nalog nalog = (Nalog) ((ListTableModel) dialog.getTable().getModel()).getRow(i).get(0);
				MainFrame.getInstance().getBaza().getNaloziZaSefa().getNalog().remove(nalog);
				MainFrame.getInstance().getBaza().getNaloziZaDirektora().getNalog().remove(nalog);
				FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
						"http://localhost:8081/BaseX75/rest/firmaa");
			}
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);
		}
	}
}
