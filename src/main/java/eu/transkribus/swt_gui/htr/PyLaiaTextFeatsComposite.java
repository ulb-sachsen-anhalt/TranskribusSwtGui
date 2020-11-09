package eu.transkribus.swt_gui.htr;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.transkribus.core.model.beans.TextFeatsCfg;
import eu.transkribus.swt.util.LabeledText;
import eu.transkribus.swt.util.SWTUtil;

public class PyLaiaTextFeatsComposite extends Composite {
	Button deslopeCheck;
	Button deslantCheck;
	Button stretchCheck;
	Button enhanceCheck;
	LabeledText enhWinText;
	LabeledText enhPrmText;
	LabeledText normHeightText;
	LabeledText normxHeightText;
	Button momentnormCheck;
	Button fpgramCheck;
	Button fcontourCheck;
	LabeledText fcontour_dilateText;
	LabeledText paddingText;
	LabeledText maxwidthText;
	
	TextFeatsCfg textFeatsCfg = new TextFeatsCfg();
	
	public PyLaiaTextFeatsComposite(Composite parent, TextFeatsCfg textFeatsCfg) {
		super(parent, 0);
		this.setLayout(new GridLayout(1, false));
		this.textFeatsCfg = textFeatsCfg == null ? new TextFeatsCfg() : textFeatsCfg;
		createUi();
		updateUi();
	}	
	
	private void createUi() {
		deslopeCheck = new Button(this, SWT.CHECK);
		deslopeCheck.setText("Deslope");
		deslopeCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		deslantCheck = new Button(this, SWT.CHECK);
		deslantCheck.setText("Deslant");
		deslantCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stretchCheck = new Button(this, SWT.CHECK);
		stretchCheck.setText("Stretch");
		stretchCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		enhanceCheck = new Button(this, SWT.CHECK);
		enhanceCheck.setText("Enhance");
		enhanceCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		enhWinText = new LabeledText(this, "Enhance window size: ");
		enhWinText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		enhPrmText = new LabeledText(this, "Sauvola enhancement parameter: ");
		enhPrmText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		normHeightText = new LabeledText(this, "Line height: ");
		normHeightText.setToolTipText("Normalized height of extracted lines. Set to 0 for no normalization.");
		normHeightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		normxHeightText = new LabeledText(this, "Line x-height: ");
		normxHeightText.setToolTipText("Normalized x-height (= height - descender and cap height) of extracted lines. Set to 0 for no normalization.");
		normxHeightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		momentnormCheck = new Button(this, SWT.CHECK);
		momentnormCheck.setText("Moment normalization");
		momentnormCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fpgramCheck = new Button(this, SWT.CHECK);
		fpgramCheck.setText("Features parallelogram");
		fpgramCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fcontourCheck = new Button(this, SWT.CHECK);
		fcontourCheck.setText("Features surrounding polygon");
		fcontourCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fcontour_dilateText = new LabeledText(this, "Features surrounding polygon dilate: ");
		fcontour_dilateText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		paddingText = new LabeledText(this, "Left/right padding: ");
		paddingText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		maxwidthText = new LabeledText(this, "Max width: ");
		maxwidthText.setToolTipText("Maximum width of the output line - warning: exceeding pixels are cut off on the right side!");
		maxwidthText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
	}	
	
	public TextFeatsCfg getTextFeatsCfg() {
		textFeatsCfg.setDeslope(deslopeCheck.getSelection());
		textFeatsCfg.setDeslant(deslantCheck.getSelection());
		textFeatsCfg.setStretch(stretchCheck.getSelection());
		textFeatsCfg.setEnh(enhanceCheck.getSelection());
		
		textFeatsCfg.setEnh_win(enhWinText.toIntVal(textFeatsCfg.getEnh_win()));
		textFeatsCfg.setEnh_prm(enhPrmText.toDoubleVal(textFeatsCfg.getEnh_prm()));
		textFeatsCfg.setNormheight(normHeightText.toIntVal(textFeatsCfg.getNormheight()));
		textFeatsCfg.setNormxheight(normxHeightText.toIntVal(textFeatsCfg.getNormxheight()));
		
		textFeatsCfg.setMomentnorm(momentnormCheck.getSelection());
		textFeatsCfg.setFpgram(fpgramCheck.getSelection());
		textFeatsCfg.setFcontour(fcontourCheck.getSelection());
		
		textFeatsCfg.setFcontour_dilate(fcontour_dilateText.toIntVal(textFeatsCfg.getFcontour_dilate()));
		textFeatsCfg.setPadding(paddingText.toIntVal(textFeatsCfg.getPadding()));
		textFeatsCfg.setMaxwidth(maxwidthText.toIntVal(textFeatsCfg.getMaxwidth()));
		
		return textFeatsCfg;
	}	
	
	public void updateUi() {
		deslopeCheck.setSelection(textFeatsCfg.isDeslope());
		deslantCheck.setSelection(textFeatsCfg.isDeslant());
		stretchCheck.setSelection(textFeatsCfg.isStretch());
		enhanceCheck.setSelection(textFeatsCfg.isEnh());
		enhWinText.setText(""+textFeatsCfg.getEnh_win());
		enhPrmText.setText(""+textFeatsCfg.getEnh_prm());
		normHeightText.setText(""+textFeatsCfg.getNormheight());
		normxHeightText.setText(""+textFeatsCfg.getNormxheight());
		momentnormCheck.setSelection(textFeatsCfg.isMomentnorm());
		fpgramCheck.setSelection(textFeatsCfg.isFpgram());
		fcontourCheck.setSelection(textFeatsCfg.isFcontour());
		fcontour_dilateText.setText(""+textFeatsCfg.getFcontour_dilate());
		paddingText.setText(""+textFeatsCfg.getPadding());
		maxwidthText.setText(""+textFeatsCfg.getMaxwidth());
	}

}
