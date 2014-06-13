package provider.banka;

import javax.ejb.Stateless;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "NalogPort", 
serviceName = "BankaServis",
targetNamespace = "http://www.toomanysecrets.com/bankaServis",
wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class NalogProvider  implements Provider<DOMSource> {
	
	@Override
	public DOMSource invoke(DOMSource request) {
		return null;
	}

}
