package firma.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.miginfocom.swing.MigLayout;
import util.ConstantsXWS;
import util.accessControl.Korisnici;
import util.accessControl.TKorisnik;

public class LoginDialog extends JFrame {
	private static final long serialVersionUID = -2199313384659333005L;

	private JLabel jlUname = new JLabel("Korisničko ime:");
	private JTextField jtfUname = new JTextField(20);
	private JLabel jlPwd = new JLabel("Lozinka:");
	private JTextField jtfPwd = new JPasswordField(20);
	private JButton jbOK = new JButton("Prijavi se");
	private JButton jbCancel = new JButton("Odustani");
	private JLabel jlError = new JLabel("Pogrešno korisničko ime/lozinka");

	private Korisnici korisnici;

	public LoginDialog() {
		MigLayout migLayout = new MigLayout("wrap 2");
		setLayout(migLayout);
		setTitle("Prijava na sistem");

		try {
			File fXmlFile = new File("./KontrolaPristupa/korisnici.xml");
			JAXBContext context = JAXBContext.newInstance("util.accessControl");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			korisnici = (Korisnici) unmarshaller.unmarshal(fXmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		jlError.setForeground(Color.red);
		jlError.setVisible(false);
		jbCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});
		jbOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String uname = jtfUname.getText();
				String pwd = jtfPwd.getText();

				for (TKorisnik korisnik : korisnici.getKorisnik()) {
					if (uname.equals(korisnik.getUsername())) {
						if (TKorisnik.checkPassword(pwd, korisnik.getPasswordSalt(), korisnik.getPassword())) {
							ConstantsXWS.TRENUTNI_KORISNIK = korisnik;
							MainFrame.getInstance();
							dispose();
						}
					}
				}
				jlError.setVisible(true);
			}
		});

		add(jlUname, "cell 0 0");
		add(jtfUname, "cell 1 0");
		add(jlPwd, "cell 0 1");
		add(jtfPwd, "cell 1 1");
		add(jlError, "cell 0 2");
		add(jbOK, "cell 0 3");
		add(jbCancel, "cell 1 3");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});

		setSize(300, 180);
		setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}
}
