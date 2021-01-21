package eu.transkribus.swt_gui.mainwidget.storage;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dea.fimgstoreclient.beans.ImgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itextpdf.text.DocumentException;

import eu.transkribus.core.io.ExportFilePatternUtils;
import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.LocalDocWriter;
import eu.transkribus.core.misc.APassthroughObservable;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent.MetadataType;
import eu.transkribus.core.model.beans.pagecontent.TranskribusMetadataType;
import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.model.builder.alto.AltoExporter;
import eu.transkribus.core.model.builder.pdf.PdfExporter;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.PageXmlUtils;

/**
 * 
 * Custom local document export to save TrpDocs in PAGE format
 * see: transkribus-core
 * 	eu/transkribus/core/io/DocExporter.java
 * 
 * @author M3ssman
 *
 */
public class ULBLocalDocExporter extends APassthroughObservable {

	private final static Logger LOGGER = LoggerFactory.getLogger(ULBLocalDocExporter.class);

	private final ExportCache cache;
	static DocumentBuilder dBuilder;
	private final AltoExporter altoEx;
	protected CommonExportPars pars;
	protected OutputDirStructure outputDir;
	
	public ULBLocalDocExporter() {
		this.cache = new ExportCache();
		altoEx = new AltoExporter();
	}
	
	public void writePDF(final TrpDoc doc, final String path, Set<Integer> pageIndices, final boolean addTextPages, final boolean imagesOnly, final boolean highlightTags, final boolean highlightArticles, final boolean wordBased, final boolean doBlackening, boolean createTitle, ExportCache cache, final String font, final ImgType pdfImgType) throws MalformedURLException, DocumentException, IOException, JAXBException, URISyntaxException, InterruptedException{
		PdfExporter pdfWriter = new PdfExporter();
		pdfWriter.export(doc, path, pageIndices, wordBased, addTextPages, imagesOnly, highlightTags, highlightArticles, doBlackening, createTitle, cache, font, pdfImgType);
	}
	
	/**
	 * Export *only* PAGE or ALTO from current document.
	 * 
	 * @param doc current document
	 * @param pars export settings 
	 * @return directory to which the export files were written 
	 * @throws IOException
	 */
	public File exportDoc(TrpDoc doc, CommonExportPars pars) throws IOException {

		//create copy of object, as we alter it here while exporting
		TrpDoc doc2 = new TrpDoc(doc);
		this.init(pars);
		List<TrpPage> pages = doc2.getPages();
		Set<Integer> pageIndices = pars.getPageIndices(doc.getNPages());
		// do export for all defined pages
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i)) {
				continue;
			}
			TrpPage exportedPage = exportPage(pages.get(i));
			pages.set(i, exportedPage);
		}
		
		return outputDir.getRootOutputDir();
	}
	
	/**
	 * Set output directories according to parameters and create them.
	 * 
	 * @param pars
	 * @throws IOException
	 */
	public void init(CommonExportPars pars) throws IOException {
		this.pars = pars;
		// check and create output directory
		File rootOutputDir = new File(pars.getDir());
		if (!pars.isDoOverwrite() && rootOutputDir.exists()) {
			throw new IOException("File path already exists.");
		}
		rootOutputDir.mkdir();
		
		//decide where to put the images
		final File imgOutputDir;
		if (pars.isUseOcrMasterDir()) {
			imgOutputDir = new File(rootOutputDir.getAbsolutePath(), LocalDocConst.OCR_MASTER_DIR);
			imgOutputDir.mkdir();
		} else {
			imgOutputDir = rootOutputDir;
		}
		
		File pageOutputDir = null, altoOutputDir = null;
		
		// check PAGE export settings and create output directory
		String pageDirName = pars.getPageDirName();
		if (pars.isDoExportPageXml() && !StringUtils.isEmpty(pageDirName)) {
			pageOutputDir = new File(rootOutputDir.getAbsolutePath() + File.separatorChar + pageDirName);
			if (pageOutputDir.mkdir()){
		        LOGGER.debug("pageOutputDir created successfully ");
			}
			else{
		        LOGGER.debug("pageOutputDir could not be created!");
			}
		} else {
			//if pageDirName is not set, export the PAGE XMLs to imgOutputDir
			pageOutputDir = imgOutputDir;
		}
		// check Alto export settings and create output directory
		if (pars.isDoExportAltoXml()){
			altoOutputDir = altoEx.createAltoOuputDir(rootOutputDir.getAbsolutePath());
		}
		outputDir = new OutputDirStructure(rootOutputDir, imgOutputDir, pageOutputDir, altoOutputDir);
	}

	
	/**
	 * Exports a single TrpPage object to disk according to the CommonExportPars given to the {@link #init(CommonExportPars)} method.
	 * @param page
	 * @return
	 * @throws IOException
	 */
	private TrpPage exportPage(TrpPage page) throws IOException {
		if(pars == null || outputDir == null) {
			throw new IllegalStateException("Export parameters are not set or output directory has not been initialized!");
		}
		//create copy of TrpPage to not mess with the original object
		TrpPage pageExport = new TrpPage(page);
		
		File imgFile = null; 
		File xmlFile = null, altoFile = null;
		
		URL imgUrl = pageExport.getUrl(); 
		
		String baseFileName;
		String imgExt = "." + FilenameUtils.getExtension(pageExport.getImgFileName());
		String xmlExt = ".xml";
		
		// gather remote files and export document
		if (!pageExport.isLocalFile()) {
			//use export filename pattern for remote files
			baseFileName = ExportFilePatternUtils.buildBaseFileName(pars.getFileNamePattern(), pageExport);
			
			if (pars.isDoWriteImages()) {
				final String msg = "Storing " + pars.getRemoteImgQuality().toString() + " image for page nr. " + pageExport.getPageNr();
			        LOGGER.debug(msg);
				updateStatus(msg);
				
				/**
				 * FIXME test if the ImgFileName can be set here. If a filename pattern is set (e.g. in HTR) the value contained is wrong.
				 */
				//page.setImgFileName(imgFile.getName());
				pageExport.setKey(null);
			}
			if(pars.isDoExportPageXml()) {
				TrpTranscriptMetadata transcriptMd;
				JAXBPageTranscript transcript = cache.getPageTranscriptAtIndex(pageExport.getPageNr()-1);
				
				// set up transcript metadata
				if(transcript == null) {
					transcriptMd = pageExport.getCurrentTranscript();
				        LOGGER.warn("Have to unmarshall transcript in DocExporter for transcript "+transcriptMd+" - should have been built before using ExportUtils::storePageTranscripts4Export!");
					transcript = new JAXBPageTranscript(transcriptMd);
					transcript.build();
				} else {
					transcriptMd = transcript.getMd();
				}
				
				//fix the image file name attribute in the Page element in case there was another name set for the export
				transcript.getPageData().getPage().setImageFilename(baseFileName + imgExt);
				if (pars.isUpdatePageXmlImageDimensions()) {
					Dimension dim = ImgUtils.readImageDimensions(FileUtils.toFile(pageExport.getUrl()));
					if (dim != null) {
					        LOGGER.debug("Updating image dimensions in PAGE-XML: "+dim);
						transcript.getPageData().getPage().setImageWidth(dim.width);
						transcript.getPageData().getPage().setImageHeight(dim.height);
					}
				}
				
				URL xmlUrl = transcriptMd.getUrl();
				
				if (pars.isExportTranscriptMetadata()) {
					MetadataType md = transcript.getPage().getPcGtsType().getMetadata();
					if (md == null) {
						md = new MetadataType();
						transcript.getPage().getPcGtsType().setMetadata(md);
					}
					
					String imgUrlStr = CoreUtils.urlToString(imgUrl);
					String xmlUrlStr = CoreUtils.urlToString(xmlUrl);
					String status = transcriptMd.getStatus() == null ? null : transcriptMd.getStatus().toString();

					TranskribusMetadataType tmd = new TranskribusMetadataType();
					tmd.setDocId(pageExport.getDocId());
					tmd.setPageId(pageExport.getPageId());
					tmd.setPageNr(pageExport.getPageNr());
					tmd.setTsid(transcriptMd.getTsId());
					tmd.setStatus(status);
					tmd.setUserId(transcriptMd.getUserId());
					tmd.setImgUrl(imgUrlStr);
					tmd.setXmlUrl(xmlUrlStr);
					tmd.setImageId(pageExport.getImageId());
					md.setTranskribusMetadata(tmd);
				}
				
				// write transcript to file
				xmlFile = new File(FilenameUtils.normalizeNoEndSeparator(outputDir.getPageOutputDir().getAbsolutePath()) 
							+ File.separator + baseFileName + xmlExt);
		        LOGGER.debug("PAGE XMl output file: "+xmlFile.getAbsolutePath());
				transcript.write(xmlFile);

				// old code: save file by just downloading to disk
//				xmlFile = getter.saveFile(transcriptMd.getUrl().toURI(), pageOutputDir.getAbsolutePath(), baseFileName + xmlExt);
				
				// make sure (for other exports) that the transcript that is exported is the only one set in the transcripts list of TrpPage
				pageExport.getTranscripts().clear();
				TrpTranscriptMetadata tCopy = new TrpTranscriptMetadata(transcriptMd, pageExport);
				tCopy.setUrl(xmlFile.toURI().toURL());
				pageExport.getTranscripts().add(tCopy);
			}
		} else {
			updateStatus("Copying local files for page nr. " + pageExport.getPageNr());
			//ignore export filename pattern for local files
			baseFileName = FilenameUtils.getBaseName(pageExport.getImgFileName());
			// copy local files during export
			if (pars.isDoWriteImages()) {
				// ULB Start
				imgFile = writeImage(pageExport.getUrl(), baseFileName + imgExt);
				LOGGER.warn("write Image disabled!");
				// ULB End
			}
			
			if(pars.isDoExportPageXml()) {
				xmlFile = copyTranscriptFile(pageExport, 
						outputDir.getPageOutputDir().getAbsolutePath(), baseFileName + xmlExt);
			}
		}
		// export alto:
		if (pars.isDoExportAltoXml()) {
			altoFile = altoEx.exportAltoFile(pageExport, baseFileName + xmlExt, outputDir.getAltoOutputDir(), pars.isSplitIntoWordsInAltoXml(), pars.isWriteTextOnWordLevel());
		}
		
		/**
		 * FIXME please resolve parent of image file in places where this URL is used as all exported pages miss the image URL which is 
		 * needed for processing exported documents.
		 * to find the output dir later on during the mets creation 
		 */
		if (imgFile != null)
		        LOGGER.debug("Written image file " + imgFile.getAbsolutePath());
		
		if (xmlFile != null) {
		        LOGGER.debug("Written transcript xml file " + xmlFile.getAbsolutePath());
		} else {
		        LOGGER.warn("No transcript was exported for page ");
		}
		if (altoFile != null) {
		        LOGGER.debug("Written ALTO xml file " + altoFile.getAbsolutePath());
		} else {
		        LOGGER.warn("No alto was exported for page ");
		}
		
		this.setChanged();
		this.notifyObservers(Integer.valueOf(pageExport.getPageNr()));
		return pageExport;
	}
	
	/**
	 * Store a local transcript file in another location.
	 * Code from LocalDocWriter and adapted to apply DocExporter's PAGE XML preprocessing.
	 * 
	 * @param p
	 * @param path
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private File copyTranscriptFile(TrpPage p, String path, String fileName) throws IOException {
		if (p.getTranscripts().isEmpty()) {
			return null;
		}
		TrpTranscriptMetadata tmd = p.getTranscripts().get(p.getTranscripts().size()-1);

		JAXBPageTranscript tr = cache.getPageTranscriptAtIndex(p.getPageNr()-1);
					
		if (tr == null){
			tr = new JAXBPageTranscript(tmd);
			tr.build();
		}
		
		File xmlFile = new File(path, fileName);
		try {
			PageXmlUtils.marshalToFile(tr.getPageData(), xmlFile);
		} catch (JAXBException e) {
			throw new IOException("Could not write PAGE XML file.", e);
		}
		return xmlFile;
	}

	/**
	 * Copy local image file at URL to outFilename according to {@link #pars} and {@link #outputDir}.
	 * 
	 * @param url locator of the local image
	 * @param outFilename the name of the target file
	 * @return target file
	 * @throws IOException
	 */
	private File writeImage(URL url, String outFilename) throws IOException {
		if(url.getProtocol().startsWith("http")) {
			//this is only used on local docs right now
			throw new IllegalArgumentException("Only local URLs allowed, but http(s) URL was passed: " + url);
		}
		return LocalDocWriter.copyImgFile(url, outputDir.getImgOutputDir().getAbsolutePath(), outFilename);
	}

	protected static class OutputDirStructure {
		final File rootOutputDir, imgOutputDir, pageOutputDir, altoOutputDir;
		
		public OutputDirStructure(File rootOutputDir, File imgOutputDir, File pageOutputDir, File altoOutputDir) {
			this.rootOutputDir = rootOutputDir;
			this.imgOutputDir = imgOutputDir;
			this.pageOutputDir = pageOutputDir;
			this.altoOutputDir = altoOutputDir;
		}

		public File getRootOutputDir() {
			return rootOutputDir;
		}
		
		public File getImgOutputDir() {
			return imgOutputDir;
		}

		public File getPageOutputDir() {
			return pageOutputDir;
		}

		public File getAltoOutputDir() {
			return altoOutputDir;
		}
	}
}
