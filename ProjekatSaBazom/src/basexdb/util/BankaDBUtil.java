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
import basexdb.banka.BankeSema;


public class BankaDBUtil {
	
	public static BankeSema loadBankaDatabase(String urlString){
		BankeSema bank=null;
		try{
			InputStream is=RESTUtility.retrieveResource("fn:doc('"+RESTUtility.getDatabaseName(urlString)+"/banka.xml')", urlString, RequestMethod.GET);
			JAXBContext ctx = JAXBContext.newInstance(BankeSema.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			bank = (BankeSema) unmarshaller.unmarshal(is);
		}catch(Exception e){
			e.printStackTrace();
		}
		return bank;
	}
	
	public static void storeBankaDatabase(BankeSema bankaSema,String urlString){
		try{
			URL url = new URL(urlString + "/" + "banka.xml");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(RequestMethod.PUT.toString());
			OutputStream os = new BufferedOutputStream(conn.getOutputStream());
			JAXBContext ctx = JAXBContext.newInstance(BankeSema.class);
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
