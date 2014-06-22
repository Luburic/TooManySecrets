package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import firma.gui.MainFrame;
import firma.gui.dialogs.NalogDialog;

@SuppressWarnings("serial")
public class NalogAction extends AbstractAction{
	
	
	public NalogAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Kreiranje novog naloga");
		putValue(NAME, "Nalog");
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		NalogDialog nalog = new NalogDialog(MainFrame.getInstance());
		nalog.setVisible(true);
		
	}

}
