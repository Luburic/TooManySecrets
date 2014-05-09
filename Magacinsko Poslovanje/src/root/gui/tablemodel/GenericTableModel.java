package root.gui.tablemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.table.DefaultTableModel;

import root.dbConnection.DBConnection;
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

			for (int i = 0; i < columnCount; i++)
				rowForInsert[i] = rset.getObject(i + 1);

			addRow(rowForInsert);

		}
		rset.close();
		stmt.close();
		fireTableDataChanged();

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

	@Override
	public void deleteRow(int index) throws SQLException {

		PreparedStatement statement = DBConnection.getConnection().prepareStatement(
				"DELETE FROM " + tableCode + " WHERE " + primaryKey + "=?");
		Integer sifra = (Integer) getValueAt(index, 0);
		statement.setInt(1, sifra);

		int rowsAffected = statement.executeUpdate();
		statement.close();
		DBConnection.getConnection().commit();

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

		for (int i = 0; i < colNames.length - 1; i++)
			sb.append("?, ");
		sb.append("?)");

		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sb.toString(),
				Statement.RETURN_GENERATED_KEYS);
		int retVal = 0;

		for (int i = 0; i < colNames.length; i++)
			stmt.setString(i + 1, (String) colNames[i]);

		System.out.println(sb.toString());

		int rowsAffected = stmt.executeUpdate();
		if (rowsAffected > 0) {
			stmt.getGeneratedKeys().next();
			Object[] insertion = new Object[colNames.length + 1];
			insertion[0] = stmt.getGeneratedKeys().getInt(1);
			for (int i = 0; i < colNames.length; i++) {
				insertion[i + 1] = colNames[i];
			}
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
		GenericTableModel gtm = TableModelCreator.createTableModel("Država", null);

		try {
			gtm.open();
			gtm.insertRow(new String[] { "SRB", "Srbija" });
			gtm.deleteRow(5);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.close();
		}
		DBConnection.close();
	}
}
