package eu.transkribus.swt_gui.htr;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.PyLaiaCreateModelPars;
import eu.transkribus.core.model.beans.PyLaiaTrainCtcPars;
import eu.transkribus.core.model.beans.TextFeatsCfg;
import eu.transkribus.core.model.beans.TrpPreprocPars;
import eu.transkribus.core.model.beans.rest.ParameterMap;
import eu.transkribus.swt.util.Fonts;
import eu.transkribus.swt.util.SWTUtil;

public class PyLaiaAdvancedConfComposite extends Composite {
	private static final Logger logger = LoggerFactory.getLogger(PyLaiaAdvancedConfComposite.class);
	
	TrpPreprocPars trpPreprocPars = new TrpPreprocPars();
	TextFeatsCfg textFeatsCfg = new TextFeatsCfg();
	PyLaiaCreateModelPars modelPars;
	PyLaiaTrainCtcPars trainPars;
	
	CTabFolder preprocTf;
	CTabItem textFeatsTi, trpPreprocTi;
	PyLaiaTextFeatsComposite textFeatsComp;
	PyLaiaTrpPreprocComposite trpPreprocComp;
	
	Group preprocGroup;
	
	Group modelParsGroup;
	Text modelParsText;
	
	Group trainParsGroup;
	Text trainParsText;
	
	public PyLaiaAdvancedConfComposite(Composite parent, /*int batchSize,*/ TextFeatsCfg textFeatsCfg, TrpPreprocPars trpPreprocPars, PyLaiaCreateModelPars modelPars, PyLaiaTrainCtcPars trainPars) {
		super(parent, 0);

//		this.batchSize = batchSize;
		this.trpPreprocPars = trpPreprocPars;
		this.textFeatsCfg = textFeatsCfg;
		
		this.modelPars = modelPars == null ? PyLaiaCreateModelPars.getDefault() : modelPars;
		this.trainPars = trainPars == null ? PyLaiaTrainCtcPars.getDefault() : trainPars;
		
		// remove pars that are explicitly set via custom UI fields
//		this.trainPars.remove("--batch_size"); // set via custom field
		this.trainPars.remove("--max_nondecreasing_epochs"); // set via main UI
		this.trainPars.remove("--max_epochs"); // set via main UI
		this.trainPars.remove("--learning_rate"); // set via main UI

		// those are the fixed parameters that cannot be changed by the user (will be set to default value at server):
		this.modelPars.remove("--fixed_input_height"); // determined via fixed height par in textFeatsCfg
		this.modelPars.remove("--logging_level");
		this.modelPars.remove("--logging_also_to_stderr");
		this.modelPars.remove("--logging_file");
		this.modelPars.remove("--logging_config");
		this.modelPars.remove("--logging_overwrite");
		this.modelPars.remove("--train_path");
		this.modelPars.remove("--model_filename");
		this.modelPars.remove("--print_args");
		
		this.trainPars.remove("--logging_level");
		this.trainPars.remove("--logging_also_to_stderr");
		this.trainPars.remove("--logging_file");
		this.trainPars.remove("--logging_config");
		this.trainPars.remove("--logging_overwrite");
		this.trainPars.remove("--train_path");
		this.trainPars.remove("--show_progress_bar");
		this.trainPars.remove("--delimiters");
		this.trainPars.remove("--print_args");
		this.trainPars.remove("--save_checkpoint_interval");
		
		createContent();
	}
	
	public TextFeatsCfg getTextFeatsCfg() {
		if (preprocTf.getSelection() == textFeatsTi) {
			this.textFeatsCfg = textFeatsComp.getTextFeatsCfg();
		}
		else {
			this.textFeatsCfg = null;
		}
		return textFeatsCfg;
	}
	
	public TrpPreprocPars getTrpPreprocPars() {
		if (preprocTf.getSelection() == trpPreprocTi) {
			this.trpPreprocPars = trpPreprocComp.getTrpPreprocPars();
		}
		else {
			this.trpPreprocPars = null;
			return null;
		}
		return trpPreprocPars;
	}
	
	public PyLaiaCreateModelPars getCreateModelPars() {
		PyLaiaCreateModelPars modelPars = new PyLaiaCreateModelPars();
		insertParametersFromText(modelParsText, modelPars);
		this.modelPars = modelPars;
		return this.modelPars;
	}
	
	public PyLaiaTrainCtcPars getTrainCtcPars() {
		PyLaiaTrainCtcPars trainPars = new PyLaiaTrainCtcPars();
		insertParametersFromText(trainParsText, trainPars);
		this.trainPars = trainPars;
		return this.trainPars;
	}
	
	private ParameterMap insertParametersFromText(Text text, ParameterMap parMap) {
		for (String line : text.getText().split("\n")) {
			parMap.addParameterFromSingleLine(line, " ");
		}
		return parMap;
	}
	
	private void updateUi() {
		// preprocess pars:
		trpPreprocComp.updateUi();
		textFeatsComp.updateUi();
		
		// model pars:
		modelParsText.setText(modelPars.toSimpleStringLineByLine());
		
		// train pars:
		trainParsText.setText(trainPars.toSimpleStringLineByLine());
	}
	
	private void createContent() {
		this.setLayout(new GridLayout(1, false));
//		batchSizeText = new LabeledText(this, "Batch size: ");
//		batchSizeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		Fonts.setBoldFont(batchSizeText.getLabel());
		
		SashForm subC = new SashForm(this, 0);
		subC.setLayout(SWTUtil.createGridLayout(3, false, 0, 0));
		subC.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createPreprocessUi(subC);
		createModelParsUi(subC);
		createTrainParsUi(subC);
		
		subC.setWeights(new int[] {3, 3, 2});
		
		updateUi();
	}
	
	private void createModelParsUi(Composite parent) {
		modelParsGroup = new Group(parent, 0);
		Fonts.setBoldFont(modelParsGroup);
		modelParsGroup.setText("Model");
		modelParsGroup.setLayout(new GridLayout(1, false));
		modelParsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		modelParsText = new Text(modelParsGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		modelParsText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	private void createTrainParsUi(Composite parent) {
		trainParsGroup = new Group(parent, 0);
		Fonts.setBoldFont(trainParsGroup);
		trainParsGroup.setText("Training");
		trainParsGroup.setLayout(new GridLayout(1, false));
		trainParsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		trainParsText = new Text(trainParsGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		trainParsText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	private void createPreprocessUi(Composite parent) {
		preprocGroup = new Group(parent, 0);
		Fonts.setBoldFont(preprocGroup);
		preprocGroup.setText("Preprocessing");
		preprocGroup.setLayout(new GridLayout(1, false));
		preprocGroup.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		preprocTf = new CTabFolder(preprocGroup, SWT.BORDER | SWT.FLAT);
		preprocTf.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		trpPreprocTi = new CTabItem(preprocTf, 0);
		trpPreprocTi.setText("TRP (new)");
		trpPreprocComp = new PyLaiaTrpPreprocComposite(preprocTf, trpPreprocPars);
		trpPreprocTi.setControl(trpPreprocComp);
		
		textFeatsTi = new CTabItem(preprocTf, 0);
		textFeatsTi.setText("TextFeats (old)");
		textFeatsComp = new PyLaiaTextFeatsComposite(preprocTf, textFeatsCfg);
		textFeatsTi.setControl(textFeatsComp);
		
		if (trpPreprocPars != null) {
			preprocTf.setSelection(0);
		}
		else {
			preprocTf.setSelection(1);	
		}
		
		SWTUtil.setTabFolderBoldOnItemSelection(preprocTf);
	}

}
