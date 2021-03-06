package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import root.dbConnection.DBConnection;
import root.gui.action.IzvestajLagerListaAction;
import root.gui.action.NextFormButton;
import root.gui.action.NivelacijaSvegaAction;
import root.gui.action.NovoStanjeAction;
import root.gui.action.PickupAction;
import root.gui.action.dialog.MagacinskaKarticaAction;
import root.gui.action.dialog.PopisniDokumentAction;
import root.gui.action.dialog.PrometniDokumentAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;

public class OrganizacionaJedinicaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPreduzece = new JButton("...");
	private JButton btnZoomOrgJedinica = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JTextField tfNaziv = new JTextField(20);
	protected JCheckBox chkMagacin = new JCheckBox();
	private final JLabel lblGreska1 = new JLabel();
	private final JLabel lblGreska2 = new JLabel();

	private JButton btnLagerLista = new JButton(new IzvestajLagerListaAction(this));
	private JButton btnNovoStanje = new JButton(new NovoStanjeAction(this));
	private JButton btnNivelacija = new JButton(new NivelacijaSvegaAction(this));

	public OrganizacionaJedinicaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Organizacione jedinice");
		btnLagerLista.setEnabled(false);
		btnNovoStanje.setEnabled(false);
		btnNivelacija.setEnabled(false);
		JLabel lblNaziv = new JLabel("Naziv jedinice*:");
		JLabel lblMagacin = new JLabel("Magacin:");
		JLabel lblOrgJedinica = new JLabel("Nadsektor:");
		JLabel lblPreduzece = new JLabel("Preduzeće*:");
		tfNaziv.setName("naziv jedinice");
		chkMagacin.setName("magacin");
		tfNaziv.setDocument(new JTextFieldLimit(20));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);

		cmbOrgJedinica = super.setupJoinsWithComboBox(cmbOrgJedinica, "Organizaciona_jedinica", "Org_id_jedinice",
				"Org_id jedinice", "naziv_jedinice", "naziv nadsektora", true, " WHERE magacin = 0");
		cmbOrgJedinica.insertItemAt(new ComboBoxPair(0, ""), 0);
		cmbOrgJedinica.setSelectedIndex(0);
		if (Constants.idPreduzeca == 0) {
			cmbPreduzece = super.setupJoinsWithComboBox(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća",
					"naziv_preduzeca", "naziv preduzeća", false, "");
		} else {
			super.setupJoins("Preduzece", "id_preduzeca", "naziv_preduzeca", "naziv preduzeća");
			cmbPreduzece = new JComboBox<ComboBoxPair>();
			cmbPreduzece.insertItemAt(new ComboBoxPair(Constants.idPreduzeca, Constants.nazivPreduzeca), 0);
			cmbPreduzece.setSelectedIndex(0);
			cmbPreduzece.setEnabled(false);
			cmbPreduzece.setName("id preduzeća");
			btnZoomPreduzece.setVisible(false);
		}
		if (!childWhere.contains("id_preduzeca")) {
			btnZoomPreduzece.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPreduzece.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPreduzece.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PreduzeceStandardForm(cmbPreduzece, ""), id);
				}
			});
		} else {
			cmbPreduzece.setEnabled(false);
			btnZoomPreduzece.setVisible(false);
		}
		if (!childWhere.contains("id_jedinice")) {
			btnZoomOrgJedinica.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbOrgJedinica.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbOrgJedinica.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new OrganizacionaJedinicaStandardForm(cmbOrgJedinica, ""), id);
				}
			});
		} else {
			cmbOrgJedinica.setEnabled(false);
			for (int i = 0; i < cmbOrgJedinica.getItemCount(); i++) {
				if (cmbOrgJedinica.getItemAt(i).getId().equals(parentId)) {
					cmbOrgJedinica.setSelectedIndex(i);
					break;
				}
			}
			btnZoomOrgJedinica.setVisible(false);
		}

		tfNaziv.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});
		cmbPreduzece.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbPreduzece.getSelectedIndex() != -1) {
					lblGreska2.setText("");
				}
			}
		});

		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = tblGrid.getSelectedRow();
				if (i != -1) {
					String s = (String) tableModel.getValueAt(i, 4);
					if (s.trim().equals("Da")) {
						btnLagerLista.setEnabled(true);
						btnNovoStanje.setEnabled(true);
						btnNivelacija.setEnabled(true);
						if (OrganizacionaJedinicaStandardForm.this.returning != null) {
							btnPickup.setEnabled(true);
						}
					} else {
						btnLagerLista.setEnabled(false);
						btnNovoStanje.setEnabled(false);
						btnNivelacija.setEnabled(false);
						btnPickup.setEnabled(false);
					}
				}
			}
		});

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNaziv, "span 2");
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(chkMagacin, "wrap");

		dataPanel.add(lblOrgJedinica);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica, "wrap, gapx 15px");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece);
		dataPanel.add(btnZoomPreduzece);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		toolBar.add(btnNovoStanje);
		toolBar.addSeparator();
		toolBar.add(btnNivelacija);
		toolBar.addSeparator();
		toolBar.add(btnLagerLista);
		toolBar.addSeparator();
		JPopupMenu popup = new JPopupMenu();
		popup.add(new MagacinskaKarticaAction());
		popup.add(new PopisniDokumentAction());
		popup.add(new PrometniDokumentAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Organizaciona jedinica", joinColumn);
		tableModel.setColumnForSorting(3);
		if (Constants.idPreduzeca != 0) {
			if (childWhere.equals("")) {
				tableModel.setWhereStmt(" WHERE Organizaciona_jedinica1.id_preduzeca = " + Constants.idPreduzeca);
			} else {
				tableModel.setWhereStmt(childWhere + " AND Organizaciona_jedinica1.id_preduzeca = "
						+ Constants.idPreduzeca);
			}
		}
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfNaziv.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNaziv.requestFocus();
			return false;
		}
		if (cmbPreduzece.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPreduzece.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Magacinska_kartica", "Organizaciona_jedinica", "Prometni_dokument", "Popisni_dokument");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}

	@Override
	protected void clearFields(boolean needFocus) {
		super.clearFields(needFocus);
		cmbOrgJedinica.setSelectedIndex(0);
	}

	public void lagerLista() {
		try {
			Map<String, Object> params = new HashMap<String, Object>(4);
			params.put("preduzece", Constants.nazivPreduzeca);
			params.put("id_jedinice", tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			params.put("id_godine", Constants.idGodine);
			params.put("naziv_jedinice", tableModel.getValueAt(tblGrid.getSelectedRow(), 3));
			JasperPrint jp = JasperFillManager.fillReport(getClass().getResource("/root/izvestaj/LagerLista.jasper")
					.openStream(), params, DBConnection.getConnection());
			JasperViewer.viewReport(jp, false);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void otvoriNovoStanje() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String now = dateFormatter.format(Calendar.getInstance().getTime());

		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall("{ call OtvaranjePocetnog(?, ?, ?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, Constants.idGodine);
			proc.setObject(3, Constants.idPreduzeca);
			proc.setObject(4, now);
			proc.executeUpdate();
			DBConnection.getConnection().commit();
			proc.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void nivelacija() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String now = dateFormatter.format(Calendar.getInstance().getTime());
		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall("{ call NivelacijaSvega(?, ?, ?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, Constants.idGodine);
			proc.setObject(3, Constants.godinaZakljucena ? 1 : 0);
			proc.setObject(4, now);

			proc.executeUpdate();
			DBConnection.getConnection().commit();
			proc.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}
}
