package eu.transkribus.swt_gui.credits.admin;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;

import eu.transkribus.client.util.SessionExpiredException;
import eu.transkribus.client.util.TrpClientErrorException;
import eu.transkribus.client.util.TrpServerErrorException;
import eu.transkribus.core.model.beans.TrpCreditPackage;
import eu.transkribus.swt.util.DialogUtil;
import eu.transkribus.swt.util.SWTUtil;
import eu.transkribus.swt_gui.mainwidget.storage.Storage;

public class CreditAdminSashFormListener {
	
	private CreditAdminSashForm view;
	private Storage store;

	public CreditAdminSashFormListener(CreditAdminSashForm view) {
		this.view = view;
		this.store = Storage.getInstance();
		view.userTable.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				BusyIndicator.showWhile(view.getShell().getDisplay(), new Runnable() {
					@Override
					public void run() {
						view.refreshUserAdminCreditsTable(true);
					}
				});
			}
		});
		
		SWTUtil.onSelectionEvent(view.userAdminCreditsTable.getCreatePackageBtn(), (e) -> {
			openCreatePackageDialog();
		});
	}

	private void openCreatePackageDialog() {
		CreateCreditPackageDialog d = new CreateCreditPackageDialog(view.getShell(), view.getSelectedUser());
		if (d.open() == IDialogConstants.OK_ID) {
			TrpCreditPackage newPackage = d.getPackageToCreate();
			try {
				TrpCreditPackage createdPackage = store.getConnection().getCreditCalls().createCredit(newPackage);
				DialogUtil.showInfoBalloonToolTip(view.userAdminCreditsTable.getCreatePackageBtn(), "Done",
						"Package created: '" + createdPackage.getProduct().getLabel() + "'" + "\nOwner: "
								+ createdPackage.getUserName());
				view.userAdminCreditsTable.refreshPage(false);
			} catch (TrpServerErrorException | TrpClientErrorException | SessionExpiredException e1) {
				DialogUtil.showErrorMessageBox2(view.getShell(), "Error", "Package could not be created.", e1);
			}
		}
	}
}
