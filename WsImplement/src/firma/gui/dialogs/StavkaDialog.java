package firma.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JDialog;

import firma.gui.MainFrame;
import net.miginfocom.swing.MigLayout;
import beans.faktura.Faktura;

@SuppressWarnings("serial")
public class StavkaDialog extends JDialog{

	public StavkaDialog(Faktura novaFaktura) {
		// TODO Auto-generated constructor stub
		//forma za popunjavanje stavke odgovrajuceg zaglavlja fakture
		super();
		setLocationRelativeTo(MainFrame.getInstance());
		setTitle("Popunjavanje stavke");	
		setResizable(false);
		setSize(new Dimension(400,300));
		setLayout(new MigLayout("fill"));
	}
	
}
