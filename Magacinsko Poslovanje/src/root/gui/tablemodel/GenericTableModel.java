package root.gui.tablemodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import root.dbConnection.DBConnection;
import root.util.SortUtils;
import rs.mgifos.mosquito.IMetaLoader;
import rs.mgifos.mosquito.LoadingException;
import rs.mgifos.mosquito.impl.pdm.PDMetaLoader;
import rs.mgifos.mosquito.model.MetaColumn;
import rs.mgifos.mosquito.model.MetaModel;
import rs.mgifos.mosquito.model.MetaTable;

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
		/*IMetaLoader metaLoader = new PDMetaLoader();

		Properties properties = new Properties();
		properties.put(PDMetaLoader.FILENAME, "model/Magacinsko Poslovanje.pdm");

		try {
			MetaModel model = metaLoader.getMetaModel(properties);
			for (MetaTable table : model) {
				if (table.getCode().equals(tableCode)) {
					columns = table.cColumns();
				}
			}
		} catch (LoadingException e) {
			e.printStackTrace();
		}*/

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
				if (name.equals(primaryKey)) {
					continue;
				}
				sb.append(iterator.next().getCode());
				if (iterator.hasNext()) {
					sb.append(", ");
				} else {
					sb.append("FROM " + tableCode);
					break;
				}
			}
		}

		if (orderBy.equals("")) {
			orderBy = " ORDER BY " + columns.iterator().next().getCode();
		}

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

		String sifra = (String) getValueAt(index, 0);
		statement.setString(1, sifra);

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

		sb.append("INSERT INTO cveki.dbo." + tableCode + " (");
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
		stmt.close();

		DBConnection.getConnection().commit();

		if (rowsAffected > 0) {
			retVal = sortedInsert(colNames);
			fireTableDataChanged();
		}
		return retVal;

	}

	@Override
	public int sortedInsert(Object[] colNames) {
		int left = 0;
		int right = getRowCount() - 1;
		int mid = (left + right) / 2;

		while (left <= right) {
			mid = (left + right) / 2;

			String aSifra = (String) getValueAt(mid, 0);

			if (SortUtils.getLatCyrCollator().compare((String) colNames[0], aSifra) > 0)
				left = mid + 1;
			else if (SortUtils.getLatCyrCollator().compare((String) colNames[0], aSifra) < 0)
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

	// Postavljeno radi testiranja ucitavanja metapodataka
	public static void main(String[] args) {
		GenericTableModel gtm = TableModelCreator.createTableModel("Država");
		// GenericTableModel gtm = new GenericTableModel("Drzava", new String[] { "Šifra države", "Naziv države" });

		try {
			gtm.insertRow(new String[] { "SRB", "Srbija" });
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DBConnection.close();
		}
		DBConnection.close();
	}
}
