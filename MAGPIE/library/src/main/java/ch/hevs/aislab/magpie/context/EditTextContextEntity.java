package ch.hevs.aislab.magpie.context;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import alice.tuprolog.Term;
import ch.hevs.aislab.magpie.event.EditTextEvent;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.support.NumericValidator;
import hevs.aislab.magpie.R;

public class EditTextContextEntity extends EditText {

private static final String TAG = "Magpie-EditTextContextEntity";
	
	private String service;
	private String logicTupleName;
	
	private int value;
	
	private NumericValidator validator;
	
	public EditTextContextEntity(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public EditTextContextEntity(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	protected void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextContextEntity);
		service = typedArray.getString(R.styleable.EditTextContextEntity_service);
		logicTupleName = typedArray.getString(R.styleable.EditTextContextEntity_logicTupleName);
		
		String error = getResources().getString(R.string.error_only_numbers_allowed);
		validator = new NumericValidator(error);  
		typedArray.recycle();
		
		addTextChangedListener(textWatcher);
	}
	
	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public EditTextEvent getEvent() throws NullPointerException {
		if ((getError() == null) && (getText().length() > 0)) {
			this.setText("");
			return new EditTextEvent(service, value);
		} else {
			throw new NullPointerException();
		}	
	}
	
	public LogicTupleEvent getLogicTupleEvent() throws NullPointerException {
		if ((getError() == null) && (getText().length() > 0)) {
			this.setText("");
			return new LogicTupleEvent(Term.createTerm(logicTupleName + "(" + value + ")"));
		} else {
			throw new NullPointerException();
		}	
	}
	
	private boolean isValid() {
		return validator.check(this);
	}
	
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.i(TAG, "EditTextContextEntity - onTextChanged(...)");
			// Check if the text is a number
			if (!isValid()) {
				setError(validator.getErrorMessage());
				return;
			}
			
			// Avoid an exception when the box is empty
			if (s.length() < 1) {
				return;
			}
			
			try {
				value = Integer.parseInt(EditTextContextEntity.this.getText().toString());
			} catch (NumberFormatException e) {
				setError(getResources().getString(R.string.error_int_out_of_range));
			}
		}
	};
}
