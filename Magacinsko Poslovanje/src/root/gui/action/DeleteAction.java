package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import root.gui.form.GenericForm;

public class DeleteAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private GenericForm standardForm;

	public DeleteAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/remove.gif"));
		putValue(SHORT_DESCRIPTION, "Brisanje");
		this.standardForm = standardForm;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(standardForm.getTblGrid().getSelectedRow() == -1)
			return;
		
		if (standardForm.allowDeletion()) {
			if (JOptionPane.showConfirmDialog(standardForm, "Da li ste sigurni da želite da obrišete dati slog?",
					"Brišete slog", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				standardForm.removeRow();
			}
		} else {
			JOptionPane
					.showMessageDialog(
							standardForm,
							"Dati slog se ne može obrisati jer se za njega vezuju slogovi drugih tabela. Saznajte koji putem Next mehanizma.",
							"Zabranjeno brisanje povezanog sloga!", JOptionPane.ERROR_MESSAGE);
		}
	}
}
