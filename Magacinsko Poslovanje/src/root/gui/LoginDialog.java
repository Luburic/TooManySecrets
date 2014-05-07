package root.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
					sqlSelect = "SELECT [dbo].[KORISNIK].[USERNAME], [dbo].[KORISNIK].[PASSWORD], [dbo].[KORISNIK].[ROLE] FROM [dbo].[KORISNIK] WHERE [dbo].[KORISNIK].[USERNAME] = '"
							+ uname + "'" + " AND [dbo].[KORISNIK].[PASSWORD] = '" + pwd + "'";
					PreparedStatement statement = DBConnection.getConnection().prepareStatement(sqlSelect);
					ResultSet rset = statement.executeQuery();
					if (rset.next()) {
						User user = new User(rset.getString(1), rset.getString(2), rset.getString(3));
						Constants.setCurrentUser(user);

						MainFrame mw = new MainFrame();
						dispose();
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

		setSize(300, 180);
		setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}
}
