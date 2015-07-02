package ch.hevs.aislab.magpie.support;

import android.text.TextUtils;
import android.widget.EditText;

public class NumericValidator {

	String error;
	
	public NumericValidator(String error) {
		this.error = error;
	}
	
	public boolean check(EditText editText) {
		return TextUtils.isDigitsOnly(editText.getText());
	}
	
	public String getErrorMessage() {
		return error;
	}
}
