package firma.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import firma.gui.dialogs.FakturaDialog;
import firma.gui.dialogs.StavkaDialog;

@SuppressWarnings("serial")
public class StavkaAction extends AbstractAction{

	private FakturaDialog parent;
	
	public StavkaAction(FakturaDialog parent) {
		putValue(SHORT_DESCRIPTION, "Kreiranje stavke zaglavlja fkture");
		this.parent = parent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		StavkaDialog stavkaDialog = new StavkaDialog(parent);
		stavkaDialog.setVisible(true);
		
	}

}
