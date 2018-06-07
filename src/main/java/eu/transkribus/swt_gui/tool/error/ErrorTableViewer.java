package eu.transkribus.swt_gui.tool.error;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import eu.transkribus.core.model.beans.TrpErrorList;
import eu.transkribus.core.model.beans.TrpErrorRate;
import eu.transkribus.swt.mytableviewer.ColumnConfig;
import eu.transkribus.swt.mytableviewer.MyTableViewer;

public class ErrorTableViewer extends MyTableViewer {

	public ErrorTableViewer(Composite parent, int style) {
		super(parent, style);
		this.table = getTable();
		this.addColumns(ERR_COLS);
	}
	
	public static final String ERR_EMPTY_COL = " ";
	public static final String ERR_WORD_COL = "Error Rate Word";
	public static final String ERR_CHAR_COL = "Error Rate Char";
	public static final String ACC_WORD_COL = "Accuracy Word";
	public static final String ACC_CHAR_COL = "Accuracy Char";
	public static final String BAG_PREC_COL = "Bag Tokens Precision";
	public static final String BAG_REC_COL = "Bag Tokens Recall";
	public static final String BAG_FMEA_COL = "Bag Tokens F-Measure";
	
	public final ColumnConfig[] ERR_COLS = new ColumnConfig[] { new ColumnConfig(ERR_EMPTY_COL, 100),
			new ColumnConfig(ERR_WORD_COL, 150), new ColumnConfig(ERR_CHAR_COL, 150), 
			new ColumnConfig(ACC_WORD_COL, 150), new ColumnConfig(ACC_CHAR_COL, 150),
			new ColumnConfig(BAG_PREC_COL, 150), new ColumnConfig(BAG_REC_COL, 150),
			new ColumnConfig(BAG_FMEA_COL, 150), };
	
	public void setEntriesList(Table table, TrpErrorRate rate) {
		for (TrpErrorList p : rate.getList()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { "Page "+p.getPageNumber(), p.getWer(), p.getCer(),p.getwAcc(),p.getcAcc(),p.getBagTokensPrec(),p.getBagTokensRec(),p.getBagTokensF()});
		}	
	}
	


}