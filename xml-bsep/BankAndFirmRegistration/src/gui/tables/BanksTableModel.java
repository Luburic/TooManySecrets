package gui.tables;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import banks.Bank;
import banks.RegisteredBanks;



@SuppressWarnings("serial")
public class BanksTableModel extends AbstractTableModel {

	static final public String[] colHeaders = new String []{"Bank Name", "SWIFT code"};
	private ArrayList<RowData> rowData;

	public BanksTableModel(){

		rowData = new ArrayList<RowData>();
	}

	public void fillTable(RegisteredBanks regBanks){
		rowData.clear();			
		for(Bank b : regBanks.getBank()){
			RowData data = new RowData();
			data.setBankName(b.getName());
			data.setSwift(b.getSwiftCode());						
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
		RowData row = rowData.get(r);
		switch(c){
		case 0: return row.getBankName();
		case 1:	return row.getSwift();
		}
		return "";		
	}
}
