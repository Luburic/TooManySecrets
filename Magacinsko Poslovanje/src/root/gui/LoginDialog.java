package root.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import root.data.User;
import root.dbConnection.DBConnection;
import root.util.Constants;

public class LoginDialog extends JFrame {
	private static final long serialVersionUID = -2199313384659333005L;

	private JLabel jlUname = new JLabel("Korisničko ime:");
	private JTextField jtfUname = new JTextField(20);
	private JLabel jlPwd = new JLabel("Lozinka:");
	private JTextField jtfPwd = new JPasswordField(20);
	private JButton jbOK = new JButton("Prijavi se");
	private JButton jbCancel = new JButton("Odustani");
	private JLabel jlError = new JLabel("Pogrešno korisničko ime/lozinka");

	public LoginDialog() {
		MigLayout migLayout = new MigLayout("wrap 2");
		setLayout(migLayout);
		setTitle("Prijava na sistem");
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
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					String uname = jtfUname.getText();
					String pwd = jtfPwd.getText();
					uname = uname.replace("'", "''");
					pwd = pwd.replace("'", "''");
					String sqlSelect = "";
					sqlSelect = "SELECT [dbo].[KORISNIK].[USERNAME], [dbo].[KORISNIK].[PASSWORD], [dbo].[KORISNIK].[PASSWORD SALT], [dbo].[KORISNIK].[ROLE] FROM [dbo].[KORISNIK] WHERE [dbo].[KORISNIK].[USERNAME] = '"
							+ uname + "'";
					PreparedStatement statement = DBConnection.getConnection().prepareStatement(sqlSelect);
					ResultSet rset = statement.executeQuery();
					if (rset.next()) {
						if (User.checkPassword(pwd, rset.getString(3), rset.getString(2))) {
							Constants.setCurrentUser(new User(rset.getString(1), rset.getString(2), rset.getString(3)));
							MainFrame mw = MainFrame.getInstance();
							dispose();
						} else {
							jlError.setVisible(true);
						}
					} else {
						jlError.setVisible(true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
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
				try {
					DBConnection.getConnection().close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		setSize(300, 180);
		setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}
}
