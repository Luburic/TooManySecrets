package root.gui.action.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import root.gui.PreFrame;

public class PreduzeceGodinaAction extends AbstractAction {
	private static final long serialVersionUID = -8796495513586098280L;

	public PreduzeceGodinaAction() {
		KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlMKeyStroke);
		putValue(SHORT_DESCRIPTION, "Promena režima");
		putValue(NAME, "Promena režima");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		PreFrame.getInstance();
	}
}
