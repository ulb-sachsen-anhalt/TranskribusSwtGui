package eu.transkribus.swt_gui.pagination_tables;

import java.util.ArrayList;

import javax.ws.rs.ServerErrorException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.client.util.SessionExpiredException;
import eu.transkribus.core.model.beans.auth.TrpUser;
import eu.transkribus.core.model.beans.rest.TrpUserList;
import eu.transkribus.swt.pagination_table.ATableWidgetPagination;
import eu.transkribus.swt.pagination_table.IPageLoadMethod;
import eu.transkribus.swt.pagination_table.RemotePageLoaderSingleRequest;
import eu.transkribus.swt.util.TrpViewerFilterWidget;
import eu.transkribus.swt_gui.mainwidget.TrpMainWidget;
import eu.transkribus.swt_gui.mainwidget.storage.Storage;

public class UserAdminTableWidgetPagination extends ATableWidgetPagination<TrpUser> {
	private final static Logger logger = LoggerFactory.getLogger(UserAdminTableWidgetPagination.class);
	
	public static final String USER_USERNAME_COL = "Username";
	public static final String USER_FIRSTNAME_COL = "Firstname";
	public static final String USER_LASTNAME_COL = "Lastname";
	public static final String USER_CREATE_TIME_COL = "Created";
	
	private TrpViewerFilterWidget filterComposite;
	
	public UserAdminTableWidgetPagination(Composite parent, int style, int initialPageSize) {
		super(parent, style, initialPageSize);
		filterComposite = createFilterWidget();
	}
	
	protected TrpViewerFilterWidget createFilterWidget() {
		TrpViewerFilterWidget filterComposite = new TrpViewerFilterWidget(this, getTableViewer(), SWT.NONE, TrpUser.class, "email") {
			@Override
			protected void refreshViewer() {
				logger.debug("refreshing viewer...");
				refreshPage(true);
			}
			@Override
			protected void attachFilter() {
			}
		};
		filterComposite.getFilterText().setMessage("Email");
		filterComposite.getFilterText().setToolTipText("Filter users by email address");
		filterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filterComposite.setLayout(new GridLayout(2, false));
		filterComposite.moveAbove(null);
		return filterComposite;
	}
	
	protected void setPageLoader() {
		IPageLoadMethod<TrpUserList, TrpUser> plm = new IPageLoadMethod<TrpUserList, TrpUser>() {

			@Override
			public TrpUserList loadPage(int fromIndex, int toIndex, String sortPropertyName,
					String sortDirection) {
				String username = filterComposite.getFilterText().getText();
				if("".equals(username)) {
					username = null;
				}
				Storage store = Storage.getInstance();
				if (!store.isAdminLoggedIn()) {
					return new TrpUserList(new ArrayList<>(), 0, 0, 0, null, null);	
				}
				try {
					return store.getConnection().getUserList(username, null, null, false, false, fromIndex, toIndex-fromIndex, sortPropertyName, sortDirection);
				} catch (SessionExpiredException | ServerErrorException | IllegalArgumentException e) {
					TrpMainWidget.getInstance().onError("Error loading documents", e.getMessage(), e);
				}
				return new TrpUserList(new ArrayList<>(), 0, 0, 0, null, null);
			}
		};
		pageableTable.setPageLoader(new RemotePageLoaderSingleRequest<>(pageableTable.getController(), plm));
	}

	protected void createColumns() {		
		createDefaultColumn(USER_USERNAME_COL, 100, "userName", true);
		createDefaultColumn(USER_FIRSTNAME_COL, 100, "firstname", true);
		createDefaultColumn(USER_LASTNAME_COL, 100, "lastname", true);
		createDefaultColumn(USER_CREATE_TIME_COL, 100, "created", true);
	}
}
