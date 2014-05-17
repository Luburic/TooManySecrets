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
	private List<String> displayColumnValue = new ArrayList<String>();
	private List<String> displayColumnName = new ArrayList<String>();
	private List<String> displayColumnCode = new ArrayList<String>();

	public MetaSurogateDisplay() {
		displayColumnValue.add("");
	}

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

	public List<String> getDisplayColumnCode() {
		return displayColumnCode;
	}

	public void setDisplayColumnCode(List<String> displayColumnCode) {
		this.displayColumnCode = displayColumnCode;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public List<String> getDisplayColumnName() {
		return displayColumnName;
	}

	public void setDisplayColumnName(List<String> displayColumnName) {
		this.displayColumnName = displayColumnName;
	}

	public List<String> getDisplayColumnValue() {
		return displayColumnValue;
	}

	public void setDisplayColumnValue(List<String> displayColumnValue) {
		this.displayColumnValue = displayColumnValue;
	}
}
