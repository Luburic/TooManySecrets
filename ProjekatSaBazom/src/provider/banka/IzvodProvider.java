package provider.banka;

import javax.ejb.Stateless;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaIzvodPort", 
					serviceName = "BankaIzvod",
					targetNamespace = "http://www.toomanysecrets.com/BankaIzvod",
					wsdlLocation = "WEB-INF/wsdl/BankaIzvod.wsdl")
public class IzvodProvider  implements Provider<DOMSource>{
	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/BankaIzvod";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	
	
	public IzvodProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DOMSource invoke(DOMSource request) {
		return new DOMSource();
	}

	
}
