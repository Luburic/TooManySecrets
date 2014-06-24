package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class ApproveAction extends AbstractAction {

	public ApproveAction() {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Odobri fakturu");
		putValue(NAME, "Odobri fakturu");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Kod za slanje fakture, kao i brisanje iz liste šefa/direktora.
	}

}
