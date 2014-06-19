package util;

import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;


public class CertificatesUtility {
	
	public static String getOwner(X509Certificate cert) {
		try {
			
			String dn = cert.getSubjectX500Principal().getName();
			String ownerName = null;
			
			LdapName ln = new LdapName(dn);

	        for (Rdn rdn : ln.getRdns()) {
	            if (rdn.getType().equalsIgnoreCase("CN")) {
	            	ownerName = rdn.getValue().toString();
	                break;
	            }
	        }
			
			if(ownerName != null)
				return ownerName;
		}	catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getIssuer(X509Certificate cert) {
		try {
			
			String dn = cert.getIssuerX500Principal().getName();
			String issuerName = null;
			
			LdapName ln = new LdapName(dn);

	        for (Rdn rdn : ln.getRdns()) {
	            if (rdn.getType().equalsIgnoreCase("CN")) {
	            	issuerName = rdn.getValue().toString();
	                break;
	            }
	        }
			
			if(issuerName != null)
				return issuerName;
		}	catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
