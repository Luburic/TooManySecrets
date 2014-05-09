package root.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import root.dbConnection.DBConnection;

public class Lookup {
	public static String getDrzava(String sifraDrzave) throws SQLException {
		String naziv = "";
		if (sifraDrzave == "")
			return naziv;
		PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
				"SELECT DR_NAZIV FROM DRZAVA WHERE DR_SIFRA = ?");
		stmt.setString(1, sifraDrzave);
		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			naziv = rset.getString("DR_NAZIV");
		}
		rset.close();
		stmt.close();
		return naziv;
	}
}