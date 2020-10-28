package eu.transkribus.swt_gui.credits.admin;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.transkribus.core.model.beans.auth.TrpUser;
import eu.transkribus.swt.util.SWTUtil;
import eu.transkribus.swt_gui.pagination_tables.UserAdminTableWidgetPagination;

public class CreditAdminSashForm extends SashForm {
	
	CreditPackagesUserAdminPagedTableWidget userAdminCreditsTable;
	UserAdminTableWidgetPagination userTable;
	
	public CreditAdminSashForm(Composite parent, int style) {
		super(parent, SWT.HORIZONTAL | style);
		this.setLayout(SWTUtil.createGridLayout(2, false, 0, 0));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group userTableGroup = new Group(this, SWT.BORDER);
		userTableGroup.setLayout(new GridLayout(1, true));
		userTableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		userTableGroup.setText("Users");
		userTable = new UserAdminTableWidgetPagination(userTableGroup, SWT.SINGLE, 25);
		userTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group userCreditGroup = new Group(this, SWT.BORDER);
		userCreditGroup.setLayout(new GridLayout(1, true));
		userCreditGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		userCreditGroup.setText("User Credit Packages");
		
		userAdminCreditsTable = new CreditPackagesUserAdminPagedTableWidget(userCreditGroup, SWT.NONE);
		userAdminCreditsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.setWeights(new int[] { 50, 50 });
		
		new CreditAdminSashFormListener(this);
	}
	
	public void refresh(boolean resetToFirstPage) {
		userTable.refreshPage(resetToFirstPage);
		refreshUserAdminCreditsTable(resetToFirstPage);
	}
	
	void refreshUserAdminCreditsTable(boolean resetToFirstPage) {
		userAdminCreditsTable.setUser(getSelectedUser());
		userAdminCreditsTable.refreshPage(resetToFirstPage);
	}
	
	TrpUser getSelectedUser() {
		List<TrpUser> selection = userTable.getSelected();
		if(selection == null || selection.isEmpty()) {
			return null;
		} else {
			return selection.get(0);
		}
	}
}
