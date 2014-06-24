package util;

import util.accessControl.TKorisnik;
import util.accessControl.TRola;

public class ConstantsXWS {
	// public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";

	public static final String NAMESPACE_XSD_FAKTURA = "http://www.toomanysecrets.com/faktura";
	public static final String NAMESPACE_XSD_MT102 = "http://www.toomanysecrets.com/mt102";
	public static final String NAMESPACE_XSD_MT103 = "http://www.toomanysecrets.com/mt103";
	public static final String NAMESPACE_XSD_MT900 = "http://www.toomanysecrets.com/mt900";
	public static final String NAMESPACE_XSD_MT910 = "http://www.toomanysecrets.com/mt910";
	public static final String NAMESPACE_XSD_NALOG = "http://www.toomanysecrets.com/nalog";
	public static final String NAMESPACE_XSD_PRESEK = "http://www.toomanysecrets.com/presek";
	public static final String NAMESPACE_XSD_ZAHTEV = "http://www.toomanysecrets.com/zahtevzaizvod";
	public static final String NAMESPACE_XSD_NOTIFICATION = "http://www.toomanysecrets.com/notification";
	public static final String NAMESPACE_XSD_FAULT = "http://www.toomanysecrets.com/fault";

	public static final String TARGET_NAMESPACE_FIRMA = "http://www.toomanysecrets.com/firmaServis";
	public static final String TARGET_NAMESPACE_BANKA_NALOG = "http://www.toomanysecrets.com/BankaNalog";
	public static final String TARGET_NAMESPACE_BANKA_IZVOD = "http://www.toomanysecrets.com/BankaIzvod";
	public static final String TARGET_NAMESPACE_BANKA_MT = "http://www.toomanysecrets.com/OdobrenjeMt";
	public static final String TARGET_NAMESPACE_CENTRALNA_BANKA_MT102 = "http://www.toomanysecrets.com/CentralnaClearingNalog";
	public static final String TARGET_NAMESPACE_CENTRALNA_BANKA_MT103 = "http://www.toomanysecrets.com/CentralnaRTGSNalog";

	public static TRola AKTIVNA_ROLA = null;
	public static TKorisnik TRENUTNI_KORISNIK = null;
}
