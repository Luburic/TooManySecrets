package firma.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import client.firma.NalogClient;
import firma.gui.MainFrame;

public class NalogDialog extends JDialog{
	
	private JLabel lbIdPor = new JLabel("Id poruke:");
	private JLabel lbDuz = new JLabel("Dužnik - nalogodavac:");
	private JLabel lbSvr = new JLabel("Svrha plaæanja:");
	private JLabel lbPrim = new JLabel("Primalac - poverilac:");
	
	private JLabel lbDatNal = new JLabel("Datum naloga:");
	private JLabel lbDatVal = new JLabel("Datum valute:");
	
	private JLabel lbRacDuz = new JLabel("Raèun dužnika(18):");
	private JLabel lbModZad = new JLabel("Model zaduženja:");
	private JLabel lbPozBr = new JLabel("Poziv na broj zaduženja:");
	private JLabel lbRacPov = new JLabel("Raèun poverioca(18):");
	private JLabel lbModOdo = new JLabel("Model odobrenja:");
	private JLabel lbPozOdo = new JLabel("Poziv na broj odobrenja:");
	private JLabel lbIzn = new JLabel("Iznos:");
	private JLabel lbOznVal = new JLabel("Oznaka valute:");
	private JLabel lbHit = new JLabel("Hitno:");
	/************************************************************************/
	
	private JTextField tfIdPor = new JTextField();
	private JTextField tfDuz =  new JTextField();
	private JTextField tfSvr =  new JTextField();
	private JTextField tfPrim =  new JTextField();
	
	private JDatePickerImpl tfDatNal = new JDatePickerImpl(new JDatePanelImpl(
			null));
	
	private JDatePickerImpl tfDatVal = new JDatePickerImpl(new JDatePanelImpl(
			null));
	
	private JTextField tfRacDuz =  new JTextField();
	
	@SuppressWarnings("rawtypes")
	private JComboBox cbModZad =  new JComboBox();
	
	private JTextField tfPozBr =  new JTextField();
	private JTextField tfRacPov =  new JTextField();
	
	@SuppressWarnings("rawtypes")
	private  JComboBox cbModOdo =  new JComboBox();
	
	private JTextField tfPozOdo =  new JTextField();
	private JTextField tfIzn =  new JTextField();
	private JTextField tfOznVal =  new JTextField();
	
	private JCheckBox chbHit = new JCheckBox();
	
	private JButton btnOk = new JButton("Potvrdi");
	private JButton btnCancel = new JButton("Otkazi");
	/************************************************************************/

	
	
	
	
	
	public NalogDialog(MainFrame instance) {
		// TODO Auto-generated constructor stub
		super();
		setTitle("Popunjavanje naloga");
		setResizable(false);
		setSize(new Dimension(400, 300));
		setLayout(new MigLayout("fill"));
		initialize();
		pack();
		setLocationRelativeTo(instance);
		
		
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				
				
				if (("").equals(tfIdPor.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos id-a poruke!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfIdPor.requestFocus();
					return;
				}
			
			
				
				if (("").equals(tfDuz.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos duznika!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfDuz.requestFocus();
					return;
				}
				
				
				if (("").equals(tfSvr.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos namene uplate!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfSvr.requestFocus();
					return;
				}
				
				
				if (("").equals(tfPrim.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos primaoca uplate!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfPrim.requestFocus();
					return;
				}
				
				if (("").equals(tfDatNal.getJFormattedTextField().getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos datuma naloga!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfDatNal.requestFocus();
					return;
				}
				
				if (("").equals(tfDatVal.getJFormattedTextField().getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos datuma valute!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfDatVal.requestFocus();
					return;
				}
			
				if (("").equals(tfRacDuz.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos racuna duznika!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfRacDuz.requestFocus();
					return;
				}
				
				
				if (tfRacDuz.getText().length()!=18) {
					JOptionPane.showMessageDialog(null,
							"Neispravna duzina racuna duznika!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfRacDuz.requestFocus();
					return;
					
				}
				
				if (cbModZad.getSelectedIndex() == 0) {
					JOptionPane.showMessageDialog(null,
							"Obavezan izbor modela zaduzenja!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					cbModZad.requestFocus();
					return;
				}
				
				
				
				
				
				if (("").equals(tfPozBr.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos poziva na broj zaduzenja!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfPozBr.requestFocus();
					return;
				}
				
				if (("").equals(tfRacPov.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos racuna poverioca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfRacPov.requestFocus();
					return;
				}
				
				
				if (tfRacPov.getText().length()!=18) {
					JOptionPane.showMessageDialog(null,
							"Neispravna duzina racuna poverioca!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfRacPov.requestFocus();
					return;
					
				}
				
				
				
				
				if (cbModOdo.getSelectedIndex() == 0) {
					JOptionPane.showMessageDialog(null,
							"Obavezan izbor modela odobrenja!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					cbModOdo.requestFocus();
					return;
				}
				
				
				
				if (("").equals(tfPozOdo.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos poziva na broj odobrenja!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfPozOdo.requestFocus();
					return;
				}
				
				
				
				if (("").equals(tfIzn.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos iznosa uplate!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfIzn.requestFocus();
					return;
				}
				
				BigDecimal iznos = null;
				try {
					iznos= BigDecimal.valueOf(new Double(tfIzn.getText()));
				}catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost iznosa uplate!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfIzn.requestFocus();
					return;
				}
				
				
				
				if (("").equals(tfOznVal.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos oznake valute!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfOznVal.requestFocus();
					return;
				}
			
				
				NalogClient nc = new NalogClient();
				/*nc.createNalog(
						tfIdPor.getText(),
						tfDuz.getText(),
						tfPrim.getText(),
						tfDatNal.getJFormattedTextField().getText(),
						tfDatVal.getJFormattedTextField().getText(),
						chbHit.isSelected(),
						iznos,
						Integer.valueOf((String) cbModZad.getSelectedItem()),
						Integer.valueOf((String) cbModOdo.getSelectedItem()),
						tfOznVal.getText(),
						tfPozBr.getText(),
						Integer.valueOf(tfPozOdo.getText()),
						tfRacDuz.getText(), 
						tfRacPov.getText(), 
						tfSvr.getText()
						);*/
				nc.createNalog();
				nc.testIt("firmaA", "bankaa", "cerbankaa", "./NalogTest/nalog.xml");
				
				String message="";
				
				JOptionPane.showMessageDialog(null,message,"Kreiranje naloga", JOptionPane.INFORMATION_MESSAGE);
				setVisible(false);
				
		}});
		
	
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void initialize() {
		
		cbModOdo.addItem("...");
		cbModOdo.addItem("96");
		cbModOdo.addItem("97");
		cbModZad.addItem("...");
		cbModZad.addItem("96");
		cbModZad.addItem("97");
	
		
		tfIdPor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfIdPor.getText().length()>49){
					tfIdPor.setText(tfIdPor.getText().substring(0,tfIdPor.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfDuz.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfDuz.getText().length()>254){
					tfDuz.setText(tfDuz.getText().substring(0,tfDuz.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfSvr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfSvr.getText().length()>254){
					tfSvr.setText(tfSvr.getText().substring(0,tfSvr.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfPrim.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfPrim.getText().length()>254){
					tfPrim.setText(tfPrim.getText().substring(0,tfPrim.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfRacDuz.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfRacDuz.getText().length()>17){
					tfRacDuz.setText(tfRacDuz.getText().substring(0,tfRacDuz.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfPozBr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfPozBr.getText().length()>19){
					tfPozBr.setText(tfPozBr.getText().substring(0,tfPozBr.getText().length()-1));
					return;
				}
			}
			
		});
		
		

		tfRacPov.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfRacPov.getText().length()>17){
					tfRacPov.setText(tfRacPov.getText().substring(0,tfRacPov.getText().length()-1));
					return;
				}
			}
			
		});
		
		tfPozOdo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfPozOdo.getText().length()>19){
					tfPozOdo.setText(tfPozOdo.getText().substring(0,tfPozOdo.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfIzn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfIzn.getText().length()>17){
					tfIzn.setText(tfIzn.getText().substring(0,tfIzn.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfOznVal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfOznVal.getText().length()>2){
					tfOznVal.setText(tfOznVal.getText().substring(0,tfOznVal.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		add(lbIdPor);
		tfIdPor.setMinimumSize(new Dimension(140, 20));
		add(tfIdPor, "wrap");

		add(lbDuz);
		tfDuz.setMinimumSize(new Dimension(140, 20));
		add(tfDuz, "wrap");
		
		add(lbSvr);
		tfSvr.setMinimumSize(new Dimension(140,20));
		add(tfSvr, "wrap");
		
		add(lbPrim);
		tfPrim.setMinimumSize(new Dimension(140, 20));
		add(tfPrim, "wrap");
		
		
		add(lbDatNal);
		tfDatNal.setMinimumSize(new Dimension(140, 20));
		add(tfDatNal, "wrap");
		
		
		
		add(lbDatVal);
		tfDatVal.setMinimumSize(new Dimension(140, 20));
		add(tfDatVal, "wrap");
		
	
		add(lbRacDuz);
		tfRacDuz.setMinimumSize(new Dimension(140, 20));
		add(tfRacDuz, "wrap");
		
		add(lbModZad);
		cbModZad.setMinimumSize(new Dimension(140, 20));
		add(cbModZad, "wrap");
		
		add(lbPozBr);
		tfPozBr.setMinimumSize(new Dimension(140, 20));
		add(tfPozBr, "wrap");
		
		add(lbRacPov);
		tfRacPov.setMinimumSize(new Dimension(140, 20));
		add(tfRacPov, "wrap");
		
	
		add(lbModOdo);
		cbModOdo.setMinimumSize(new Dimension(140, 20));
		add(cbModOdo, "wrap");
		
		add(lbPozOdo);
		tfPozOdo.setMinimumSize(new Dimension(140, 20));
		add(tfPozOdo, "wrap");
		
		add(lbIzn);
		tfIzn.setMinimumSize(new Dimension(140, 20));
		add(tfIzn, "wrap");
		
		add(lbOznVal);
		tfOznVal.setMinimumSize(new Dimension(140, 20));
		add(tfOznVal, "wrap");
		
		
		
		add(lbHit);
		chbHit.setMinimumSize(new Dimension(140, 20));
		add(chbHit, "wrap");
		
		
		add(btnOk,"gapleft 70");
		add(btnCancel);
		
	}
	
	
}
