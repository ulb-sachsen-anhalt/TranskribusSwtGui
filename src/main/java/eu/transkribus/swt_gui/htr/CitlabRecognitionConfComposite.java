package eu.transkribus.swt_gui.htr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import eu.transkribus.swt.util.SWTUtil;
import eu.transkribus.swt.util.ToolBox;
import eu.transkribus.swt_gui.mainwidget.storage.Storage;
import eu.transkribus.swt_gui.metadata.StructCustomTagSpec;

public class CitlabRecognitionConfComposite extends Composite {
	
	Button doLinePolygonSimplificationBtn, keepOriginalLinePolygonsBtn, doStoreConfMatsBtn;
	ToolBar structureBar;
	ToolItem structureItem;
	ToolBox structureBox;
	List<String> selectionArray = new ArrayList<>();

	public CitlabRecognitionConfComposite(Composite parent) {
		super(parent, 0);
		this.setLayout(SWTUtil.createGridLayout(1, false, 0, 0));
		
		doLinePolygonSimplificationBtn = new Button(this, SWT.CHECK);
		doLinePolygonSimplificationBtn.setText("Do polygon simplification");
		doLinePolygonSimplificationBtn.setToolTipText("Perform a line polygon simplification after the recognition process to reduce the number of points");
		doLinePolygonSimplificationBtn.setSelection(true);
		doLinePolygonSimplificationBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		keepOriginalLinePolygonsBtn = new Button(this, SWT.CHECK);
		keepOriginalLinePolygonsBtn.setText("Keep original line polygons");
		keepOriginalLinePolygonsBtn.setToolTipText("Keep the original line polygons after the recognition process, e.g. if they have been already corrected");
		keepOriginalLinePolygonsBtn.setSelection(false);
		keepOriginalLinePolygonsBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		doStoreConfMatsBtn = new Button(this, SWT.CHECK);
		doStoreConfMatsBtn.setText("Enable Keyword Spotting");
		doStoreConfMatsBtn.setToolTipText("The internal recognition result respresentation, needed for keyword spotting, will be stored in addition to the transcription.");
		doStoreConfMatsBtn.setSelection(true);
		doStoreConfMatsBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		SWTUtil.onSelectionEvent(keepOriginalLinePolygonsBtn, e -> {
			doLinePolygonSimplificationBtn.setEnabled(!keepOriginalLinePolygonsBtn.getSelection());
		});
		doLinePolygonSimplificationBtn.setEnabled(!keepOriginalLinePolygonsBtn.getSelection());		
		
		List<StructCustomTagSpec> tags = new ArrayList<>();
		tags = Storage.getInstance().getStructCustomTagSpecs();
		
//		multiCombo = new MultiCheckSelectionCombo(cont, SWT.FILL,"Restrict on structure tags", 1, 200, 300 );
//		multiCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));		
//		
//		for(StructCustomTagSpec tag : tags) {
//			int itemCount = multiCombo.getItemCount();
//			List<String> items = new ArrayList<>();
//			for(int i = 0; i < itemCount; i++) {
//				items.add(multiCombo.getItem(i));
//			}	
//			if(!items.contains(tag.getCustomTag().getType())) {
//				multiCombo.add(tag.getCustomTag().getType());
//			}	
//		}
		
		structureBar = new ToolBar(this, SWT.FILL);
		
		structureItem = new ToolItem(structureBar, SWT.CHECK);
		structureItem.setToolTipText("Choose structures...");
		structureItem.setText("Restrict on structure tags");
		
		structureBox = new ToolBox(getShell(), true, "Structures");
		structureBox.addTriggerWidget(structureItem);
		
		for(StructCustomTagSpec tag : tags) {
			Button button = structureBox.addButton(tag.getCustomTag().getType(), null, SWT.CHECK);
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if(selectionArray.contains(tag.getCustomTag().getType())) {
						selectionArray.remove(tag.getCustomTag().getType());
					}else {
						selectionArray.add(tag.getCustomTag().getType());
					}
			      }
			});
			
		}		
		
	}

}
