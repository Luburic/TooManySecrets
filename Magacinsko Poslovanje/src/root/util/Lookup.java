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
}