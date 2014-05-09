package root.gui.action;

import java.awt.event.ActionEvent;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import root.dbConnection.DBConnection;

public class PopupAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JDialog standardForm;

	private DrzavaStandardForm drzavaForm;
	private JTable tblGrid = new JTable();
	private DrzaveTableModel tableModel;

	public PopupAction(JDialog standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/nextform.gif"));
		putValue(SHORT_DESCRIPTION, "Sledeća forma");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (standardForm instanceof DrzavaStandardForm) {

			drzavaForm = (DrzavaStandardForm) standardForm;
			tblGrid = drzavaForm.getTblGrid();
			tableModel = (DrzaveTableModel) tblGrid.getModel();
			int index = tblGrid.getSelectedRow();

			// ako nije nista selektovano
			if (index < 0)
				return;

			DatabaseMetaData metaData = null;
			ResultSet tSet = null;
			try {
				metaData = DBConnection.getConnection().getMetaData();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				tSet = metaData.getTables(null, null, null, new String[] { "TABLE" });
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LinkedList<String> possibleValues = new LinkedList<String>();
			possibleValues.add("Naseljena mesta");
			possibleValues.add("Sektori");
			/*
			 * try { while (tSet.next()){ possibleValues.add(tSet.getString("TABLE_NAME")); } } catch (SQLException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); }
			 */

			Object[] values = possibleValues.toArray();
			String selectedValue = (String) JOptionPane.showInputDialog(standardForm, "Izaberi sledeći dijalog",
					"Izbor dijaloga", JOptionPane.INFORMATION_MESSAGE, null, values, values[0]);

			if (selectedValue != null) {
				drzavaForm.setSelectedForm(selectedValue);
				new NextFormAction(drzavaForm).actionPerformed(null);

			}

		}

	}
}
