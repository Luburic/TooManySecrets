package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.dialog.MagacinskaKarticaAction;
import root.gui.action.dialog.PopisniDokumentAction;
import root.gui.action.dialog.PrometniDokumentAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.verification.JTextFieldLimit;

public class PoslovnaGodinaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;

	protected JTextField tfGodina = new JTextField(4);
	protected JCheckBox chkZakljucena = new JCheckBox();
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();

	public PoslovnaGodinaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Poslovna godina");

		JLabel lblGodina = new JLabel("Godina:");
		JLabel lblZakljucena = new JLabel("Zaključena:");
		JLabel lblPreduzece = new JLabel("Preduzeće:");
		tfGodina.setName("godina");
		chkZakljucena.setName("zaključena");
		chkZakljucena.setEnabled(false);

		tfGodina.setDocument(new JTextFieldLimit(4));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);

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
		popup.add(new MagacinskaKarticaAction());
		popup.add(new PrometniDokumentAction());
		popup.add(new PopisniDokumentAction());
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
