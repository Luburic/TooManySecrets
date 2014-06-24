package util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlTransient;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

//Klasa koja sadrzi mapiranja namespace-a na prefikse

@XmlTransient
public class NSPrefixMapper extends NamespacePrefixMapper {

	private HashMap<String, String> mappings;

	public NSPrefixMapper(String defaultNS) { 
		//inicijalizacija
		mappings = new LinkedHashMap<String, String>(); 
		setDefaultMappings(defaultNS); 
	}

	protected void setDefaultMappings(String defaultNS) { 
		clear();
		//ovde se dodaju maparianja nemaspace prefix
		//Ako se umesto prefiksa "ex5" postavi "", onda ce to biti default namespace
		switch (defaultNS.toLowerCase()) {
		case "faktura":
			addMapping("http://www.toomanysecrets.com/faktura", ""); 
			break;
		case "izvod":
			addMapping("http://www.toomanysecrets.com/zahtevzaizvod", ""); 
			break;
		case "nalog":
			addMapping("http://www.toomanysecrets.com/nalog", "");
			break;
		case "mt103":
			addMapping("http://www.toomanysecrets.com/mt103", "");
			break;
		case "mt102":
			addMapping("http://www.toomanysecrets.com/mt102", "");
			break;
		case "mt900":
			addMapping("http://www.toomanysecrets.com/mt900", "");
			break;
		case "mt910":
			addMapping("http://www.toomanysecrets.com/mt910", "");
			break;
		case "notification":
			addMapping("http://www.toomanysecrets.com/notification", "");
			break;
		case "presek":
			addMapping("http://www.toomanysecrets.com/presek", "");
			break;
		case "fault":
			addMapping("http://www.toomanysecrets.com/fault", "");
			break;
		default:
			break;
		}
		addMapping("http://www.w3.org/2001/XMLSchema-instance", "xsi"); 
		addMapping("http://java.sun.com/xml/ns/jaxb", "jaxb"); 
	}

	public void addMapping(String uri, String prefix){
		mappings.put(uri, prefix);
	} 

	public String getMapping(String uri){
		return (String)mappings.get(uri);
	} 
	public HashMap<String, String> getMappings(){
		return mappings;
	} 
	public void clear(){
		mappings.clear();
	}

	public String getPreferredPrefix(String namespaceURI, String suggestion, boolean requirePrefix) { 
		String toReturn = getMapping(namespaceURI); 
		if(toReturn != null)
			return toReturn;
		return suggestion; 
	} 

}
