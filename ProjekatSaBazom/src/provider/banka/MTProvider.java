package provider.banka;

import javax.ejb.Stateless;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import util.ConstantsXWS;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "OdobrenjeMtPort", 
					serviceName = "OdobrenjeMt",
					targetNamespace = ConstantsXWS.TARGET_NAMESPACE_CENTRALNA_BANKA_MT103,
					wsdlLocation = "WEB-INF/wsdl/CentralnaRTGSNalog.wsdl")
public class MTProvider implements javax.xml.ws.Provider<DOMSource>{

	@Override
	public DOMSource invoke(DOMSource arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
