package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class DenyAction extends AbstractAction {

	public DenyAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Poništi fakturu");
		putValue(NAME, "Poništi fakturu");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Obriši fakturu iz liste šefa/direktora
	}

}
