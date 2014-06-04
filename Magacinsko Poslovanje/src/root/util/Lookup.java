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

	public static boolean getGodinaZakljucena(Integer idGodine) throws SQLException {
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT zakljucena FROM Poslovna_godina WHERE id_poslovne_godine = " + idGodine);
		ResultSet rset = stmt.executeQuery();
		boolean retVal = false;
		rset.next();
		if (rset.getByte(1) == 1) {
			retVal = true;
		}
		rset.close();
		stmt.close();
		return retVal;
	}

	public static String getRedniBroj(String tableCode, String columnCode) throws SQLException {
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT MAX(" + columnCode + ") FROM " + tableCode);
		ResultSet rset = stmt.executeQuery();
		rset.next();
		Integer retVal = rset.getInt(1);
		if (retVal == null) {
			retVal = 1;
		} else {
			retVal++;
		}

		rset.close();
		stmt.close();
		return retVal.toString();
	}

	public static boolean getZakljucen(String tableCode, String statusColumn, String childWhere) throws SQLException {
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT " + statusColumn + " FROM " + tableCode + childWhere);
		ResultSet rset = stmt.executeQuery();
		rset.next();
		String retVal = rset.getString(1);
		rset.close();
		stmt.close();
		if (retVal.trim().equals("u fazi formiranja")) {
			return false;
		}
		return true;
	}
}