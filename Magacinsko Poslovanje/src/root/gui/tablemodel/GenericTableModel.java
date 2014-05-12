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

import javax.swing.table.DefaultTableModel;

import root.dbConnection.DBConnection;
import root.util.Constants;
import root.util.SortUtils;
import rs.mgifos.mosquito.model.MetaColumn;

@SuppressWarnings("serial")
public class GenericTableModel extends DefaultTableModel implements ITableModel {

	protected String basicQuery = "";
	protected String joinQuery = "";
	protected String whereStmt = "";
	protected String orderBy = "";

	protected String openAsChildQuery = "";

	private String tableCode;
	private Collection<MetaColumn> columns;
	private String primaryKey;

	public GenericTableModel(String tableCode, Object[] colNames, Collection<MetaColumn> columns) {
		super(colNames, 0);
		this.tableCode = tableCode;
		this.columns = columns;
		this.primaryKey = columns.iterator().next().getCode();
	}

	@Override
	public void open() throws SQLException {
		if (basicQuery.equals("")) {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT ");
			Iterator<MetaColumn> iterator = columns.iterator();
			while (iterator.hasNext()) {
				String name = iterator.next().getCode();
				sb.append(name);
				if (iterator.hasNext()) {
					sb.append(", ");
				} else {
					sb.append(" FROM " + tableCode);
					break;
				}
			}
			basicQuery = sb.toString();
		}

		if (orderBy.equals("")) {
			orderBy = " ORDER BY " + columns.iterator().next().getCode();
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
				if (rset.getMetaData().getColumnType(i + 1) == Types.BOOLEAN) {
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
		// Kad se ucita onda je string, kad se sacuva onda je int.
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
					String newValue;
					if (rset.getMetaData().getColumnType(i + 1) == Types.BOOLEAN) {
						if (rset.getString(i + 1).equals("1")) {
							newValue = "Da";
						} else {
							newValue = "Ne";
						}
					} else if (rset.getMetaData().getColumnType(i + 1) == Types.DATE) {
						DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date = rset.getDate(i + 1);
						newValue = formatter.format(date);
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

		if (this instanceof Object) {
			openAsChildQuery = "SELECT nm_sifra, nm_naziv, naseljeno_mesto.dr_sifra, naseljeno_mesto.dr_naziv FROM naseljeno_mesto JOIN drzava on naseljeno_mesto.dr_sifra = drzava.dr_sifra WHERE naseljeno_mesto.dr_sifra ='"
					+ whereValue + "' ORDER BY nm_sifra";
		}

		if (this instanceof Object) {

		}

		fillData(openAsChildQuery);

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
		for (int i = 0; i < colNames.length; i++)
			stmt.setString(i + 1, (String) colNames[i]);
		// Za verziju
		Integer version = (int) getValueAt(index, getColumnCount() - 1) + 1;
		stmt.setInt(colNames.length + 1, version);
		// Za where od primarnog
		stmt.setInt(colNames.length + 2, (int) getValueAt(index, 0));
		// Ovde sam stao

		int rowsAffected = stmt.executeUpdate();
		stmt.close();
		DBConnection.getConnection().commit();
		DBConnection.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		if (rowsAffected > 0) {
			setValueAt(version, index, getColumnCount() - 1);
			for (int i = 0; i < colNames.length; i++) {
				setValueAt(colNames[i], index, i + 1);
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

		for (int i = 0; i < colNames.length; i++)
			stmt.setString(i + 1, (String) colNames[i]);
		// Za verziju
		stmt.setInt(colNames.length + 1, 1);

		System.out.println(sb.toString());

		int rowsAffected = stmt.executeUpdate();
		if (rowsAffected > 0) {
			stmt.getGeneratedKeys().next();
			Object[] insertion = new Object[colNames.length + 2];
			insertion[0] = stmt.getGeneratedKeys().getInt(1);
			for (int i = 0; i < colNames.length; i++) {
				insertion[i + 1] = colNames[i];
			}
			insertion[colNames.length + 1] = 1;
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

			String aSifra = (String) getValueAt(mid, 1);

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
}
