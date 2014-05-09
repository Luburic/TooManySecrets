package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class RefreshAction extends AbstractAction {

	private static final long serialVersionUID = 1L;


	public RefreshAction() {
		putValue(SMALL_ICON, new ImageIcon("img/refresh.gif"));
		putValue(SHORT_DESCRIPTION, "Refresh");

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
}
