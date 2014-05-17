package banks;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.bind.annotation.adapters.XmlAdapter;



public class CertificateConvertor
extends XmlAdapter<byte[], X509Certificate>
{


	public X509Certificate unmarshal(byte[] data) {
		X509Certificate c = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream bais =  new ByteArrayInputStream(data);
			c = (X509Certificate) cf.generateCertificate(bais);

		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return c;
	}

	public byte[] marshal(X509Certificate cert) {

		try {
			return cert.getEncoded();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}