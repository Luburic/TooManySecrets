package root.util;

import java.text.Collator;
import java.text.RuleBasedCollator;

/**
 * Contains various utilities for sorting.
 * 
 * @author Branko Milosavljevic, mbranko@uns.ns.ac.yu
 * @version 1.0
 */
public class SortUtils {
	/** Returns a cyrillic/latin collator with cyrillic alphabet ordering. */
	public static Collator getCyrLatCollator() {
		return cyrLatCollator;
	}

	/** Returns a cyrillic/latin collator with latin alphabet ordering. */
	public static Collator getLatCyrCollator() {
		return latCyrCollator;
	}

	private static Collator latCyrCollator;
	private static Collator cyrLatCollator;

	static {
		String cyrLatRules = "< a      =A      =\u0430 =\u0410 " + // a
				"< b      =B      =\u0431 =\u0411 " + // b
				"< v      =V      =\u0432 =\u0412 " + // v
				"< g      =G      =\u0433 =\u0413 " + // g
				"< d      =D      =\u0434 =\u0414 " + // d
				"< \u0111 =\u0110 =\u0452 =\u0402 " + // dj
				"< e      =E      =\u0435 =\u0415 " + // e
				"< \u017e =\u017d =\u0436 =\u0416 " + // zz
				"< z      =Z      =\u0437 =\u0417 " + // z
				"< i      =i      =\u0438 =\u0418 " + // i
				"< j      =J      =\u0458 =\u0408 " + // j
				"< k      =K      =\u043a =\u041a " + // k
				"< l      =L      =\u041b =\u041b " + // l
				"< lj     =Lj     =\u0459 =\u0409 " + // lj
				"< m      =M      =\u043c =\u041c " + // m
				"< n      =N      =\u043d =\u041d " + // n
				"< nj     =Nj     =\u045a =\u040a " + // nj
				"< o      =O      =\u043e =\u041e " + // o
				"< p      =P      =\u043f =\u041f " + // p
				"< q      =Q " + "< r      =R      =\u0440 =\u0420 " + // r
				"< s      =S      =\u0441 =\u0421 " + // s
				"< t      =T      =\u0442 =\u0422 " + // t
				"< \u0107 =\u0106 =\u045b =\u040b " + // cc
				"< u      =U      =\u0443 =\u0423 " + // u
				"< f      =F      =\u0444 =\u0424 " + // f
				"< h      =H      =\u0445 =\u0425 " + // h
				"< c      =C      =\u0446 =\u0426 " + // c
				"< \u010d =\u010c =\u0447 =\u0427 " + // ch
				"< d�    =D     =\u045f =\u040f " + // dz
				"< \u0161 =\u0160 =\u0448 =\u0428 " + // ss
				"< w      =W " + "< x      =X " + "< y      =Y ";

		String latCyrRules = "< a      =A      =\u0430 =\u0410 " + // a
				"< b      =B      =\u0431 =\u0411 " + // b
				"< c      =C      =\u0446 =\u0426 " + // c
				"< \u010d =\u010c =\u0447 =\u0427 " + // ch
				"< \u0107 =\u0106 =\u045b =\u040b " + // tj
				"< d      =D      =\u0434 =\u0414 " + // d
				"< d�     =D�     =\u045f =\u040f " + // dz
				"< \u0111 =\u0110 =\u0452 =\u0402 " + // dj
				"< e      =E      =\u0435 =\u0415 " + // e
				"< f      =F      =\u0444 =\u0424 " + // f
				"< g      =G      =\u0433 =\u0413 " + // g
				"< h      =H      =\u0445 =\u0425 " + // h
				"< i      =i      =\u0438 =\u0418 " + // i
				"< j      =J      =\u0458 =\u0408 " + // j
				"< k      =K      =\u043a =\u041a " + // k
				"< l      =L      =\u041b =\u041b " + // l
				"< lj     =Lj     =\u0459 =\u0409 " + // lj
				"< m      =M      =\u043c =\u041c " + // m
				"< n      =N      =\u043d =\u041d " + // n
				"< nj     =Nj     =\u045a =\u040a " + // nj
				"< o      =O      =\u043e =\u041e " + // o
				"< p      =P      =\u043f =\u041f " + // p
				"< q      =Q " + "< r      =R      =\u0440 =\u0420 " + // r
				"< s      =S      =\u0441 =\u0421 " + // s
				"< \u0161 =\u0160 =\u0448 =\u0428 " + // ss
				"< t      =T      =\u0442 =\u0422 " + // t
				"< u      =U      =\u0443 =\u0423 " + // u
				"< v      =V      =\u0432 =\u0412 " + // v
				"< w      =W " + "< x      =X " + "< y      =Y " + "< z      =Z      =\u0437 =\u0417 " + // z
				"< \u017e =\u017d =\u0436 =\u0416 "; // zz

		try {

			cyrLatCollator = new RuleBasedCollator(cyrLatRules);
			latCyrCollator = new RuleBasedCollator(latCyrRules);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}