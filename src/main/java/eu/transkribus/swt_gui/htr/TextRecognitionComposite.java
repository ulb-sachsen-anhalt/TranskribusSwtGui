
package eu.transkribus.swt_gui.htr;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.client.util.SessionExpiredException;
import eu.transkribus.core.model.beans.TrpHtr;
import eu.transkribus.core.model.beans.job.enums.JobImpl;
import eu.transkribus.swt.util.Images;
import eu.transkribus.swt.util.LabeledCombo;
import eu.transkribus.swt.util.SWTUtil;
import eu.transkribus.swt_gui.mainwidget.storage.IStorageListener;
import eu.transkribus.swt_gui.mainwidget.storage.Storage;

public class TextRecognitionComposite extends Composite {
	private static final Logger logger = LoggerFactory.getLogger(TextRecognitionComposite.class);
	
	private LabeledCombo methodCombo;
	
	public static final String METHOD_OCR = "OCR (Abbyy FineReader)";
	public static final String METHOD_HTR = "HTR (CITlab HTR+ & PyLaia)";
	
	public static final String[] METHODS_ADMIN = { METHOD_HTR, METHOD_OCR };
	public static final String[] METHODS = { METHOD_HTR };
	
	Button runBtn;
	
	HtrModelChooserButton modelsBtn;
	Button trainBtn;
	
	public TextRecognitionComposite(Composite parent, int style) {
		super(parent, style);
		
		int nCols = 2;
		GridLayout gl = new GridLayout(nCols, false);
		gl.marginHeight = gl.marginWidth = 0;
		this.setLayout(gl);

		methodCombo = new LabeledCombo(this, "Method:");
		methodCombo.combo.setItems(METHODS);
		methodCombo.combo.select(0);
		methodCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nCols, 1));
		methodCombo.combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateGui();
			}
		});
		
		modelsBtn = new HtrModelChooserButton(this, false) {
			@Override public void setModel(TrpHtr htr) {}
		};
		modelsBtn.setText("Models...");
//		modelsBtn.setImage(Images.getOrLoad("/icons/model2_16.png"));
		modelsBtn.setImage(Images.MODEL_ICON);
		modelsBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		trainBtn = new Button(this, 0);
		trainBtn.setText("Train..."); //α Train
		trainBtn.setImage(Images.TRAIN);
//		trainBtn.setImage(Images.MODEL_ADD_ICON);
		trainBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		
//		text2ImageBtn = new Button(this, SWT.PUSH);
//		text2ImageBtn.setText("Text2Image (experimental)...");
//		text2ImageBtn.setImage(Images.getOrLoad("/icons/image_link.png"));
//		text2ImageBtn.setToolTipText("Tries to align the text in this document to a layout analysis\nWarning: does take some time...");
////		text2ImageBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		text2ImageBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		runBtn = new Button(this, 0);
		runBtn.setText("Run...");
		runBtn.setImage(Images.ARROW_RIGHT);
		runBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		runBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nCols, 1));
		
		Storage.getInstance().addListener(new IStorageListener() {
			public void handleLoginOrLogout(LoginOrLogoutEvent arg) {
				updateGui();
			}
		});
		
		updateGui();
	}
	
	public Button getRunBtn() {
		return runBtn;
	}
	
	public Button getTrainBtn() {
		return trainBtn;
	}
	
	public HtrModelChooserButton getModelBtn() {
		return modelsBtn;
	}
	
//	public Button getText2ImageBtn() {
//		return text2ImageBtn;
//	}
	
	public String getSelectedMethod() {
		return methodCombo.txt();
	}
	
	public boolean isOcr() {
		return methodCombo.txt().equals(METHOD_OCR);
	}
	
	public boolean isHtr() {
		return methodCombo.txt().equals(METHOD_HTR);
	}

	public void updateGui() {	
		JobImpl[] htrTrainingJobImpls = {};
		if(Storage.getInstance() != null && Storage.getInstance().isLoggedIn()) {
			try {
				htrTrainingJobImpls = Storage.getInstance().getHtrTrainingJobImpls();
			} catch (SessionExpiredException | ServerErrorException | ClientErrorException e) {
				logger.debug("An exception occurred while querying job acl. Training is off.", e);
			}
		}
		
		setBtnVisibility(htrTrainingJobImpls.length > 0);
	}
	
	private void setBtnVisibility(boolean withTrainBtn) {
		if (Storage.getInstance()!=null) {
			methodCombo.combo.setItems(Storage.getInstance().isAdminLoggedIn() ? METHODS_ADMIN : METHODS);
			methodCombo.combo.select(0);
		}
		
		boolean showTrainBtn = withTrainBtn && isHtr();
		boolean showModelBtn = isHtr();
		
		if (showModelBtn){
			modelsBtn.setParent(this);
			runBtn.moveBelow(modelsBtn);
			//modelsBtn.moveAbove(runBtn);
		}
		else {
			modelsBtn.setParent(SWTUtil.dummyShell);
		}

		if (showTrainBtn) {
			trainBtn.setParent(this);
			runBtn.moveBelow(trainBtn);
		} else {
			trainBtn.setParent(SWTUtil.dummyShell);
		}

		this.layout(true, true);
		this.pack();
		this.redraw();
		super.layout(true, true);
		
		logger.trace("parent: "+getParent());
		getParent().layout();
	}

}
