//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.24 at 04:50:14 PM CEST 
//

package util.accessControl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;


/**
 * <p>
 * Java class for t_korisnik complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_korisnik">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="passwordSalt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rola" type="{http://www.toomanysecrets.com/role}t_rola"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_korisnik", namespace = "http://www.toomanysecrets.com/korisnici", propOrder = { "username",
		"password", "passwordSalt", "rola" })
public class TKorisnik {

	@XmlElement(required = true)
	protected String username;
	@XmlElement(required = true)
	protected String password;
	@XmlElement(required = true)
	protected String passwordSalt;
	@XmlElement(required = true)
	protected TRola rola;

	/**
	 * Gets the value of the username property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the value of the username property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUsername(String value) {
		this.username = value;
	}

	/**
	 * Gets the value of the password property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the value of the password property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPassword(String value) {
		this.password = value;
	}

	/**
	 * Gets the value of the passwordSalt property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPasswordSalt() {
		return passwordSalt;
	}

	/**
	 * Sets the value of the passwordSalt property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPasswordSalt(String value) {
		this.passwordSalt = value;
	}

	/**
	 * Gets the value of the rola property.
	 * 
	 * @return possible object is {@link TRola }
	 * 
	 */
	public TRola getRola() {
		return rola;
	}

	/**
	 * Sets the value of the rola property.
	 * 
	 * @param value
	 *            allowed object is {@link TRola }
	 * 
	 */
	public void setRola(TRola value) {
		this.rola = value;
	}

	public static boolean checkPassword(String password, String passwordSalt, String hash) {
		if (password == null || password.equals(""))
			return false;

		return hash.equals(DigestUtils.sha256Hex(password + passwordSalt));
	}

	public void generatePassword(String plainPassword) {
		this.passwordSalt = RandomStringUtils.randomAscii(20);
		this.password = DigestUtils.sha256Hex(plainPassword + passwordSalt);
	}

}
