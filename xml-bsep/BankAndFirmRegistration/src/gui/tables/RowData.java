package gui.tables;


public class RowData {
	private String bankName;
	private String swift;
	
	public RowData(){
		bankName = null;
		swift = null;
	}

	public RowData(String bankName, String swift) {
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
