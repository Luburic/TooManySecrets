package ws.style.message;

import javax.ejb.Stateless;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.transform.dom.DOMSource;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "MT103Port", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class MT103Provider implements javax.xml.ws.Provider<DOMSource>{

	
	@Override
	public DOMSource invoke(DOMSource request) {
		// TODO Auto-generated method stub
		return null;
	}

}
