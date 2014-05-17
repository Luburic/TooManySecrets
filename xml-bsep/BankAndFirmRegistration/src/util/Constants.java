package util;

import java.io.File;

/**
 * Constants for default self-signed certificate.
 * 
 * 
 * 
 * 
 *
 */
public class Constants {
	
	public static File file = new File("./keystores/centralnabanka.jks");
	public static String alias = "centralnabanka";
	public static char[] ksPass = "centralnabanka".toCharArray();
	public static char[] pkPass = "centralnabanka".toCharArray();

}
