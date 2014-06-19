package gui.tables;


public class RowDataBanks {
	private String bankName;
	private String swift;
	
	public RowDataBanks(){
		bankName = null;
		swift = null;
	}

	public RowDataBanks(String bankName, String swift) {
		super();
		this.bankName = bankName;
		this.swift = swift;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSwift() {
		return swift;
	}

	public void setSwift(String swift) {
		this.swift = swift;
	}
	
	
}
