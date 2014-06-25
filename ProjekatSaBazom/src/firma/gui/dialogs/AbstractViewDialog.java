package firma.gui.dialogs;

import javax.swing.JDialog;
import javax.swing.JTable;

@SuppressWarnings("serial")
public abstract class AbstractViewDialog extends JDialog {
	public abstract JTable getTable();
}
