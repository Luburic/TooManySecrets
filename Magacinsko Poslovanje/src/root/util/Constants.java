package root.util;

import java.util.ArrayList;

import root.data.User;

public class Constants {

	public static final int MODE_EDIT = 1;
	public static final int MODE_ADD = 2;
	public static final int MODE_SEARCH = 3;

	public static final long ROLE_ODG_LICE = 1;
	public static final long ROLE_SEF_RAC = 2;
	public static final long ROLE_DIR = 3;

	public static final String DB_INTEGER_BIG = "java.math.BigInteger";
	public static final String DB_DECIMAL_BIG = "java.math.BigDecimal";
	public static final String DB_DATE = "java.sql.Date";
	public static final String DB_VARCHAR = "java.lang.String";
	public static final String DB_BOOLEAN = "java.lang.Boolean";
	public static final String DB_VARCHAR_ENUM = "java.lang.String";

	public static final String J_COMBO_BOX = "javax.swing.JComboBox";
	public static final String ZOOM_ELEMENT = "zoom element";

	public static final String VALIDATION_MANDATORY_FIELD = "Ovo polje je obavezno za unos.";
	public static final String VALIDATION_OUT_OF_RANGE = "Vrednost unosa mora biti u opsegu ";
	public static final String VALIDATION_DATE_OUT_OF_RANGE = "Datum mora biti u periodu od ";
	public static final String VALIDATION_COMBOBOX_FIELD = "Potrebno je odabrati jednu od ponudjenih vrednosti.";
	public static final String VALIDATION_REGEX = "Uneti sadrzaj ne odgovara predvidjenom formatu unosa.";
	public static final String VALIDATION_ZOOM_FIELD = "Uneti sadrzaj ne odgovara podacima u sistemu.";

	public static final String DB_CONNECTION = "magacinConnection";
	public static final String MODEL_LOCATION = "model/Magacinsko Poslovanje.pdm";

	public static int MODE;

	private static ArrayList<Integer> zoomModes = new ArrayList<Integer>();

	private static User currentUser;

	public static void stepIntoZoom() {
		zoomModes.add(MODE);
	}

	public static void getModeFromZoom() {
		int size = zoomModes.size();
		int result = 1;
		if (size > 0) {
			result = zoomModes.get(size - 1);
			zoomModes.remove(size - 1);
		}
		MODE = result;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User usr) {
		currentUser = usr;
	}
}
