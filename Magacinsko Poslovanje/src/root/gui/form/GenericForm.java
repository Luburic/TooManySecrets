package root.gui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import root.dbConnection.DBConnection;
import root.gui.MainFrame;
import root.gui.action.AddAction;
import root.gui.action.CommitAction;
import root.gui.action.DeleteAction;
import root.gui.action.FirstAction;
import root.gui.action.HelpAction;
import root.gui.action.LastAction;
import root.gui.action.NextAction;
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

	protected String childWhere;

	protected JTable tblGrid = new JTable();

	protected int mode;

	protected GenericTableModel tableModel;

	protected Integer parentId = 0;

	public GenericForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		setLayout(new MigLayout("fill"));
		setSize(new Dimension(700, 500));
		setLocationRelativeTo(MainFrame.getInstance());
		setModal(true);
		tblGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblGrid.getTableHeader().setReorderingAllowed(false);
		initToolbar();
		initPanels();
		this.returning = returning;
		this.childWhere = childWhere;
		if (childWhere.contains("=")) {
			parentId = Integer.parseInt(childWhere.substring(childWhere.indexOf("=") + 1, childWhere.length()));
		}
	}

	public void initToolbar() {

		toolBar = new JToolBar();
		btnSearch = new JButton(new SearchAction(this));
		toolBar.add(btnSearch);

		btnRefresh = new JButton(new RefreshAction());
		toolBar.add(btnRefresh);

		initPickup();

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

	protected abstract void initPickup();

	protected JComboBox<ComboBoxPair> setupJoinsWithComboBox(JComboBox<ComboBoxPair> cmbForJoin, String tableCode,
			String pkCode, String pkName, String displayCode, String displayName, boolean renamed, String whereClause) {
		MetaSurogateDisplay temp = new MetaSurogateDisplay();
		temp.setTableCode(tableCode);
		temp.setIdColumnName(pkCode);
		temp.getDisplayColumnCode().add(displayCode);
		temp.getDisplayColumnName().add(displayName);
		joinColumn.add(temp);

		try {
			if (renamed) {
				cmbForJoin = new JComboBox<ComboBoxPair>(Lookup.getComboBoxEntity(tableCode, pkCode.substring(4),
						displayCode, whereClause));
			} else {
				cmbForJoin = new JComboBox<ComboBoxPair>(Lookup.getComboBoxEntity(tableCode, pkCode, displayCode,
						whereClause));
			}
			cmbForJoin.setName(pkName);
			return cmbForJoin;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void setupJoins(String tableCode, String pkCode, String displayCode, String displayName) {
		MetaSurogateDisplay temp = new MetaSurogateDisplay();
		temp.setTableCode(tableCode);
		temp.setIdColumnName(pkCode);
		temp.getDisplayColumnCode().add(displayCode);
		temp.getDisplayColumnName().add(displayName);
		joinColumn.add(temp);
	}

	protected void setupTable(String customQuery) {
		if (tableModel.getWhereStmt().equals("")) {
			tableModel.setWhereStmt(childWhere);
		}
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
			for (int i = n; i > 0; i--) {
				tblGrid.removeColumn(tblGrid.getColumnModel().getColumn(i));
			}
		}
		tblGrid.removeColumn(tblGrid.getColumnModel().getColumn(0));
		try {
			if (customQuery == null) {
				tableModel.open();
			} else {
				tableModel.fillData(customQuery);
			}
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
			int index = 0;

			LinkedList<Object> newRow = new LinkedList<Object>();

			getDataAndAddToRow(newRow);

			index = tableModel.insertRow(newRow.toArray());
			tblGrid.setRowSelectionInterval(index, index);
			setMode(Constants.MODE_ADD);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateRow() {

		try {
			LinkedList<Object> newRow = new LinkedList<Object>();

			getDataAndAddToRow(newRow);

			int i = tblGrid.getSelectedRow();

			tableModel.updateRow(newRow.toArray(), i);

			tblGrid.getSelectionModel().setSelectionInterval(i, i);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void getDataAndAddToRow(LinkedList<Object> newRow) {
		int relatedTabelCount = 0;
		for (Component cp : dataPanel.getComponents()) {
			if (cp instanceof JComboBox<?> && cp.getName() != null) {
				@SuppressWarnings("unchecked")
				JComboBox<ComboBoxPair> comboBox = (JComboBox<ComboBoxPair>) cp;
				ComboBoxPair selected = (ComboBoxPair) comboBox.getSelectedItem();
				tableModel.getOutsideColumns().get(relatedTabelCount).getDisplayColumnValue()
						.set(0, selected.toString());
				newRow.add(selected.getId());
				relatedTabelCount++;
			}
		}
		for (Component cp : dataPanel.getComponents()) {
			if (cp.getName() != null) {
				if (cp instanceof JTextField) {
					JTextField textField = (JTextField) cp;
					newRow.add(textField.getText().trim());
				} else if (cp instanceof JCheckBox) {
					JCheckBox checkBox = (JCheckBox) cp;
					if (checkBox.isSelected()) {
						newRow.add("1");
					} else {
						newRow.add("0");
					}
				} else if (cp instanceof JDatePickerImpl) {
					JDatePickerImpl datePicker = (JDatePickerImpl) cp;
					Date date = (Date) datePicker.getJDateInstantPanel().getModel().getValue();
					String dateString = "";
					if (date != null) {
						DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						dateString = formatter.format(date);
					}
					newRow.add(dateString);
				}
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

	public void setMode(int mode) {
		btnCommit.setEnabled(true);
		this.mode = mode;

		if (mode == Constants.MODE_ADD || mode == Constants.MODE_SEARCH) {
			clearFields(true);
		}
	}

	protected void clearFields(boolean needFocus) {
		for (Component cp : dataPanel.getComponents()) {
			if (cp instanceof JTextField) {
				JTextField textField = (JTextField) cp;
				textField.setText("");
				if (needFocus) {
					textField.requestFocus();
					needFocus = false;
				}
			} else if (cp instanceof JCheckBox) {
				JCheckBox checkBox = (JCheckBox) cp;
				checkBox.setSelected(false);
			} else if (cp instanceof JDatePickerImpl) {
				JDatePickerImpl datePicker = (JDatePickerImpl) cp;
				datePicker
						.getJDateInstantPanel()
						.getModel()
						.setDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
								Calendar.getInstance().get(Calendar.DATE));
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
				if (cp.getName() != null && cp.getName().equals(cname)) {
					if (cp instanceof JTextField) {
						JTextField textField = (JTextField) cp;
						if (value != null) {
							textField.setText(value.toString());
						} else {
							textField.setText("");
						}
					} else if (cp instanceof JComboBox<?>) {
						@SuppressWarnings("unchecked")
						JComboBox<ComboBoxPair> cmb = (JComboBox<ComboBoxPair>) cp;
						for (int i = 0; i < cmb.getModel().getSize(); i++) {
							if (cmb.getItemAt(i).getId() == value) {
								cmb.setSelectedIndex(i);
								break;
							}
						}
					} else if (cp instanceof JCheckBox) {
						JCheckBox checkBox = (JCheckBox) cp;
						if (value.toString().equals("Da")) {
							checkBox.setSelected(true);
						} else {
							checkBox.setSelected(false);
						}
					} else if (cp instanceof JDatePickerImpl) {
						JDatePickerImpl datePicker = (JDatePickerImpl) cp;
						String date = value.toString();
						if (date.contains("-")) {
							String[] dates = date.split("\\-");
							datePicker.getModel().setDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1,
									Integer.parseInt(dates[2]));
						}
					}
				}
			}
		}

	}

	public void prepareDialogForZoom(GenericForm dialog, Integer id) {
		dialog.getBtnPickup().setEnabled(true);
		int n = dialog.getTblGrid().getModel().getRowCount();
		for (int i = 0; i < n; i++) {
			if (id == dialog.getTblGrid().getModel().getValueAt(i, 0)) {
				dialog.getTblGrid().getSelectionModel().setSelectionInterval(i, i);
				break;
			}
		}
		dialog.setVisible(true);
	}

	public abstract boolean verification();

	public abstract boolean allowDeletion();

	protected boolean allowDeletion(String... tabele) {
		Integer id = (Integer) tableModel.getValueAt(tblGrid.getSelectedRow(), 0);

		try {
			PreparedStatement statement = null;
			for (String tabela : tabele) {

				if (tableModel.getTableCode().equals(tabela)) {
					statement = DBConnection.getConnection().prepareStatement(
							"SELECT " + tableModel.getPrimaryKey() + " FROM " + tabela + " WHERE "
									+ tabela.substring(0, 3) + "_" + tableModel.getPrimaryKey() + " = " + id);
				} else {
					statement = DBConnection.getConnection().prepareStatement(
							"SELECT " + tableModel.getPrimaryKey() + " FROM " + tabela + " WHERE "
									+ tableModel.getPrimaryKey() + " = " + id);
				}
				ResultSet rset = statement.executeQuery();
				if (rset.next()) {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
