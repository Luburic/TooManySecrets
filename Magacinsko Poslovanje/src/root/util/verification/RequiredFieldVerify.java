package root.util.verification;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.util.Constants;

public class RequiredFieldVerify extends InputVerifier {
	private JLabel errorLabel;

	public RequiredFieldVerify(JLabel errorLabel) {
		super();
		this.errorLabel = errorLabel;
	}

	@Override
	public boolean verify(JComponent input) {
		String text = ((JTextField) input).getText();
		boolean empty = text.isEmpty();
		if (empty) {
			input.setBackground(Color.red);
			errorLabel.setText(Constants.VALIDATION_MANDATORY_FIELD);
		}
		return !empty;
	}

}
