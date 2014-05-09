package root.gui.tablemodel;

import java.sql.SQLException;

public interface ITableModel {
	public void open() throws SQLException;

	public void fillData(String sql) throws SQLException;

	public void openAsChildForm(String whereValue) throws SQLException;

	public void deleteRow(int index) throws SQLException;

	public int insertRow(Object[] colNames) throws SQLException;

	public int sortedInsert(Object[] colNames);

}
