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
import basexdb.centralna.CentralnaSema;


public class CentralnaDBUtil {
	
	public static CentralnaSema loadCentralnaDatabase(String urlString){
		CentralnaSema centralBank=null;
		try{
			InputStream is=RESTUtility.retrieveResource("fn:doc('"+RESTUtility.getDatabaseName(urlString)+"/centralnabanka.xml')", urlString, RequestMethod.GET);
			JAXBContext ctx = JAXBContext.newInstance(CentralnaSema.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			centralBank = (CentralnaSema) unmarshaller.unmarshal(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		return centralBank;
	}
	
	public static void storeCentralnaDatabase(CentralnaSema centralnaSema,String urlString){
		try{
			URL url = new URL(urlString + "/" + "centralnabanka.xml");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(RequestMethod.PUT.toString());
			OutputStream os = new BufferedOutputStream(conn.getOutputStream());
			JAXBContext ctx = JAXBContext.newInstance(CentralnaSema.class);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.marshal(centralnaSema, os);
			IOUtils.closeQuietly(os);
			conn.getResponseCode();
			conn.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
