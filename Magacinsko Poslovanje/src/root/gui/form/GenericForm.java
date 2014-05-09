package root.gui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import root.gui.MainFrame;
import root.gui.action.AddAction;
import root.gui.action.CommitAction;
import root.gui.action.DeleteAction;
import root.gui.action.FirstAction;
import root.gui.action.HelpAction;
import root.gui.action.LastAction;
import root.gui.action.NextAction;
import root.gui.action.PickupAction;
import root.gui.action.PopupAction;
import root.gui.action.PreviousAction;
import root.gui.action.RefreshAction;
import root.gui.action.RollbackAction;
import root.gui.action.SearchAction;
import root.gui.tablemodel.GenericTableModel;
import root.util.Column;
import root.util.ColumnList;
import root.util.Constants;

@SuppressWarnings("serial")
public abstract class GenericForm extends JDialog {

	protected JToolBar toolBar;

	protected JButton btnAdd, btnCommit, btnDelete, btnFirst, btnLast, btnHelp, btnNext, btnNextForm, btnPickup,
			btnRefresh, btnRollback, btnSearch, btnPrevious;

	protected JPanel dataPanel;

	// ime forme koja je selektovana next mehanizmom(popupAction->nextFormAction)
	protected String selectedForm;

	// da li se desilo otvaranje child forme
	protected boolean childAction = false;

	protected JTable tblGrid = new JTable();

	protected DefaultTableModel tableModel = new DefaultTableModel();

	protected int mode;

	public GenericForm() {
		setLayout(new MigLayout("fill"));
		setSize(new Dimension(600, 400));
		setLocationRelativeTo(MainFrame.getInstance());
		setModal(true);
		initToolbar();
		initPanels();
	}

	public void initToolbar() {

		toolBar = new JToolBar();
		btnSearch = new JButton(new SearchAction(this));
		toolBar.add(btnSearch);

		btnRefresh = new JButton(new RefreshAction());
		toolBar.add(btnRefresh);

		btnPickup = new JButton(new PickupAction(this));
		toolBar.add(btnPickup);

		btnHelp = new JButton(new HelpAction());
		toolBar.add(btnHelp);

		toolBar.addSeparator();

		btnFirst = new JButton(new FirstAction(this));
		toolBar.add(btnFirst);

		btnPrevious = new JButton(new PreviousAction(this));
		toolBar.add(btnPrevious);

		btnNext = new JButton(new NextAction(this));
		toolBar.add(btnNext);

		btnLast = new JButton(new LastAction(this));
		toolBar.add(btnLast);

		toolBar.addSeparator();

		btnAdd = new JButton(new AddAction(this));
		toolBar.add(btnAdd);

		btnDelete = new JButton(new DeleteAction(this));
		toolBar.add(btnDelete);

		toolBar.addSeparator();

		btnNextForm = new JButton(new PopupAction(this));
		toolBar.add(btnNextForm);

		add(toolBar, "dock north");

	}

	public void initPanels() {

		JScrollPane scrollPane = new JScrollPane(tblGrid);
		add(scrollPane, "grow, wrap");

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new MigLayout("fillx"));

		dataPanel = new JPanel();
		dataPanel.setLayout(new MigLayout("gapx 15px"));

		JPanel buttonsPanel = new JPanel();

		btnCommit = new JButton(new CommitAction(this));
		btnRollback = new JButton(new RollbackAction(this));

		bottomPanel.add(dataPanel);

		buttonsPanel.setLayout(new MigLayout("wrap"));
		buttonsPanel.add(btnCommit);
		buttonsPanel.add(btnRollback);
		bottomPanel.add(buttonsPanel, "dock east");

		add(bottomPanel, "grow, wrap");

	}

	public void goLast() {
		int rowCount = tblGrid.getModel().getRowCount();
		if (rowCount > 0)
			tblGrid.setRowSelectionInterval(rowCount - 1, rowCount - 1);
		sync();
	}

	public void goFirst() {
		int rowCount = tblGrid.getModel().getRowCount();
		if (rowCount > 0)
			tblGrid.setRowSelectionInterval(0, 0);
		sync();
	}

	public void goNext() {
		int rowCount = tblGrid.getModel().getRowCount();
		if (rowCount > 0) {
			int index = tblGrid.getSelectedRow();
			if (index + 1 == rowCount)
				tblGrid.setRowSelectionInterval(0, 0);
			else
				tblGrid.setRowSelectionInterval(index + 1, index + 1);

			sync();
		}

	}

	public void goPrevious() {
		int rowCount = tblGrid.getModel().getRowCount();
		if (rowCount > 0) {
			int index = tblGrid.getSelectedRow();
			if (index == 0)
				tblGrid.setRowSelectionInterval(rowCount - 1, rowCount - 1);
			else
				tblGrid.setRowSelectionInterval(index - 1, index - 1);

			sync();
		}

	}

	public void addRow() {

		try {
			GenericTableModel dtm = (GenericTableModel) tblGrid.getModel();
			int index = 0;

			LinkedList<Object> newRow = new LinkedList<Object>();

			getDataAndAddToRow(newRow);

			index = dtm.insertRow(newRow.toArray());
			tblGrid.setRowSelectionInterval(index, index);
			setMode(Constants.MODE_ADD);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void getDataAndAddToRow(LinkedList<Object> newRow) {
		for (Component cp : dataPanel.getComponents()) {
			if (cp instanceof JTextField) {
				JTextField textField = (JTextField) cp;
				newRow.add(textField.getText().trim());
			}
		}
	}

	public void removeRow() {

		int index = tblGrid.getSelectedRow();

		// ako nista nije selektovano
		if (index == -1)
			return;

		// kada obrisemo tekuci red, selektovacemo sledeci (newindex):
		int newIndex = index;

		// sem ako se obrise poslednji red, tada selektujemo prethodni
		if (index == tableModel.getRowCount() - 1)
			newIndex--;

		try {

			GenericTableModel dtm = (GenericTableModel) tblGrid.getModel();
			((GenericTableModel) dtm).deleteRow(index);

			if (tableModel.getRowCount() > 0)
				tblGrid.setRowSelectionInterval(newIndex, newIndex);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getMode() {
		return mode;
	}

	public JButton getBtnPickup() {
		return btnPickup;
	}

	public void setBtnPickup(JButton btnPickup) {
		this.btnPickup = btnPickup;
	}

	public JTable getTblGrid() {
		return tblGrid;
	}

	public void setTblGrid(JTable tblGrid) {
		this.tblGrid = tblGrid;
	}

	public String getSelectedForm() {
		return selectedForm;
	}

	public void setSelectedForm(String selectedForm) {
		this.selectedForm = selectedForm;
	}

	// svaki naslednik setuje mod na svoj nacin ?
	public void setMode(int mode) {
		this.mode = mode;

		switch (mode) {
		case Constants.MODE_ADD:
			clearFields(true);
		}
	}

	private void clearFields(boolean needFocus) {
		for (Component cp : dataPanel.getComponents()) {
			if (cp instanceof JTextField) {
				JTextField textField = (JTextField) cp;
				textField.setText("");
				if (needFocus) {
					textField.requestFocus();
					needFocus = false;
				}
			}
		}
	}

	public void sync() {
		int index = tblGrid.getSelectedRow();
		GenericTableModel dtm = (GenericTableModel) tblGrid.getModel();

		if (index < 0) {
			clearFields(false);
			return;
		}

		int columnCount = dtm.getColumnCount();
		ColumnList columns = new ColumnList();

		for (int i = 0; i < columnCount; i++) {
			columns.add(new Column(dtm.getColumnName(i), dtm.getValueAt(index, i)));
		}

		Iterator<Column> iter = columns.iterator();

		while (iter.hasNext()) {
			Column c = iter.next();
			String cname = c.getName();
			String value = (String) c.getValue();

			for (Component cp : dataPanel.getComponents()) {
				if (cp instanceof JTextField) {
					JTextField textField = (JTextField) cp;
					if (textField.getName().equals(cname)) {
						textField.setText(value);
					}
				}
			}
		}

	}
}
