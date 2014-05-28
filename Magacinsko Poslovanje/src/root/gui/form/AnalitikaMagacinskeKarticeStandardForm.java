package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.dialog.PoslovniPartnerAction;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class AnalitikaMagacinskeKarticeStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbVrsta;
	protected JTextField tfRedniBroj = new JTextField(3);
	protected JTextField tfSmer = new JTextField(1);
	protected JTextField tfKolicina = new JTextField(12);
	protected JTextField tfCena = new JTextField(12);
	protected JTextField tfVrednost = new JTextField(12);

	public AnalitikaMagacinskeKarticeStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Analitika magacinske kartice");

		JLabel lblRedniBroj = new JLabel("Redni broj:");
		JLabel lblSmer = new JLabel("Smer:");
		JLabel lblKolicina = new JLabel("Količina:");
		JLabel lblCena = new JLabel("Cena:");
		JLabel lblVrednost = new JLabel("Vrednost:");
		JLabel lblVrsta = new JLabel("Vrsta:");

		cmbVrsta = super.setupJoins(cmbVrsta, "Drzava", "id_drzave", "id države", "naziv_drzave", "naziv države",
				false, "");
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbVrsta.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbVrsta.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new DrzavaStandardForm(cmbVrsta, ""), id);
				}
			});
		} else {
			cmbVrsta.setEnabled(false);
			btnZoom.setVisible(false);
		}

		dataPanel.add(lblRedniBroj);
		dataPanel.add(tfRedniBroj, "wrap, gapx 15px");

		dataPanel.add(lblSmer);
		dataPanel.add(tfSmer, "wrap, gapx 15px");

		dataPanel.add(lblVrsta);
		dataPanel.add(cmbVrsta);
		dataPanel.add(btnZoom, "wrap, gapx 15px");

		dataPanel.add(lblKolicina);
		dataPanel.add(tfKolicina, "wrap, gapx 15px");

		dataPanel.add(lblCena);
		dataPanel.add(tfCena, "wrap, gapx 15px");

		dataPanel.add(lblVrednost);
		dataPanel.add(tfVrednost, "wrap, gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PreduzeceAction());
		popup.add(new RadnikAction());
		popup.add(new PoslovniPartnerAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Analitika magacinske kartice", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		return false;
	}

	@Override
	public boolean allowDeletion() {
		return false;
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
