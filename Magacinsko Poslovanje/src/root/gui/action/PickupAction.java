package root.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTable;


public class PickupAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private JDialog standardForm;
	
	private JTable tblGrid = new JTable();
	
	
	public PickupAction(JDialog standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/zoom-pickup.gif"));
		putValue(SHORT_DESCRIPTION, "Zoom pickup");
		this.standardForm = standardForm;
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
/*
			if(standardForm instanceof DrzavaStandardForm) {
				drzavaForm =(DrzavaStandardForm)standardForm;
					
				tblGrid = drzavaForm.getTblGrid();
				tableModel = (DrzaveTableModel) tblGrid.getModel();
				int index = tblGrid.getSelectedRow();
				String sifra = (String) tableModel.getValueAt(index, 0);
				String naziv = (String) tableModel.getValueAt(index, 1);
				
				//Lookup.getDrzava(sifra);
				
				ColumnList atributi = new ColumnList();
				Column c1 = new Column();
				c1.setName("dr_sifra");
				c1.setValue(sifra);
				
				Column c2 = new Column();
				c2.setName("dr_naziv");
				c2.setValue(naziv);
				
			    atributi.add(c1);
			    atributi.add(c2);
			    
			    drzavaForm.setDrzavaColumnList(atributi);
			    drzavaForm.setVisible(false);
			}
			*/
	}
}
