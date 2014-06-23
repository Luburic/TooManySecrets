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
import basexdb.firma.FirmeSema;

public class FirmaDBUtil {
	
	public static FirmeSema loadFirmaDatabase(String urlString){
		FirmeSema company=null;
		try{
			InputStream is=RESTUtility.retrieveResource("fn:doc('"+RESTUtility.getDatabaseName(urlString)+"/firma.xml')", urlString, RequestMethod.GET);
			JAXBContext ctx = JAXBContext.newInstance(FirmeSema.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			company = (FirmeSema) unmarshaller.unmarshal(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		return company;
	}
	
	public static void storeFirmaDatabase(FirmeSema firmaSema,String urlString){
		try{
			URL url = new URL(urlString + "/" + "firma.xml");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(RequestMethod.PUT.toString());
			OutputStream os = new BufferedOutputStream(conn.getOutputStream());
			JAXBContext ctx = JAXBContext.newInstance(FirmeSema.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.marshal(firmaSema, os);
			IOUtils.closeQuietly(os);
			conn.getResponseCode();
			conn.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
