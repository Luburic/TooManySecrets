package util.accessControl;

public class GenerateHashPasswords {

	public static void main(String[] args) {
		TKorisnik korisnik = new TKorisnik();
		korisnik.generatePassword("direktor");
		System.out.println(korisnik.getPassword());
		System.out.println(korisnik.getPasswordSalt());
		System.out.println();
		korisnik.generatePassword("menadzer");
		System.out.println(korisnik.getPassword());
		System.out.println(korisnik.getPasswordSalt());
		System.out.println();
		korisnik.generatePassword("korisnik");
		System.out.println(korisnik.getPassword());
		System.out.println(korisnik.getPasswordSalt());
		System.out.println();
		korisnik.generatePassword("korisnikNalog");
		System.out.println(korisnik.getPassword());
		System.out.println(korisnik.getPasswordSalt());
	}

}
