package root.data;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

public class User {
	private String username;
	private String password;
	private String passwordSalt;
	private String role;

	public User(String u, String p, String role) {
		this.username = u;
		this.password = p;
		this.passwordSalt = "";
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password.equals(this.password)) {
			return;
		}

		if (passwordSalt == null || passwordSalt.equals("")) {
			passwordSalt = RandomStringUtils.randomAscii(20);
		}

		this.password = DigestUtils.sha256Hex(password + passwordSalt);
	}

	public static boolean checkPassword(String password, String passwordSalt, String hash) {
		if (password == null || password.equals(""))
			return false;

		return hash.equals(DigestUtils.sha256Hex(password + passwordSalt));
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String password_salt) {
		this.passwordSalt = password_salt;
	}
}
