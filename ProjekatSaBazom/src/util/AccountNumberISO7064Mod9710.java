package util;

public class AccountNumberISO7064Mod9710 {
	
	/**
	 * Verify account number
	 * @param digits Account number including control digits
	 * @return Verification result
	 */
	public static boolean verify(String digits) {
		return (Long.parseLong(digits) % 97 == 1);
	}
	
	/**
	 * Generate check number
	 * @param digits Account number without check (control) digits
	 * @return Generated check number
	 */
	public static int computeCheck(String digits) {
		return ((int)(98 - (Long.parseLong(digits) * 100) % 97L)) % 97;
	}
	
	/**
	 * Generate check number as string, padd leading 0 if number is less than 10
	 * @param digits Account number without check (control) digits
	 * @return Generated check number
	 */
	public static String computeCheckAsString(String digits) {
		int check = computeCheck(digits);
		return check > 9 ? String.valueOf(check) : '0' + String.valueOf(check);
	}
	
}
