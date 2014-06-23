package basexdb;

import java.util.ResourceBundle;

/**
 * Klasa sadrzi set podrzanih HTTP metoda.
 * 
 * @author Igor Cverdelj-Fogarasi
 *
 */
public class RequestMethod {

	public static final String PUT = "PUT";

	public static final String GET = "GET";
	
	public static final String POST = "POST";
	
	public static final String DELETE = "DELETE";
	
	public static final String REST_URL = ResourceBundle.getBundle("basex").getString("rest.url");

}
