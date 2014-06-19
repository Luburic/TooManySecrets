package gui.tables;

public class RowDataFirms {
	
	private String firmName;
	private String pib;
	
	public RowDataFirms(){
		firmName = null;
		pib = null;
	}

	public RowDataFirms(String firmName, String pib) {
		super();
		this.firmName = firmName;
		this.pib = pib;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getPib() {
		return pib;
	}

	public void setPib(String pib) {
		this.pib = pib;
	}
	

}
