package eu.transkribus.swt_gui.pagination_tables;

import java.util.ArrayList;

import javax.ws.rs.ServerErrorException;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.client.util.SessionExpiredException;
import eu.transkribus.core.model.beans.TrpCollection;
import eu.transkribus.core.model.beans.TrpCreditPackage;
import eu.transkribus.core.model.beans.rest.TrpCreditPackageList;
import eu.transkribus.swt.pagination_table.IPageLoadMethod;
import eu.transkribus.swt.pagination_table.RemotePageLoaderSingleRequest;
import eu.transkribus.swt_gui.mainwidget.TrpMainWidget;
import eu.transkribus.swt_gui.mainwidget.storage.Storage;

public class CreditPackagesCollectionPagedTableWidget extends CreditPackagesUserPagedTableWidget {
	
	TrpCollection collection;
	
	public CreditPackagesCollectionPagedTableWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	public void setCollection(TrpCollection collection) {
		this.collection = collection;
	}

	public TrpCollection getCollection() {
		return collection;
	}
	
	@Override
	protected RemotePageLoaderSingleRequest<TrpCreditPackageList, TrpCreditPackage> createPageLoader() {
		IPageLoadMethod<TrpCreditPackageList, TrpCreditPackage> plm = new IPageLoadMethod<TrpCreditPackageList, TrpCreditPackage>() {

			@Override
			public TrpCreditPackageList loadPage(int fromIndex, int toIndex, String sortPropertyName,
					String sortDirection) {
				Storage store = Storage.getInstance();
				if (store.isLoggedIn() && collection != null) {
					try {
						return store.getConnection().getCreditCalls().getCreditPackagesByCollection(collection.getColId(), fromIndex, toIndex-fromIndex, sortPropertyName, sortDirection);
					} catch (SessionExpiredException | ServerErrorException | IllegalArgumentException e) {
						TrpMainWidget.getInstance().onError("Error loading Credit Packages", e.getMessage(), e);
					}
				}
				return new TrpCreditPackageList(new ArrayList<>(), 0.0d, 0, 0, 0, null, null);
			}
		};
		return new RemotePageLoaderSingleRequest<>(pageableTable.getController(), plm);
	}
	
	@Override
	protected void createColumns() {
		createColumn(PACKAGE_NAME_COL, 220, "label", new PackageColumnLabelProvider(p -> p.getProduct().getLabel()));
		createColumn(PACKAGE_BALANCE_COL, 80, "balance", new PackageColumnLabelProvider(p -> "" + p.getBalance()));
		createColumn(PACKAGE_USER_NAME_COL, 120, "userName", new PackageColumnLabelProvider(p -> p.getUserName()));
		//for now we don't need the userid
//		createDefaultColumn(PACKAGE_USER_ID_COL, 50, "userId", new PackageColumnLabelProvider(p -> "" + p.getUserId()));
		createColumn(PACKAGE_SHAREABLE_COL, 70, "shareable", new PackageColumnLabelProvider(p -> "" + p.getProduct().getShareable()));
		createColumn(PACKAGE_DATE_COL, 120, "purchaseDate", new PackageColumnLabelProvider(p -> "" + p.getPurchaseDate()));
		//hide credit type as the value is currently not used anyway
//		createColumn(PACKAGE_TYPE_COL, 100, "creditType", new PackageColumnLabelProvider(p -> "" + p.getCreditType()));
		createColumn(PACKAGE_EXPIRATION_DATE_COL, 120, "expirationDate", new PackageColumnLabelProvider(p -> "" + p.getExpirationDate()));
		createColumn(PACKAGE_ID_COL, 50, "packageId", new PackageColumnLabelProvider(p -> "" + p.getPackageId()));
	}
}