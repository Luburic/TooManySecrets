package root.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa zadužena za enkapsulaciju IDa i kolona koje trebaju da se prikažu umesto njega.
 * 
 * @author Nikola
 * 
 */
public class MetaSurogateDisplay {
	private String tableCode;
	private Integer id;
	private String idColumnName;
	private List<Object> display = new ArrayList<Object>();
	private List<String> displayColumnName = new ArrayList<String>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public List<Object> getDisplay() {
		return display;
	}

	public void setDisplay(List<Object> display) {
		this.display = display;
	}

	public List<String> getDisplayColumnName() {
		return displayColumnName;
	}

	public void setDisplayColumnName(List<String> displayColumnName) {
		this.displayColumnName = displayColumnName;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}
}
