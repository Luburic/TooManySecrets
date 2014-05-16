package root.gui.tablemodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import root.dbConnection.DBConnection;
import root.util.Constants;
import root.util.MetaSurogateDisplay;
import root.util.SortUtils;
import rs.mgifos.mosquito.model.MetaColumn;

@SuppressWarnings("serial")
public class GenericTableModel extends DefaultTableModel implements ITableModel {
	private String basicQuery = "";
	private String joinQuery = "";
	private String whereStmt = "";
	private String orderBy = "";

	private String whereClause = "";
	private List<MetaSurogateDisplay> outsideColumns;

	public List<MetaSurogateDisplay> getOutsideColumns() {
		return outsideColumns;
	}

	public void setOutsideColumns(List<MetaSurogateDisplay> outsideColumns) {
		this.outsideColumns = outsideColumns;
	}

	private String tableCode;

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	private Collection<MetaColumn> columns;
	private String primaryKey;

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	private int columnForSorting = 1;

	public int getColumnForSorting() {
		return columnForSorting;
	}

	public void setColumnForSorting(int columnForSorting) {
		this.columnForSorting = columnForSorting;
	}

	public GenericTableModel(String tableCode, Object[] colNames, Collection<MetaColumn> columns) {
		super(colNames, 0);
		this.tableCode = tableCode;
		this.columns = columns;
		this.primaryKey = columns.iterator().next().getCode();
		this.setWhereClause(" WHERE " + this.tableCode + "." + this.primaryKey + "=");
	}

	@Override
	public void open() throws SQLException {
		if (basicQuery.equals("")) {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT ");
			Iterator<MetaColumn> iterator = columns.iterator();
			while (iterator.hasNext()) {
				MetaColumn column = iterator.next();
				String name = column.getCode();
				if (name.contains("version") && outsideColumns != null) {
					Iterator<MetaSurogateDisplay> outsideIterator = outsideColumns.iterator();
					while (outsideIterator.hasNext()) {
						MetaSurogateDisplay msd = outsideIterator.next();
						Iterator<String> insideIterator = msd.getDisplayColumnCode().iterator();
						while (insideIterator.hasNext()) {
							if (!tableCode.equals(msd.getTableCode())) {
								sb.append(msd.getTableCode() + "." + insideIterator.next() + ", ");
							} else {
								sb.append(msd.getTableCode() + "2." + insideIterator.next() + ", ");
							}
						}
					}
					sb.append(tableCode + "1." + name + " FROM " + tableCode + " " + tableCode + "1");
					break;
				}

				sb.append(tableCode + "1." + name);
				if (iterator.hasNext()) {
					sb.append(", ");
				} else {
					sb.append(" FROM " + tableCode + " " + tableCode + "1");
					break;
				}
			}
			basicQuery = sb.toString();
		}

		if (outsideColumns != null) {
			StringBuilder sb = new StringBuilder();
			Iterator<MetaSurogateDisplay> outsideIterator = outsideColumns.iterator();
			while (outsideIterator.hasNext()) {
				sb.append(" JOIN ");
				MetaSurogateDisplay msd = outsideIterator.next();
				if (!msd.getTableCode().equals(tableCode)) {
					sb.append(msd.getTableCode());
					String foreignKey = msd.getIdColumnName();
					sb.append(" ON " + tableCode + "1." + foreignKey + " = " + msd.getTableCode() + "." + foreignKey);
				} else {
					sb.append(msd.getTableCode() + " " + msd.getTableCode() + "2");
					String foreignKey = msd.getIdColumnName();
					sb.append(" ON " + tableCode + "1." + foreignKey + " = " + msd.getTableCode() + "2." + primaryKey);
				}
			}
			joinQuery = sb.toString();
		}

		if (orderBy.equals("")) {
			orderBy = " ORDER BY " + tableCode + "1." + primaryKey;
		}
		System.out.println(basicQuery + joinQuery + whereStmt + orderBy);
		fillData(basicQuery + joinQuery + whereStmt + orderBy);
	}

	@Override
	public void fillData(String sql) throws SQLException {
		setRowCount(0);
		Statement stmt = DBConnection.getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		int columnCount = rset.getMetaData().getColumnCount();

		while (rset.next()) {
			Object[] rowForInsert = new Object[rset.getMetaData().getColumnCount()];
			for (int i = 0; i < columnCount; i++) {
				if (rset.getMetaData().getColumnType(i + 1) == Types.BIT) {
					if (rset.getString(i + 1).equals("1")) {
						rowForInsert[i] = "Da";
					} else {
						rowForInsert[i] = "Ne";
					}
				} else if (rset.getMetaData().getColumnType(i + 1) == Types.DATE) {
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = rset.getDate(i + 1);
					rowForInsert[i] = formatter.format(date);
				} else if (rset.getMetaData().getColumnType(i + 1) == Types.INTEGER) {
					rowForInsert[i] = rset.getInt(i + 1);
				} else {
					rowForInsert[i] = rset.getString(i + 1);
				}
			}
			addRow(rowForInsert);
		}

		rset.close();
		stmt.close();
		fireTableDataChanged();
	}

	// U kodu je generalno hardkodovano da je prva kolona za surogatni ključ, a poslednja za verziju. Isti rezultat bi
	// se postigao da preko meta podataka obeležavamo koje kolone su za verziju, a koje za ključ, ali ovako je
	// efikasnije i nema potrebe komplikovati.
	public void checkRow(int index) throws SQLException {
		DBConnection.getConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

		PreparedStatement statement = DBConnection.getConnection().prepareStatement(
				"SELECT * FROM " + tableCode + " WHERE " + primaryKey + "=?");
		Integer sifra = (Integer) getValueAt(index, 0);
		statement.setInt(1, sifra);
		ResultSet rset = statement.executeQuery();
		String errorMessage = null;

		if (!rset.next()) {
			removeRow(index);
			fireTableDataChanged();
			errorMessage = Constants.ERROR_RECORD_WAS_DELETED;
		} else {
			int columnCount = rset.getMetaData().getColumnCount();
			Integer versionFromDb = rset.getInt(columnCount);
			Integer versionFromTable = (Integer) getValueAt(index, getColumnCount() - 1);
			if (versionFromDb != versionFromTable) {
				errorMessage = Constants.ERROR_RECORD_WAS_CHANGED;
				for (int i = 0; i < columnCount; i++) {
					Object newValue;
					if (rset.getMetaData().getColumnType(i + 1) == Types.BIT) {
						if (rset.getString(i + 1).equals("1")) {
							newValue = "Da";
						} else {
							newValue = "Ne";
						}
					} else if (rset.getMetaData().getColumnType(i + 1) == Types.DATE) {
						DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date = rset.getDate(i + 1);
						newValue = formatter.format(date);
					} else if (rset.getMetaData().getColumnType(i + 1) == Types.INTEGER) {
						newValue = rset.getInt(i + 1);
					} else {
						newValue = rset.getString(i + 1);
					}
					setValueAt(newValue, index, i);
					fireTableDataChanged();
				}
			}
		}

		rset.close();
		statement.close();

		if (errorMessage != null) {
			DBConnection.getConnection().commit();
			DBConnection.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			throw new SQLException(errorMessage, "");
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public void openAsChildForm(String whereValue) throws SQLException {

	}

	public void updateRow(Object[] colNames, int index) throws SQLException {
		checkRow(index);

		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE " + tableCode + " SET ");
		Iterator<MetaColumn> iterator = columns.iterator();
		while (iterator.hasNext()) {
			String column = iterator.next().getCode();
			if (column.equals(primaryKey)) {
				continue;
			}
			sb.append(column + " = ?");
			if (iterator.hasNext()) {
				sb.append(", ");
			} else {
				sb.append(" WHERE " + primaryKey + " = ?");
				break;
			}
		}
		System.out.println(sb.toString());
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sb.toString());
		// Strani ključevi
		int k = 0;
		if (outsideColumns != null) {
			k = outsideColumns.size();
		}
		for (int i = 0; i < colNames.length - k; i++) {
			stmt.setString(i + 1 + k, (String) colNames[i + k]);
		}
		// Za strane kljuceve
		for (int i = 0; i < k; i++) {
			stmt.setInt(i + 1, (int) colNames[i]);
		}

		// Za verziju
		Integer version = (int) getValueAt(index, getColumnCount() - 1) + 1;
		stmt.setInt(colNames.length + 1, version);
		// Za where od primarnog
		stmt.setInt(colNames.length + 2, (int) getValueAt(index, 0));

		int rowsAffected = stmt.executeUpdate();
		stmt.close();
		DBConnection.getConnection().commit();
		DBConnection.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		if (rowsAffected > 0) {
			setValueAt(version, index, getColumnCount() - 1);
			for (int i = 0; i < colNames.length; i++) {
				if (i + 1 < columns.size()) {
					MetaColumn col = (MetaColumn) columns.toArray()[i + 1];
					if (col.getJClassName().equals("java.lang.Boolean")) {
						if (colNames[i].equals("1")) {
							setValueAt("Da", index, i + 1);
						} else {
							setValueAt("Ne", index, i + 1);
						}
					} else {
						setValueAt(colNames[i], index, i + 1);
					}
				} else {
					setValueAt(colNames[i], index, i + 1);
				}
			}
			if (outsideColumns != null) {
				for (int i = 0; i < outsideColumns.size(); i++) {
					setValueAt(outsideColumns.get(i).getDisplayColumnValue().get(0), index, i + colNames.length + 1);
				}
			}
			fireTableDataChanged();
		}
	}

	@Override
	public void deleteRow(int index) throws SQLException {
		checkRow(index);
		PreparedStatement statement = DBConnection.getConnection().prepareStatement(
				"DELETE FROM " + tableCode + " WHERE " + primaryKey + "=?");
		Integer sifra = (Integer) getValueAt(index, 0);
		statement.setInt(1, sifra);

		int rowsAffected = statement.executeUpdate();
		statement.close();
		DBConnection.getConnection().commit();
		DBConnection.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		if (rowsAffected > 0) {
			removeRow(index);
			fireTableDataChanged();
		}

	}

	@Override
	public int insertRow(Object[] colNames) throws SQLException {

		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO " + tableCode + " (");
		Iterator<MetaColumn> iterator = columns.iterator();
		while (iterator.hasNext()) {
			String column = iterator.next().getCode();
			if (column.equals(primaryKey)) {
				continue;
			}
			sb.append(column);
			if (iterator.hasNext()) {
				sb.append(", ");
			} else {
				sb.append(") VALUES (");
				break;
			}
		}

		for (int i = 0; i < colNames.length; i++)
			sb.append("?, ");
		// Za verziju
		sb.append("?)");

		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sb.toString(),
				Statement.RETURN_GENERATED_KEYS);
		int retVal = 0;

		for (int i = 0; i < colNames.length; i++) {
			stmt.setObject(i + 1, colNames[i]);
		}
		// Za verziju
		stmt.setInt(colNames.length + 1, 1);

		System.out.println(sb.toString());
		int rowsAffected = stmt.executeUpdate();
		if (rowsAffected > 0) {
			stmt.getGeneratedKeys().next();
			int k = 0;
			if (outsideColumns != null) {
				k = outsideColumns.size();
			}
			Object[] insertion = new Object[colNames.length + 2 + k];
			insertion[0] = stmt.getGeneratedKeys().getInt(1);
			for (int i = 0; i < colNames.length; i++) {
				insertion[i + 1] = colNames[i];
			} // Ovde je za sada hardkodovano da ce uvek biti jedna povezana kolona za prikaz, ali je stavljena lista u
				// slucaju da kasnije bude potrebno par kolona.
			if (outsideColumns != null) {
				for (int i = 0; i < k; i++) {
					insertion[colNames.length + 1 + i] = outsideColumns.get(i).getDisplayColumnValue().get(0);
				}
			}
			insertion[colNames.length + k + 1] = 1;
			retVal = sortedInsert(insertion);
			fireTableDataChanged();
		}

		stmt.close();

		DBConnection.getConnection().commit();
		return retVal;

	}

	@Override
	public int sortedInsert(Object[] colNames) {
		int left = 0;
		int right = getRowCount() - 1;
		int mid = (left + right) / 2;

		while (left <= right) {
			mid = (left + right) / 2;

			String aSifra = (String) getValueAt(mid, columnForSorting);

			if (SortUtils.getLatCyrCollator().compare((String) colNames[0].toString(), aSifra) > 0)
				left = mid + 1;
			else if (SortUtils.getLatCyrCollator().compare((String) colNames[0].toString(), aSifra) < 0)
				right = mid - 1;
			else
				break;
		}

		Object[] rowForInsert = new Object[colNames.length];

		for (int i = 0; i < colNames.length; i++)
			rowForInsert[i] = colNames[i];

		insertRow(left, rowForInsert);

		return left;
	}

	public static void main(String[] args) {
		GenericTableModel gtm = TableModelCreator.createTableModel("Mesto", null);

		try {
			gtm.updateRow(new String[] { "Test1", "Test2" }, 1);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.close();
		}
		DBConnection.close();
	}

	public String getBasicQuery() {
		return basicQuery;
	}

	public void setBasicQuery(String basicQuery) {
		this.basicQuery = basicQuery;
	}

	public String getJoinQuery() {
		return joinQuery;
	}

	public void setJoinQuery(String joinQuery) {
		this.joinQuery = joinQuery;
	}

	public String getWhereStmt() {
		return whereStmt;
	}

	public void setWhereStmt(String whereStmt) {
		this.whereStmt = whereStmt;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
}
