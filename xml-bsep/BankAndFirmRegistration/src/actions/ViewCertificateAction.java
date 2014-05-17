package actions;

import gui.dialogs.LoadCertificateDialog;
import gui.dialogs.ViewCertificateDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class ViewCertificateAction extends AbstractAction {

	public ViewCertificateAction() {

		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "View self-signed certificate");
		putValue(NAME, "View self-signed certificate");

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ViewCertificateDialog certDialog = new ViewCertificateDialog(LoadCertificateDialog.getIssuerCertificate());
		certDialog.setVisible(true);

	}

}
