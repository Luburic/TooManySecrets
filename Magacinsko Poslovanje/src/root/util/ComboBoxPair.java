package root.util;

public class ComboBoxPair {
	private Integer id;
	private Object cmbShow;

	public ComboBoxPair(Integer id, Object cmbShow) {
		this.id = id;
		this.cmbShow = cmbShow;
	}

	@Override
	public String toString() {
		return cmbShow.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Object getCmbShow() {
		return cmbShow;
	}

	public void setCmbShow(Object cmbShow) {
		this.cmbShow = cmbShow;
	}
}
