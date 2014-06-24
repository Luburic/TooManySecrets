package basexdb;

import java.io.File;
import java.io.FileInputStream;


public class DBRun {

	public static void init(){
		if(RESTUtility.isRunning("http://localhost:8081/BaseX75/rest")){
			initBanke();
			initCentralna();
			initFirme();
			System.out.println("Inicijalizacija baze uspesno zavrsena");
		}else{
			System.out.println("Baza nije aktivna");
		}
	}


	public static void initCentralna(){
		try{

			RESTUtility.dropSchema("http://localhost:8081/BaseX75/rest/centralnabanka");
			RESTUtility.createSchema("http://localhost:8081/BaseX75/rest/centralnabanka");

			RESTUtility.createResource("http://localhost:8081/BaseX75/rest/centralnabanka", "centralnabanka.xml", new FileInputStream(new File("WEB-INF/database/centralnabanka_init.xml")));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void initBanke(){
		try{

			RESTUtility.dropSchema("http://localhost:8081/BaseX75/rest/bankaaa");
			RESTUtility.dropSchema("http://localhost:8081/BaseX75/rest/bankab");
			RESTUtility.createSchema("http://localhost:8081/BaseX75/rest/bankaa");
			RESTUtility.createSchema("http://localhost:8081/BaseX75/rest/bankab");

			RESTUtility.createResource("http://localhost:8081/BaseX75/rest/bankaa", "banka.xml", new FileInputStream(new File("WEB-INF/database/bankaa_init.xml")));
			RESTUtility.createResource("http://localhost:8081/BaseX75/rest/bankab", "banka.xml", new FileInputStream(new File("WEB-INF/database/bankab_init.xml")));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void initFirme(){
		try{

			RESTUtility.dropSchema("http://localhost:8081/BaseX75/rest/firmaa");
			RESTUtility.dropSchema("http://localhost:8081/BaseX75/rest/firmab");
			RESTUtility.createSchema("http://localhost:8081/BaseX75/rest/firmaa");
			RESTUtility.createSchema("http://localhost:8081/BaseX75/rest/firmab");

			RESTUtility.createResource("http://localhost:8081/BaseX75/rest/firmaa", "firma.xml", new FileInputStream(new File("WEB-INF/database/firmaa_init.xml")));
			RESTUtility.createResource("http://localhost:8081/BaseX75/rest/firmab", "firma.xml", new FileInputStream(new File("WEB-INF/database/firmab_init.xml")));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		init();
	}

}
