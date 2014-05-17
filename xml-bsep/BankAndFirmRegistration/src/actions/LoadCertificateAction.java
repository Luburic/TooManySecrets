package actions;

import gui.MainFrame;
import gui.dialogs.LoadCertificateDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class LoadCertificateAction extends AbstractAction {

	public LoadCertificateAction() {

		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Load self-signed certificate");
		putValue(NAME, "Load self-signed certificate");

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		LoadCertificateDialog certDialog = new LoadCertificateDialog(MainFrame.getInstance());
		certDialog.setVisible(true);
		
	}

}
