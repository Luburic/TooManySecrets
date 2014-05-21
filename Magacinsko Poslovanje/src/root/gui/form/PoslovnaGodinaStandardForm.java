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

public class PoslovnaGodinaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;

	protected JTextField tfGodina = new JTextField(4);
	protected JCheckBox chkZakljucena = new JCheckBox();

	public PoslovnaGodinaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Poslovna godina");

		JLabel lblGodina = new JLabel("Godina:");
		JLabel lblZakljucena = new JLabel("Zaključena:");
		JLabel lblPreduzece = new JLabel("Preduzeće:");
		tfGodina.setName("godina");
		chkZakljucena.setName("zaključena");

		cmbPreduzece = super.setupJoins(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća", "naziv_preduzeca",
				"naziv preduzeća", false);
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
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
		}

		dataPanel.add(lblGodina);
		dataPanel.add(tfGodina, "wrap, gapx 15px");

		dataPanel.add(lblZakljucena);
		dataPanel.add(chkZakljucena, "wrap,gapx 15px, span 3");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece, "gapx 15px");

		dataPanel.add(btnZoom);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PreduzeceAction()); // IZMENA###########
		popup.add(new RadnikAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Poslovna godina", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}
}
