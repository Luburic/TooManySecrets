package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//Konverter za jva.util.Date klasu u string formata yyyy-MM-dd i obrnuto
//metode moraju biti static, i imati navedene parametre, a mogu se zvati bilo kako
public class MyDatatypeConverter {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  public static Date parseDate(String value) {
	  try {
		return dateFormat.parse(value);
	} catch (ParseException e) {
		e.printStackTrace();
		return null;
	}
	
  }

  public static String printDate(Date value) {
	  if(value != null)
		  return dateFormat.format(value);
	  else
		  return null;
		  
  }
}
