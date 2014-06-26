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
import basexdb.centralna.Centralna;


public class CentralnaDBUtil {
	
	public static Centralna loadCentralnaDatabase(String urlString){
		Centralna bank=null;
		try{
			InputStream is=RESTUtility.retrieveResource("fn:doc('"+RESTUtility.getDatabaseName(urlString)+"/centralna.xml')", urlString, RequestMethod.GET);
			JAXBContext ctx = JAXBContext.newInstance(Centralna.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			bank = (Centralna) unmarshaller.unmarshal(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		return bank;
	}
	
	public static void storeCentralnaDatabase(Centralna bankaSema,String urlString){
		try{
			URL url = new URL(urlString + "/" + "centralna.xml");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(RequestMethod.PUT.toString());
			OutputStream os = new BufferedOutputStream(conn.getOutputStream());
			JAXBContext ctx = JAXBContext.newInstance(Centralna.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.marshal(bankaSema, os);
			IOUtils.closeQuietly(os);
			conn.getResponseCode();
			conn.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
