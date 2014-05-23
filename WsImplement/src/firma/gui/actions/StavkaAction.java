package firma.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import beans.faktura.Faktura;
import firma.gui.dialogs.StavkaDialog;

@SuppressWarnings("serial")
public class StavkaAction extends AbstractAction{

	private Faktura parent;
	
	public StavkaAction(Faktura parent) {
		putValue(SHORT_DESCRIPTION, "Kreiranje stavke zaglavlja fkture");
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		StavkaDialog stavkaDialog = new StavkaDialog(parent);
		stavkaDialog.setVisible(true);
		
	}

}
