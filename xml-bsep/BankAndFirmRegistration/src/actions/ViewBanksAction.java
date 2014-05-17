package actions;

import gui.MainFrame;
import gui.dialogs.BanksDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class ViewBanksAction extends AbstractAction {

public ViewBanksAction() {
		
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "View registered banks");
		putValue(NAME, "Banks");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		BanksDialog bankDialog = new BanksDialog(MainFrame.getInstance());
		bankDialog.setVisible(true);
		
	}

}
