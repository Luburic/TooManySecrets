package root.gui.action;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class ZoomFormAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	//private ColumnList cl;
	private JDialog dialog; 
	//private NaseljenoMestoStandardForm naseljenoMestoForm;

	//called from child form
	public ZoomFormAction(JDialog dialog) {
		putValue(SHORT_DESCRIPTION, "Zoom");
		putValue(NAME, "...");
		this.dialog = dialog;

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		/*
		//**********parent form**************
		DrzavaStandardForm form = null;
		try {
			form = new DrzavaStandardForm();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		form.getBtnPickup().setEnabled(true);
		form.setVisible(true);
		//***********************************
		
		//.....
		cl = form.getDrzavaColumnList(); //-selektovani red(lista kolona)
		

		if(dialog instanceof NaseljenoMestoStandardForm) {
			naseljenoMestoForm =(NaseljenoMestoStandardForm)dialog;
		
			if(cl!=null) {
				naseljenoMestoForm.getTfSifraDrzave().setText((String)cl.getColumn("dr_sifra").getValue());
				naseljenoMestoForm.getTfNazivDrzave().setText((String)cl.getColumn("dr_naziv").getValue());
			}
		
		}*/
		
	}
	
	
}
