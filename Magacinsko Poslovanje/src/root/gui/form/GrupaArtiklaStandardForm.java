package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.dialog.ArtikalAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class GrupaArtiklaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbGrupa;
	protected JTextField tfNazivGrupe = new JTextField(20);

	public GrupaArtiklaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Grupe artikla");

		JLabel lblNaziv = new JLabel("Naziv grupe:");
		JLabel lblGrupa = new JLabel("Nadgrupa:");
		tfNazivGrupe.setName("naziv grupe");
		cmbGrupa = super.setupJoins(cmbGrupa, "Grupa_artikla", "Gru_id_grupe", "Gru_id grupe", "naziv_grupe",
				"naziv nadgrupe", true);
		cmbGrupa.insertItemAt(new ComboBoxPair(0, ""), 0);
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbGrupa.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbGrupa.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new GrupaArtiklaStandardForm(cmbGrupa, ""), id);
				}
			});
		} else {
			cmbGrupa.setEnabled(false);
		}

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivGrupe, "wrap,gapx 15px, span 3");

		dataPanel.add(lblGrupa);
		dataPanel.add(cmbGrupa, "gapx 15px");

		dataPanel.add(btnZoom);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ArtikalAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Grupa artikla", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
