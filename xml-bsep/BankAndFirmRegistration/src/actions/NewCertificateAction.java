package actions;

import gui.MainFrame;
import gui.dialogs.NewSelfSignedCertificateDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class NewCertificateAction extends AbstractAction {
	
	public NewCertificateAction() {
		
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Make new self-signed certificate");
		putValue(NAME, "New self-signed certificate");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		NewSelfSignedCertificateDialog certDialog = new NewSelfSignedCertificateDialog(MainFrame.getInstance());
		certDialog.setVisible(true);
		
	}

}
