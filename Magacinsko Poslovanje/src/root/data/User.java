package root.data;

public class User {
	private String uname;
	private String pwd;
	private String role;

	public User(String u, String p, String role) {
		this.uname = u;
		this.pwd = p;
		this.role = role;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
