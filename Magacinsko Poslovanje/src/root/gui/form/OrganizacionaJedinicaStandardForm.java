package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class OrganizacionaJedinicaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPreduzece = new JButton("...");
	private JButton btnZoomOrgJedinica = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JTextField tfNaziv = new JTextField(20);
	protected JCheckBox chkMagacin = new JCheckBox();

	public OrganizacionaJedinicaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Organizacione jedinice");

		JLabel lblNaziv = new JLabel("Naziv jedinice:");
		JLabel lblMagacin = new JLabel("Magacin:");
		JLabel lblOrgJedinica = new JLabel("Nadsektor:");
		JLabel lblPreduzece = new JLabel("Preduzeće:");
		tfNaziv.setName("naziv jedinice");
		chkMagacin.setName("magacin");

		cmbOrgJedinica = super.setupJoins(cmbOrgJedinica, "Organizaciona_jedinica", "Org_id_jedinice",
				"Org_id jedinice", "naziv_jedinice", "naziv nadsektora", true);
		cmbOrgJedinica.insertItemAt(new ComboBoxPair(0, ""), 0);
		cmbPreduzece = super.setupJoins(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća", "naziv_preduzeca",
				"naziv preduzeća", false);
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
			btnZoomOrgJedinica.setVisible(false);
		}

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNaziv, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(chkMagacin, "wrap,gapx 15px, span 3");

		dataPanel.add(lblOrgJedinica);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica, "wrap, gapx 15px");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece);
		dataPanel.add(btnZoomPreduzece);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PreduzeceAction());
		popup.add(new RadnikAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Organizaciona jedinica", joinColumn);
		tableModel.setColumnForSorting(3);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowDeletion() {
		// TODO Auto-generated method stub
		return false;
	}
}
