package root.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import root.dbConnection.DBConnection;

public class Lookup {
	public static String getDrzava(String sifraDrzave) throws SQLException {
		String naziv = "";
		if (sifraDrzave == "")
			return naziv;
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT naziv_drzave FROM Drzava WHERE sifra_drzave = ?");
		stmt.setString(1, sifraDrzave);
		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			naziv = rset.getString("naziv_drzave");
		}
		rset.close();
		stmt.close();
		return naziv;
	}

	public static Vector<ComboBoxPair> getComboBoxEntity(String tableCode, String pk, String neededColumn,
			String whereClause) throws SQLException {
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT " + pk + ", " + neededColumn + " FROM " + tableCode + " " + whereClause);
		ResultSet rset = stmt.executeQuery();
		Vector<ComboBoxPair> retVal = new Vector<ComboBoxPair>();
		while (rset.next()) {
			retVal.add(new ComboBoxPair(rset.getInt(pk), rset.getString(neededColumn)));
		}
		rset.close();
		stmt.close();
		return retVal;
	}

	public static Vector<ComboBoxPair> getMagacini() throws SQLException {
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT id_jedinice, naziv_jedinice FROM Organizaciona_jedinica WHERE magacin=1");
		ResultSet rset = stmt.executeQuery();
		Vector<ComboBoxPair> retVal = new Vector<ComboBoxPair>();
		while (rset.next()) {
			retVal.add(new ComboBoxPair(rset.getInt(1), rset.getString(2)));
		}
		rset.close();
		stmt.close();
		return retVal;
	}
}