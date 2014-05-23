package firma.gui.dialogs;

import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.JDialog;

import firma.gui.MainFrame;


@SuppressWarnings("serial")
public class FakturaDialog extends JDialog{

	public FakturaDialog(MainFrame instance) {
		super(instance, Dialog.ModalityType.APPLICATION_MODAL);
		setTitle("Slanje fakture");		
		setSize(new Dimension(400,300));
		//setResizable(false);
		/*MigLayout layout = new MigLayout(
				"",
				"20[]10[]20",
				"20[]20[]20[]10[]10[]10[]10[]10[]20[]20");
		setLayout(layout);*/
		setLocationRelativeTo(instance);
	}
	
}
