package root.gui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
import root.gui.action.PreviousAction;
import root.gui.action.RefreshAction;
import root.gui.action.RollbackAction;
import root.gui.action.SearchAction;
import root.gui.tablemodel.GenericTableModel;
import root.util.Column;
import root.util.ColumnList;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.Lookup;
import root.util.MetaSurogateDisplay;

@SuppressWarnings("serial")
public abstract class GenericForm extends JDialog {

	protected JToolBar toolBar;

	protected JButton btnAdd, btnCommit, btnDelete, btnFirst, btnLast, btnHelp, btnNext, btnNextForm, btnPickup,
			btnRefresh, btnRollback, btnSearch, btnPrevious;

	protected JPanel dataPanel;

	protected JComboBox<ComboBoxPair> returning;

	public JComboBox<ComboBoxPair> getReturning() {
		return returning;
	}

	protected List<MetaSurogateDisplay> joinColumn = new ArrayList<MetaSurogateDisplay>();

	private String childWhere;

	protected JTable tblGrid = new JTable();

	protected int mode;

	protected GenericTableModel tableModel;

	public GenericForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		setLayout(new MigLayout("fill"));
		setSize(new Dimension(600, 400));
		setLocationRelativeTo(MainFrame.getInstance());
		setModal(true);
		initToolbar();
		initPanels();
		this.returning = returning;
		this.childWhere = childWhere;
	}

	public void initToolbar() {

		toolBar = new JToolBar();
		btnSearch = new JButton(new SearchAction(this));
		toolBar.add(btnSearch);

		btnRefresh = new JButton(new RefreshAction());
		toolBar.add(btnRefresh);

		btnPickup = new JButton(new PickupAction(this));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);

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

		add(toolBar, "dock north");
	}

	protected JComboBox<ComboBoxPair> setupJoins(JComboBox<ComboBoxPair> cmbForJoin, String tableCode, String pkCode,
			String pkName, String displayCode, String displayName) {
		MetaSurogateDisplay temp = new MetaSurogateDisplay();
		temp.setTableCode(tableCode);
		temp.setIdColumnName(pkCode);
		temp.getDisplayColumnCode().add(displayCode);
		temp.getDisplayColumnName().add(displayName);
		joinColumn.add(temp);

		try {
			cmbForJoin = new JComboBox<ComboBoxPair>(Lookup.getComboBoxEntity(tableCode, pkCode, displayCode));
			cmbForJoin.setName(pkName);
			return cmbForJoin;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void setupTable() {
		tableModel.setWhereStmt(childWhere);
		tblGrid.setModel(tableModel);
		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				sync();
				setMode(Constants.MODE_EDIT);
			}
		});
		tblGrid.removeColumn(tblGrid.getColumnModel().getColumn(tblGrid.getColumnCount() - 1));
		if (tableModel.getOutsideColumns() != null) {
			int n = tableModel.getOutsideColumns().size();
			for (int i = 1; i <= n; i++) {
				tblGrid.removeColumn(tblGrid.getColumnModel().getColumn(i));
			}
		}
		tblGrid.removeColumn(tblGrid.getColumnModel().getColumn(0));
		try {
			tableModel.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (tblGrid.getRowCount() > 0) {
			tblGrid.getSelectionModel().setSelectionInterval(0, 0);
		} else {
			setMode(Constants.MODE_ADD);
		}
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
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateRow() {

		try {
			GenericTableModel dtm = (GenericTableModel) tblGrid.getModel();

			LinkedList<Object> newRow = new LinkedList<Object>();

			getDataAndAddToRow(newRow);

			dtm.updateRow(newRow.toArray(), tblGrid.getSelectedRow());
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void getDataAndAddToRow(LinkedList<Object> newRow) {
		int relatedTabelCount = 0;
		for (Component cp : dataPanel.getComponents()) {
			if (cp instanceof JComboBox<?>) {
				@SuppressWarnings("unchecked")
				JComboBox<ComboBoxPair> comboBox = (JComboBox<ComboBoxPair>) cp;
				ComboBoxPair selected = (ComboBoxPair) comboBox.getSelectedItem();
				tableModel.getOutsideColumns().get(relatedTabelCount).getDisplayColumnValue().add(selected.toString());
				newRow.add(selected.getId());
				relatedTabelCount++;
			}
		}
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
		if (index == tblGrid.getModel().getRowCount() - 1)
			newIndex--;

		try {

			GenericTableModel dtm = (GenericTableModel) tblGrid.getModel();
			dtm.deleteRow(index);

			if (tblGrid.getModel().getRowCount() > 0)
				tblGrid.setRowSelectionInterval(newIndex, newIndex);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
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
			Object value = null;
			if (c.getValue() != null) {
				value = c.getValue();
			}

			for (Component cp : dataPanel.getComponents()) {
				if (cp instanceof JTextField) {
					JTextField textField = (JTextField) cp;
					if (textField.getName() != null && textField.getName().equals(cname)) {
						if (value != null) {
							textField.setText(value.toString());
						} else {
							textField.setText("");
						}
					}
				} else if (cp instanceof JComboBox<?>) {
					@SuppressWarnings("unchecked")
					JComboBox<ComboBoxPair> cmb = (JComboBox<ComboBoxPair>) cp;
					if (cmb.getName() != null && cmb.getName().equals(cname)) {
						for (int i = 0; i < cmb.getModel().getSize(); i++) {
							if (cmb.getItemAt(i).getId() == value) {
								cmb.setSelectedIndex(i);
								break;
							}
						}
					}
				}
			}
		}

	}
}
