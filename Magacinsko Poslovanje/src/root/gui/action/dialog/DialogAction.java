package root.gui.action.dialog;

import javax.swing.AbstractAction;

public abstract class DialogAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private String whereClause = "";

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

}
