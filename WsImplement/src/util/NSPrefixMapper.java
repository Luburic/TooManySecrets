package util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlTransient;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

//Klasa koja sadrzi mapiranja namespace-a na prefikse

@XmlTransient
public class NSPrefixMapper extends NamespacePrefixMapper {

	private HashMap<String, String> mappings;

	public NSPrefixMapper() { 
		//inicijalizacija
		mappings = new LinkedHashMap<String, String>(); 
		setDefaultMappings(); 
	}

	protected void setDefaultMappings() { 
		clear();
		//ovde se dodaju maparianja nemaspace prefix
		//Ako se umesto prefiksa "ex5" postavi "", onda ce to biti default namespace
		addMapping("http://www.toomanysecrets.com/tipovi", ""); 
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
		//System.out.println("Pozvano za " + namespaceURI + " " + suggestion + " " + requirePrefix);
		String toReturn = getMapping(namespaceURI); 
		if(toReturn != null)
			return toReturn;
		return suggestion; 
	} 

}
