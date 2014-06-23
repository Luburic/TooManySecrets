package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class IzvodAction extends AbstractAction{

	public IzvodAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Kreiranje zahteva za izvod");
		putValue(NAME, "Zahtev za izvod");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
