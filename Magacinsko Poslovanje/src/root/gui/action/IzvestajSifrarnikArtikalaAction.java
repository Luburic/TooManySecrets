package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.ArtikalStandardForm;
import root.gui.form.GenericForm;

public class IzvestajSifrarnikArtikalaAction extends AbstractAction {
	private static final long serialVersionUID = 5579192348929947520L;
	
	private GenericForm standardForm;

	public IzvestajSifrarnikArtikalaAction(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/report.png"));
		putValue(SHORT_DESCRIPTION, "Šifrarnik artikala");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = standardForm.getTblGrid().getSelectedRow();
		if (i != -1) {
			((ArtikalStandardForm) standardForm).prikaziSifrarnik();
		}
	}

}
