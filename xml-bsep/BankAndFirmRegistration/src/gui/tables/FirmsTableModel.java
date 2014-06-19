package gui.tables;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import firms.Firm;
import firms.RegisteredFirms;

@SuppressWarnings("serial")
public class FirmsTableModel extends AbstractTableModel {
	
	static final public String[] colHeaders = new String []{"Firm Name", "PIB"};
	private ArrayList<RowDataFirms> rowData;

	public FirmsTableModel(){

		rowData = new ArrayList<RowDataFirms>();
	}

	public void fillTable(RegisteredFirms regFirms){
		rowData.clear();			
		for(Firm f : regFirms.getFirm()){
			RowDataFirms data = new RowDataFirms();
			data.setFirmName(f.getName());
			data.setPib(f.getPib());						
			rowData.add(data);
		}
	}

	public int getColumnCount() {
		return colHeaders.length;
	}

	public int getRowCount() {
		return rowData.size();
	}

	public String getColumnName(int i) {
		return colHeaders[i];
	}

	public Object getValueAt(int r, int c) {
		if( r<0 || r >= getRowCount())
			return "";
		RowDataFirms row = rowData.get(r);
		switch(c){
		case 0: return row.getFirmName();
		case 1:	return row.getPib();
		}
		return "";		
	}

}
