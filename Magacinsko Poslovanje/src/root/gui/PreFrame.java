package root.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.Lookup;

public class PreFrame extends JDialog {
	private static final long serialVersionUID = 3409703900859791816L;
	private static PreFrame instance = null;

	private JLabel lblPreduzece = new JLabel("Preduzeće:");
	private JComboBox<ComboBoxPair> cmbPreduzece;
	private JLabel lblGodina = new JLabel("Poslovna godina:");
	private JComboBox<ComboBoxPair> cmbGodina;
	private JButton jbOK = new JButton("Prosledi");
	private JButton jbTrial = new JButton("Režim osnovne administracije");
	private JButton jbCancel = new JButton("Odustani");

	private PreFrame() {
		MigLayout migLayout = new MigLayout("wrap 3");
		setLayout(migLayout);
		setTitle("Izbor preduzeća i poslovne godine");
		setModal(true);
		jbOK.setEnabled(false);
		try {
			cmbPreduzece = new JComboBox<ComboBoxPair>(Lookup.getComboBoxEntity("Preduzece", "id_preduzeca",
					"naziv_preduzeca", ""));
			cmbPreduzece.insertItemAt(new ComboBoxPair(0, "-----"), 0);
			cmbPreduzece.setSelectedIndex(0);
			cmbGodina = new JComboBox<ComboBoxPair>();
			cmbGodina.insertItemAt(new ComboBoxPair(0, "-----"), 0);
			cmbGodina.setSelectedIndex(0);
			cmbPreduzece.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmbPreduzece.getSelectedIndex() > 0) {
						try {
							cmbGodina.setEnabled(true);
							cmbGodina.setModel(new DefaultComboBoxModel<ComboBoxPair>(Lookup.getComboBoxEntity(
									"Poslovna_godina", "id_poslovne_godine", "godina", " WHERE id_preduzeca = "
											+ ((ComboBoxPair) cmbPreduzece.getSelectedItem()).getId())));
							cmbGodina.insertItemAt(new ComboBoxPair(0, "-----"), 0);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					} else {
						cmbGodina.setEnabled(false);
					}
					cmbGodina.setSelectedIndex(0);
					jbOK.setEnabled(false);
				}
			});
			cmbGodina.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmbGodina.getSelectedIndex() > 0) {
						jbOK.setEnabled(true);
					} else {
						jbOK.setEnabled(false);
					}
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		jbOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Constants.idGodine = ((ComboBoxPair) cmbGodina.getSelectedItem()).getId();
				Constants.godina = ((ComboBoxPair) cmbGodina.getSelectedItem()).getCmbShow();
				try {
					Constants.godinaZakljucena = Lookup.getGodinaZakljucena(Constants.idGodine);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				Constants.idPreduzeca = ((ComboBoxPair) cmbPreduzece.getSelectedItem()).getId();
				Constants.nazivPreduzeca = ((ComboBoxPair) cmbPreduzece.getSelectedItem()).getCmbShow();
				MainFrame.getInstance().setUpFrame();
				setVisible(false);
			}
		});
		jbTrial.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbGodina.setSelectedIndex(0);
				cmbPreduzece.setSelectedIndex(0);
				jbOK.setEnabled(false);
				Constants.idGodine = 0;
				Constants.idPreduzeca = 0;
				MainFrame.getInstance().setUpFrame();
				setVisible(false);
			}
		});

		add(lblPreduzece);
		add(cmbPreduzece, "wrap, gapx 15, span 2");
		add(lblGodina);
		add(cmbGodina, "wrap, gapx 15, span 2");
		add(jbOK);
		add(jbTrial);
		add(jbCancel);
		setSize(400, 180);
		this.setResizable(false);
	}

	public static PreFrame getInstance() {
		if (instance == null) {
			instance = new PreFrame();
		}
		if (!instance.isVisible()) {
			int x = (int) ((MainFrame.getInstance().getLocation().getX() + MainFrame.getInstance().getWidth() / 2) - 200);
			int y = (int) ((MainFrame.getInstance().getLocation().getY() + MainFrame.getInstance().getHeight() / 2) - 90);
			instance.setLocation(x, y);
			instance.setVisible(true);
		}
		return instance;
	}
}