package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Text field with a slider for value adjustment.
 * <br><br>
 * Currently uses double values internally!<br>
 */
public class TrpSliderComposite extends TrpNumberTextComposite {
	private static final Logger logger = LoggerFactory.getLogger(TrpSliderComposite.class);
	
	private final static int THUMB_SIZE = 5; // size of slider thumb
	
	Slider sldr;
	
	public TrpSliderComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected int getNumColumns() {
		return super.getNumColumns() + 4;
	}
	
	@Override
	protected void createCompositeArea(Composite parent) {
		super.createCompositeArea(parent);
		
		sldr = new Slider(this, SWT.HORIZONTAL);
		sldr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		sldr.setThumb(THUMB_SIZE);
		sldr.setMinimum(convertToSliderValue(super.getMinValue()));
		
		sldr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("Slider event: {}, state = {}", e.detail, e.stateMask);
				if(e.detail == SWT.NONE || e.detail == 1){
					setValue(sldr.getSelection() / 100.0);
				}
			}
		});
	}
	
	@Override
	protected void updateView() {
		updateSlider();
		super.updateView();
	}
	
	@Override
	public void setMaximum(double max) {
		sldr.setMaximum(convertToSliderValue(max) + THUMB_SIZE);
		super.setMaximum(max);
	}
	
	private void updateSlider() {
		logger.debug("Updating slider: {}", value);
		sldr.setSelection(convertToSliderValue(value));
	}
	
	private int convertToSliderValue(Double value) {
		if (value == null) {
			throw new IllegalArgumentException("Value must not be null");
		}
		final Double sliderVal = value * 100;
		logger.debug("converted to slider value: {} -> {}", value, sliderVal);
		return sliderVal.intValue();
	}
}