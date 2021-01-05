package eu.transkribus.swt_gui.mainwidget;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TableCellType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpShapeTypeUtils;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.MonitorUtil;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.swt.progress.ProgressBarDialog;
import eu.transkribus.swt.util.DialogUtil;
import eu.transkribus.swt_gui.canvas.CanvasMode;
import eu.transkribus.swt_gui.canvas.editing.ShapeEditOperation;
import eu.transkribus.swt_gui.canvas.shapes.CanvasPolygon;
import eu.transkribus.swt_gui.canvas.shapes.CanvasPolyline;
import eu.transkribus.swt_gui.canvas.shapes.CanvasShapeUtil;
import eu.transkribus.swt_gui.canvas.shapes.ICanvasShape;
import eu.transkribus.swt_gui.dialogs.CopyShapesConfDialog;
import eu.transkribus.swt_gui.dialogs.MergeTextLinesConfDialog;
import eu.transkribus.swt_gui.dialogs.RemoveTextLinesConfDialog;
import eu.transkribus.swt_gui.dialogs.RemoveTextRegionsConfDialog;

public class ShapeEditController extends AMainWidgetController {
	private static final Logger logger = LoggerFactory.getLogger(ShapeEditController.class);
	
	public ShapeEditController(TrpMainWidget mw) {
		super(mw);
	}
	
	public static final double DEFAULT_POLYGON_SIMPLICATION_PERCENTAGE = 1.0d;
	
	public void simplifySelectedLineShapes(boolean onlySelected) {
		simplifySelectedLineShapes(DEFAULT_POLYGON_SIMPLICATION_PERCENTAGE, onlySelected);
	}
	
	public void simplifySelectedLineShapes(double percentage, boolean onlySelected) {
		try {
			logger.debug("simplifying selected line shape");
			canvas.getShapeEditor().simplifyTextLines(percentage, onlySelected);
		} catch (Exception e) {
			TrpMainWidget.getInstance().onError("Error", e.getMessage(), e);
		}
	}
	
	public void createImageSizeTextRegion() {
		try {
			if (!storage.hasTranscript()) {
				return;
			}
			
			Rectangle imgBounds = canvas.getScene().getMainImage().getBounds();
			
			if (CanvasShapeUtil.getFirstTextRegionWithSize(storage.getTranscript().getPage(), 0, 0, imgBounds.width, imgBounds.height, false) != null) {
				DialogUtil.showErrorMessageBox(getShell(), "Error", "Top level region with size of image already exists!");
				return;
			}
			
			CanvasPolygon imgBoundsPoly = new CanvasPolygon(imgBounds);
//			CanvasMode modeBackup = canvas.getMode();
			canvas.setMode(CanvasMode.ADD_TEXTREGION);
			ShapeEditOperation op = canvas.getShapeEditor().addShapeToCanvas(imgBoundsPoly, true);
			canvas.setMode(CanvasMode.SELECTION);
		} catch (Exception e) {
			TrpMainWidget.getInstance().onError("Error", e.getMessage(), e);
		}	
	}

	public void createDefaultLineForSelectedShape() {
		if (canvas.getFirstSelected() == null)
			return;
		
		try {
			logger.debug("creating default line for seected line/baseline!");
			
//			CanvasPolyline baselineShape = (CanvasPolyline) shape;
//			shapeOfParent = baselineShape.getDefaultPolyRectangle();
			
			ICanvasShape shape = canvas.getFirstSelected();
			CanvasPolyline blShape = (CanvasPolyline) CanvasShapeUtil.getBaselineShape(shape);
			if (blShape == null)
				return;
			
			CanvasPolygon pl = blShape.getDefaultPolyRectangle();
			if (pl == null)
				return;
			
			ITrpShapeType st = (ITrpShapeType) shape.getData();
			TrpTextLineType line = TrpShapeTypeUtils.getLine(st);
			if (line != null) {
				ICanvasShape lineShape = (ICanvasShape) line.getData();
				if (lineShape != null) {
					lineShape.setPoints(pl.getPoints());
					
					canvas.redraw();
				}
			}
		} catch (Exception e) {
			TrpMainWidget.getInstance().onError("Error", e.getMessage(), e);
		}	
	}

	public void removeSmallTextRegions(Double fractionOfImageSize) {
		try {
			if (!storage.hasTranscript()) {
				return;
			}
			
			Rectangle imgBounds = canvas.getScene().getMainImage().getBounds();
			double area = imgBounds.width * imgBounds.height;
			if (fractionOfImageSize == null) {
				fractionOfImageSize = DialogUtil.showDoubleInputDialog(getShell(), "Input fraction", "Fraction of area ("+area+")", 0.001);
			}
			logger.debug("fractionOfImageSize = "+fractionOfImageSize);
			if (fractionOfImageSize == null) {
				return;
			}
			
			PageXmlUtils.filterOutSmallRegions(storage.getTranscript().getPageData(), fractionOfImageSize*area);
			
			canvas.redraw();
			mw.reloadCurrentTranscript(true, true, () -> {
				storage.setCurrentTranscriptEdited(true);	
			}, null);
			
//			if (CanvasShapeUtil.getFirstTextRegionWithSize(storage.getTranscript().getPage(), 0, 0, imgBounds.width, imgBounds.height, false) != null) {
//				DialogUtil.showErrorMessageBox(getShell(), "Error", "Top level region with size of image already exists!");
//				return;
//			}
//			
//			CanvasPolygon imgBoundsPoly = new CanvasPolygon(imgBounds);
////			CanvasMode modeBackup = canvas.getMode();
//			canvas.setMode(CanvasMode.ADD_TEXTREGION);
//			ShapeEditOperation op = canvas.getShapeEditor().addShapeToCanvas(imgBoundsPoly, true);
//			canvas.setMode(CanvasMode.SELECTION);
		} catch (Exception e) {
			TrpMainWidget.getInstance().onError("Error", e.getMessage(), e);
		}		
	}
	
	public void removeSmallTextLinesFromLoadedDoc() {
		try {
			logger.debug("removeSmallTextLinesFromLoadedDoc!");

			if (!storage.isDocLoaded()) {
				DialogUtil.showErrorMessageBox(getShell(), "Error", "No document loaded!");
				return;
			}
			
			if (!mw.saveTranscriptDialogOrAutosave()) {
				return;
			}
			
			RemoveTextLinesConfDialog d = new RemoveTextLinesConfDialog(getShell());
			if (d.open() != IDialogConstants.OK_ID) {
				return;
			}
			
			double fractionOfRegionSize = d.getThreshPerc();
			Set<Integer> pageIndices = d.getPageIndices();
			boolean dryRun = d.isDryRun();
			final double threshold = fractionOfRegionSize;
			
			class Result {
				public int nPagesTotal=0;
				public int nPagesChanged=0;
				public int nShapesRemoved=0;
				public String msg;
				public List<Integer> affectedPageIndices=new ArrayList<>();
			}
			final Result res = new Result();

			ProgressBarDialog.open(getShell(), new IRunnableWithProgress() {
				@Override public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						logger.debug("removeSmallTextLinesFromLoadedDoc");
						TrpDoc doc = storage.getDoc();
						int worked=0;				
						
						int N;
						if (d.isApplySelected() && d.isDoCurrentPage()) {						
							pageIndices.clear();
							pageIndices.add(storage.getPageIndex());
							N=1;
						}				
						else {
							N = pageIndices==null ? doc.getNPages() : pageIndices.size();
						}

						res.nPagesTotal = N;
						
						MonitorUtil.beginTask(monitor, "Removing small text lines, threshold = "+threshold, N);

						for (int i=0; i<doc.getNPages(); ++i) {
							if (pageIndices!=null && !pageIndices.contains(i)) {
								continue;
							}
							
							if (MonitorUtil.isCanceled(monitor)) {
								return;
							}
							
							MonitorUtil.subTask(monitor, "Processing page "+(worked+1)+" / "+N);
						
							TrpPage p = doc.getPages().get(i);
							TrpTranscriptMetadata md = p.getCurrentTranscript();
							
							JAXBPageTranscript tr = new JAXBPageTranscript(md);
							tr.build();
							
							List<TrpRegionType> trpShapes = null;
							if (d.isApplySelected() && d.isDoCurrentPage()) {
								//neu: process only selected shapes 
								List<ICanvasShape> shapes = mw.getCanvas().getScene().getSelected();
								if (shapes.isEmpty()) {
									shapes = mw.getCanvas().getScene().getShapes();
								}
								
								trpShapes = new ArrayList<TrpRegionType>();
								for (ICanvasShape shape : shapes) {
									
									ITrpShapeType type = shape.getTrpShapeType();
									//if (!(type instanceof TrpTextLineType) && !(type instanceof TrpWordType)) {
									if (type instanceof TrpTextRegionType || type instanceof TableCellType || type instanceof TableRegionType) {
										//logger.debug("instance of trpregiontype");
										trpShapes.add((TrpRegionType)type);
									}
								}
							}
							
							int nRemoved = PageXmlUtils.filterOutSmallLines(tr.getPageData(), threshold, trpShapes);
							
							res.nShapesRemoved += nRemoved;
							logger.debug("nRemoved = "+nRemoved);
//							PageXmlUtils.filterOutSmallRegions(md.getUrl().toString(), threshold);
							if (nRemoved > 0) {
								++res.nPagesChanged;
								String msg = "Removed "+nRemoved+" lines < "+threshold+" * parent region width";
								
								res.affectedPageIndices.add(i);
								
								if (!dryRun) {
									mw.getStorage().saveTranscript(mw.getStorage().getCurrentDocumentCollectionId(), tr, msg);									
								}
							}
							
							MonitorUtil.worked(monitor, ++worked);
						}
						
						res.msg = "Removed "+res.nShapesRemoved+" lines from "+res.nPagesChanged+"/"+res.nPagesTotal+" pages";
						logger.info(res.msg);
					} catch (Exception e) {
						throw new InvocationTargetException(e, e.getMessage());
					}
				}
			}, "Removing small lines", true);
			
			DialogUtil.showInfoMessageBox(getShell(), "Removed lines", res.msg+"\nPage numbers of affected pages: "+CoreUtils.getRangeListStrFromList(res.affectedPageIndices));
			
			if (!dryRun) {
				mw.reloadCurrentPage(true, null, null);	
			}
		} catch (Throwable e) {
			mw.onError("Error", e.getMessage(), e);
		}		
	}
	
	public void mergeSmallTextLinesFromLoadedDoc() {
		try {
			logger.debug("mergeSmallTextLinesFromLoadedDoc!");

			if (!storage.isDocLoaded()) {
				DialogUtil.showErrorMessageBox(getShell(), "Error", "No document loaded!");
				return;
			}
			
			if (!mw.saveTranscriptDialogOrAutosave()) {
				return;
			}
			
			MergeTextLinesConfDialog d = new MergeTextLinesConfDialog(getShell());
			if (d.open() != IDialogConstants.OK_ID) {
				return;
			}
			
			double horizontalThreshold = d.getThreshPixel();
			Set<Integer> pageIndices = d.getPageIndices();
			boolean dryRun = d.isDryRun();
			final double threshold = horizontalThreshold;
			
			class Result {
				public int nPagesTotal=0;
				public int nLinesTotal=0;
				public int nPagesChanged=0;
				public int nShapesMerged=0;
				public String msg;
				public List<Integer> affectedPageIndices=new ArrayList<>();
			}
			final Result res = new Result();

			ProgressBarDialog.open(getShell(), new IRunnableWithProgress() {
				@Override public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						logger.debug("mergeSmallTextLinesFromLoadedDoc");
						TrpDoc doc = storage.getDoc();
						int worked=0;				
						
						int N;
						if (d.isApplySelected() && d.isDoCurrentPage()) {						
							pageIndices.clear();
							pageIndices.add(storage.getPageIndex());
							N=1;
						}				
						else {
							N = pageIndices==null ? doc.getNPages() : pageIndices.size();
						}

						res.nPagesTotal = N;
						res.nLinesTotal = 0;
						
						MonitorUtil.beginTask(monitor, "Removing small text lines, threshold = "+threshold, N);

						for (int i=0; i<doc.getNPages(); ++i) {
							if (pageIndices!=null && !pageIndices.contains(i)) {
								continue;
							}
							
							if (MonitorUtil.isCanceled(monitor)) {
								return;
							}
							
							MonitorUtil.subTask(monitor, "Processing page "+(worked+1)+" / "+N);
						
							TrpPage p = doc.getPages().get(i);
							TrpTranscriptMetadata md = p.getCurrentTranscript();
							
							JAXBPageTranscript tr = new JAXBPageTranscript(md);
							tr.build();
							
							List<TrpRegionType> trpShapes = null;
							if (d.isApplySelected() && d.isDoCurrentPage()) {
								//neu: process only selected shapes 
								List<ICanvasShape> shapes = mw.getCanvas().getScene().getSelected();
								if (shapes.isEmpty()) {
									shapes = mw.getCanvas().getScene().getShapes();
								}
								
								trpShapes = new ArrayList<TrpRegionType>();
								for (ICanvasShape shape : shapes) {
									
									ITrpShapeType type = shape.getTrpShapeType();
									//if (!(type instanceof TrpTextLineType) && !(type instanceof TrpWordType)) {
									if (type instanceof TrpTextRegionType || type instanceof TableCellType || type instanceof TableRegionType) {
										//logger.debug("instance of trpregiontype");
										trpShapes.add((TrpRegionType)type);
									}
								}
							}
							
							int merged = PageXmlUtils.mergeSmallLines(tr.getPageData(), threshold, trpShapes);
							
							res.nShapesMerged += merged;
							logger.debug("nMerged = "+merged);
//							PageXmlUtils.filterOutSmallRegions(md.getUrl().toString(), threshold);
							if (merged > 0) {
								++res.nPagesChanged;
								String msg = "After merge "+merged+" lines are left";
								
								res.affectedPageIndices.add(i);
								
								if (!dryRun) {
									mw.getStorage().saveTranscript(mw.getStorage().getCurrentDocumentCollectionId(), tr, msg);									
								}
							}
							
							MonitorUtil.worked(monitor, ++worked);
						}
						
						res.msg = "After merging "+res.nShapesMerged+" lines caare left over. "+res.nPagesChanged+"/"+res.nPagesTotal+" pages";
						logger.info(res.msg);
					} catch (Exception e) {
						throw new InvocationTargetException(e, e.getMessage());
					}
				}
			}, "Merging small lines", true);
			
			DialogUtil.showInfoMessageBox(getShell(), "Merging lines", res.msg+"\nPage numbers of affected pages: "+CoreUtils.getRangeListStrFromList(res.affectedPageIndices));
			
			if (!dryRun) {
				mw.reloadCurrentPage(true, null, null);	
			}
		} catch (Throwable e) {
			mw.onError("Error", e.getMessage(), e);
		}		
	}
	
	public void removeSmallTextRegionsFromLoadedDoc() {
		try {
			logger.debug("removeSmallTextRegionsFromLoadedDoc!");

			if (!storage.isDocLoaded()) {
				DialogUtil.showErrorMessageBox(getShell(), "Error", "No document loaded!");
				return;
			}
			
			if (!mw.saveTranscriptDialogOrAutosave()) {
				return;
			}
			
			RemoveTextRegionsConfDialog d = new RemoveTextRegionsConfDialog(getShell());
			if (d.open() != IDialogConstants.OK_ID) {
				return;
			}
			
			double fractionOfImageSize = d.getThreshPerc();
			Set<Integer> pageIndices = d.getPageIndices();
			boolean dryRun = d.isDryRun();

			Rectangle imgBounds = canvas.getScene().getMainImage().getBounds();
			double area = imgBounds.width * imgBounds.height;
			
//			if (fractionOfImageSize == null) {
//				fractionOfImageSize = DialogUtil.showDoubleInputDialog(getShell(), "Input fraction", "Fraction of area ("+area+")", 0.0005);
//			}
//			logger.debug("fractionOfImageSize = "+fractionOfImageSize);
//			if (fractionOfImageSize == null) {
//				return;
//			}
			final double threshold = fractionOfImageSize*area;
			
			class Result {
				public int nPagesTotal=0;
				public int nPagesChanged=0;
				public int nRegionsRemoved=0;
				public String msg;
				public List<Integer> affectedPageIndices=new ArrayList<>();
			}
			final Result res = new Result();

			ProgressBarDialog.open(getShell(), new IRunnableWithProgress() {
				@Override public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						logger.debug("removeSmallTextRegionsFromLoadedDoc");
						TrpDoc doc = storage.getDoc();
						int worked=0;
						int N = pageIndices==null ? doc.getNPages() : pageIndices.size();
						res.nPagesTotal = N;
						
						MonitorUtil.beginTask(monitor, "Removing small text regions, threshold = "+threshold, N);
						for (int i=0; i<doc.getNPages(); ++i) {
							if (pageIndices!=null && !pageIndices.contains(i)) {
								continue;
							}
							
							if (MonitorUtil.isCanceled(monitor)) {
								return;
							}
							MonitorUtil.subTask(monitor, "Processing page "+(worked+1)+" / "+N);
							
							TrpPage p = doc.getPages().get(i);
							TrpTranscriptMetadata md = p.getCurrentTranscript();
							
							JAXBPageTranscript tr = new JAXBPageTranscript(md);
							tr.build();
							
							int nRemoved = PageXmlUtils.filterOutSmallRegions(tr.getPageData(), threshold);
							res.nRegionsRemoved += nRemoved;
							logger.debug("nRemoved = "+nRemoved);
//							PageXmlUtils.filterOutSmallRegions(md.getUrl().toString(), threshold);
							if (nRemoved > 0) {
								++res.nPagesChanged;
								String msg = "Removed "+nRemoved+" text-regions < "+threshold+" pixels";
								
								res.affectedPageIndices.add(i);
								
								if (!dryRun) {
									mw.getStorage().saveTranscript(mw.getStorage().getCurrentDocumentCollectionId(), tr, msg);									
								}
							}
							
							MonitorUtil.worked(monitor, ++worked);
						}
						
						res.msg = "Removed "+res.nRegionsRemoved+" text-regions from "+res.nPagesChanged+"/"+res.nPagesTotal+" pages";
						logger.info(res.msg);
					} catch (Exception e) {
						throw new InvocationTargetException(e, e.getMessage());
					}
				}
			}, "Removing text-regions", true);
			
			DialogUtil.showInfoMessageBox(getShell(), "Removed text-regions", res.msg+"\nAffected pages: "+CoreUtils.getRangeListStrFromList(res.affectedPageIndices));
			
			if (!dryRun) {
				mw.reloadCurrentPage(true, null, null);	
			}
		} catch (Throwable e) {
			mw.onError("Error", e.getMessage(), e);
		}		
	}
	
	public void copyShapesToOtherPagesInLoadedDoc() {
		try {
			logger.debug("copyShapesToOtherPagesInLoadedDoc!");

			if (!storage.isDocLoaded()) {
				DialogUtil.showErrorMessageBox(getShell(), "Error", "No document loaded!");
				return;
			}
			CopyShapesConfDialog d = new CopyShapesConfDialog(getShell());
			if (d.open() != IDialogConstants.OK_ID) {
				return;
			}
			
			Set<Integer> pageIndices = d.getPageIndices();
			boolean dryRun = d.isDryRun();
			
			class Result {
				public int nPagesTotal=0;
				public int nPagesChanged=0;
				public int nShapesCopied=0;
				public String msg;
				public List<Integer> affectedPageIndices=new ArrayList<>();
			}
			final Result res = new Result();

			ProgressBarDialog.open(getShell(), new IRunnableWithProgress() {
				@Override public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						logger.debug("copyShapesToOtherPagesInLoadedDoc");
						TrpDoc doc = storage.getDoc();
						int worked=0;
						int N = pageIndices==null ? doc.getNPages() : pageIndices.size();
						res.nPagesTotal = N;
						
						MonitorUtil.beginTask(monitor, "Copy selected shapes to nr. of pages = ", N);
						List<ICanvasShape> shapes = mw.getCanvas().getScene().getSelected();
						if (shapes.isEmpty()) {
							shapes = mw.getCanvas().getScene().getShapes();
						}
						
						List<TrpRegionType> trpShapes = new ArrayList<TrpRegionType>();
						for (ICanvasShape shape : shapes) {
							
							ITrpShapeType type = shape.getTrpShapeType();
							//if (!(type instanceof TrpTextLineType) && !(type instanceof TrpWordType)) {
							if (type instanceof TrpRegionType && !(type instanceof TableCellType)) {
								logger.debug("instance of trpregiontype");
								trpShapes.add((TrpRegionType)type);
							}
							else if (type instanceof TrpTableRegionType) {
								logger.debug("instance of trpTableRegiontype");
							}

						}
						
						res.nShapesCopied = trpShapes.size();
						
						//the current page from where we want to copy
						int idx = storage.getPageIndex();
						
						TrpPage p = doc.getPages().get(idx);
						TrpTranscriptMetadata md = p.getCurrentTranscript();
						
						JAXBPageTranscript tr = new JAXBPageTranscript(md);
						
						for (int i=0; i<doc.getNPages(); ++i) {

							if (pageIndices!=null && !pageIndices.contains(i) || i==idx) {
								continue;
							}
							
							if (MonitorUtil.isCanceled(monitor)) {
								return;
							}
							MonitorUtil.subTask(monitor, "Processing page "+(worked+1)+" / "+N);
							
							TrpPage currP = doc.getPages().get(i);
							TrpTranscriptMetadata currMd = currP.getCurrentTranscript();
							
							JAXBPageTranscript currTr = new JAXBPageTranscript(currMd);
							currTr.build();
							
							logger.debug("currTr: " + currTr.getPage());

							if (!trpShapes.isEmpty()) {
								PcGtsType pc = currTr.getPageData();
								if (pc == null) {
									logger.debug("pc is null");
									continue;
								}
								pc = PageXmlUtils.copyShapes(pc, trpShapes);
								currTr.setPageData(pc);
									
								if (!dryRun) {
									mw.getStorage().saveTranscript(mw.getStorage().getCurrentDocumentCollectionId(), currTr, "copied regions");	
									
								}
								res.nPagesChanged++;
								
							}
							MonitorUtil.worked(monitor, ++worked);
						}
						
						res.msg = !dryRun ? "Copied "+res.nShapesCopied+" shape(s) to "+ res.nPagesChanged+" page(s)" : res.nShapesCopied+" shape(s) would be copied to "+ res.nPagesChanged+" page(s)." ;
						logger.info(res.msg);
					} catch (Exception e) {
						throw new InvocationTargetException(e, e.getMessage());
					}
				}
			}, "Copying shapes", true);
			
			DialogUtil.showInfoMessageBox(getShell(), "Copying shapes", res.msg);
			
			if (!dryRun) {
				mw.reloadCurrentPage(true, null, null);	
			}
		} catch (Throwable e) {
			mw.onError("Error", e.getMessage(), e);
		}		
	}

	public void rectifyAllRegions() {
		try {
			if (!storage.hasTranscript()) {
				return;
			}
			
			logger.debug("rectifyAllRegions");
			
			for (ICanvasShape s : mw.getCanvas().getScene().getShapes()) {
				ITrpShapeType st = CanvasShapeUtil.getTrpShapeType(s);
				if (st instanceof RegionType) {
					s.setPoints(s.getBoundsPoints());
				}
			}
			mw.getCanvas().redraw();
			storage.setCurrentTranscriptEdited(true);
		} catch (Exception e) {
			TrpMainWidget.getInstance().onError("Error", e.getMessage(), e);
		}
	}

}
