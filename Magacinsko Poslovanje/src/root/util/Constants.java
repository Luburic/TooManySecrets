package root.util;

import root.data.User;

public class Constants {

	public static final int MODE_EDIT = 1;
	public static final int MODE_ADD = 2;
	public static final int MODE_SEARCH = 3;

	public static final String VALIDATION_BROJ = "Dato polje zahteva unos isključivo cifri.";
	public static final String VALIDATION_SIFRA = "Karakteri moraju da budu od A-Z.";
	public static final String VALIDATION_CHECKBOX = "Potrebno je čekirat bar jednu stavku.";
	public static final String VALIDATION_MANDATORY_FIELD = "Ovo polje je obavezno za unos.";
	public static final String VALIDATION_OUT_OF_RANGE = "Vrednost unosa mora biti u opsegu ";
	public static final String VALIDATION_DATE_OUT_OF_RANGE = "Datum mora biti u periodu od ";
	public static final String VALIDATION_COMBOBOX_FIELD = "Potrebno je odabrati jednu od ponudjenih vrednosti.";
	public static final String VALIDATION_REGEX = "Uneti sadrzaj ne odgovara predvidjenom formatu unosa.";
	public static final String VALIDATION_ZOOM_FIELD = "Uneti sadrzaj ne odgovara podacima u sistemu.";

	public static final String DB_CONNECTION = "magacinConnection";
	public static final String MODEL_LOCATION = "model/Magacinsko Poslovanje.pdm";
	public static final String ERROR_RECORD_WAS_DELETED = "Slog je obrisan od strane drugog korisnika.";
	public static final String ERROR_RECORD_WAS_CHANGED = "Slog je promenjen od strane drugog korisnika. Pogledajte njegovu trenutnu vrednost.";

	public static Integer idGodine = 0;
	public static Integer idPreduzeca = 0;
	public static boolean godinaZakljucena = false;
	public static Object nazivPreduzeca = "";
	public static Object godina = 0;

	public static int MODE;

	private static User currentUser;

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User usr) {
		currentUser = usr;
	}
}
