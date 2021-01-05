package org.eclipse.swt.widgets;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.swt.util.Colors;

/**
 * A Text field for number input. Validates input in terms of min & max values and warns on non-numerical text.
 * <br><br>
 * Currently uses double values internally! Use {@link #setNumberFormat(DecimalFormat)} to restrict on other types.<br>
 */
public class TrpNumberTextComposite extends Composite {
	private static final Logger logger = LoggerFactory.getLogger(TrpNumberTextComposite.class);
	
	private final static double MIN_VALUE = 1.00;
	private DecimalFormat valueFormat;
	
	double value;
	double max;
	Text txt;		

	public TrpNumberTextComposite (Composite parent, int style) {
		super(parent, style);
		GridLayout gl = new GridLayout(getNumColumns(), true);
		gl.marginWidth = 0;
		this.setLayout(gl);
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		createCompositeArea(this);
			
		valueFormat = new DecimalFormat("0.00");
		setValue(MIN_VALUE);
	}
	
	protected void createCompositeArea(Composite parent) {
		txt = new Text(this, SWT.BORDER);
		txt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txt.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				//Do nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				final String text = txt.getText();
				logger.debug("Key released. text: {}", text);
				Double value = getValue();
				if(!StringUtils.isEmpty(text)) {
					try {
						value = Double.parseDouble(text);
						txt.setForeground(Colors.getSystemColor(SWT.COLOR_BLACK));
						if(value < MIN_VALUE) {
							value = MIN_VALUE;
						}
						if(value > max) {
							value = max;
						}
						setValue(value);
					} catch(NumberFormatException nfe) {
						logger.error("Could not parse value {}: {}", text, nfe.getMessage());
						txt.setForeground(Colors.getSystemColor(SWT.COLOR_RED));
						//invalidate value, but do not update view
						TrpNumberTextComposite.this.value = Double.MIN_VALUE;
					}
				}
			}
		});
	}

	protected int getNumColumns() {
		return 1;
	}
	
	public double getMinValue() {
		return MIN_VALUE;
	}
	
	public void setMaximum(double max) {
		this.max = max;
		if(value > max) {
			setValue(max);
		}
	}

	public void setValue(double value) {
		this.value = value;
		updateView();
		this.notifyListeners(SWT.Modify, new Event());
	}
	
	protected void updateView() {
		updateTxt();
	}
	
	public boolean isValid() {
		return value >= MIN_VALUE && value <= max;
	}
	
	public Double getValue() {
		if(!isValid()) {
			return null;
		}
		return value;
	}

	protected void updateTxt() {
		String txtValue = valueFormat.format(value);
		logger.debug("New value: {}", txtValue);
		if(!txtValue.equals(txt.getText())) {
			txt.setText(txtValue);
			logger.debug("Updating text field.");
		}
	}

	/**
	 * Define String format for values shown in the text field. Default is "0.00".
	 * 
	 * @param format a DecimalFormat. Null value in argument is ignored.
	 */
	public void setNumberFormat(DecimalFormat format) {
		if(format == null) {
			return;
		}
		valueFormat = format;
		updateView();
	}
}