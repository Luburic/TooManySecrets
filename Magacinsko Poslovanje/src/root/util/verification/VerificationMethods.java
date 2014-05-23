package root.util.verification;

public class VerificationMethods {
	public static boolean containsValidCharacters(String fieldValue) {
		int n = fieldValue.length();
		for (int i = 0; i < n; i++) {
			int k = fieldValue.charAt(i);
			if (k < 65 || k > 122 || (k > 90 && k < 97)) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsNumbers(String fieldValue) {
		try {
			Integer number = Integer.parseInt(fieldValue);
			if (number > 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}
}
