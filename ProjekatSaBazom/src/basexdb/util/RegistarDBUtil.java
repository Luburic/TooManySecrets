package basexdb.util;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import basexdb.RESTUtility;
import basexdb.RequestMethod;
import basexdb.registar.Registar;

public class RegistarDBUtil {
	
	public static Registar loadRegistarDatabase(String urlString){
		Registar registar = null;
		try{
			InputStream is=RESTUtility.retrieveResource("fn:doc('"+RESTUtility.getDatabaseName(urlString)+"/registar.xml')", urlString, RequestMethod.GET);
			JAXBContext ctx = JAXBContext.newInstance(Registar.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			registar = (Registar) unmarshaller.unmarshal(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		return registar;
	}
	
	public static void storeRegistarDatabase(Registar registar,String urlString){
		try{
			URL url = new URL(urlString + "/" + "registar.xml");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(RequestMethod.PUT.toString());
			OutputStream os = new BufferedOutputStream(conn.getOutputStream());
			JAXBContext ctx = JAXBContext.newInstance(Registar.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.marshal(registar, os);
			IOUtils.closeQuietly(os);
			conn.getResponseCode();
			conn.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
