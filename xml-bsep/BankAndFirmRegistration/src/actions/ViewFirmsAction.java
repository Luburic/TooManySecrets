package actions;

import gui.dialogs.FirmsMenuDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class ViewFirmsAction extends AbstractAction{
	
public ViewFirmsAction() {
		
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "View registered firms");
		putValue(NAME, "Firms");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		FirmsMenuDialog firmsDialog = new FirmsMenuDialog();
		firmsDialog.setVisible(true);
		
	}

}
