package eu.transkribus.swt.util;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import eu.transkribus.core.util.ImgUtils;

public class Images {
	
	public static final Image USER_EDIT = Images.getOrLoad("/icons/user_edit.png");
	
	public static final Image READING_ORDER = Images.getOrLoad("/icons/readingOrder.png");
	public static final Image READING_ORDER_REGIONS = Images.getOrLoad("/icons/reading_order_r.png");
	public static final Image READING_ORDER_LINES = Images.getOrLoad("/icons/reading_order_l.png");
	public static final Image READING_ORDER_WORDS = Images.getOrLoad("/icons/reading_order_w.png");
	
	public static final Image DISCONNECT = Images.getOrLoad("/icons/disconnect.png");
	public static final Image CONNECT = Images.getOrLoad("/icons/connect.png");
	public static final Image HELP = Images.getOrLoad("/icons/help.png");
	public static final Image FOLDER_IMPORT = Images.getOrLoad("/icons/folder_import.png");
	public static final Image FOLDER_GO = Images.getOrLoad("/icons/folder_go.png");
	
	public static final Image START = Images.getOrLoad("/icons/start_16.png");
	
	public static final Image FOLDER = Images.getOrLoad("/icons/folder.png");
	public static final Image FOLDER_DELETE = Images.getOrLoad("/icons/folder_delete.png");
	public static final Image FOLDER_WRENCH = Images.getOrLoad("/icons/folder_wrench.png");
	
	public static final Image IMAGES = Images.getOrLoad("/icons/images.png");
	public static final Image IMAGE = Images.getOrLoad("/icons/image.png");
	
	public static final Image PAGE_WHITE_STACK = Images.getOrLoad("/icons/page_white_stack.png");
	public static final Image CUP = Images.getOrLoad("/icons/cup.png");
	public static final Image KEYBOARD = Images.getOrLoad("/icons/keyboard.png");
	public static final Image SCISSOR = Images.getOrLoad("/icons/scissor.png");
	public static final Image BUG = Images.getOrLoad("/icons/bug.png");
	public static final Image BURGER = Images.getOrLoad("/icons/burger_16.png");
	public static final Image EYE = Images.getOrLoad("/icons/eye.png");
	public static final Image LOCK = getOrLoad("/icons/lock.png");
	public static final Image LOCK_OPEN = getOrLoad("/icons/lock_open.png");
	public static final Image DELETE = getOrLoad("/icons/delete.png");
	public static final Image CROSS = getOrLoad("/icons/cross.png");
	public static final Image ADD = Images.getOrLoad("/icons/add.png");
	public static final Image APPLICATION_DOUBLE = Images.getOrLoad("/icons/application_double.png");
	public static final Image LOADING_IMG = Images.getOrLoad("/icons/loading.gif");
	public static final Image ERROR_IMG = Images.getOrLoad("/icons/broken_image.png");
	public static final Image COMMENT = Images.getOrLoad("/icons/comment.png");
	public static final Image CONTROL_EQUALIZER = Images.getOrLoad("/icons/control_equalizer.png");
	
	public static final Image TEXT_FIELD_DELETE = getOrLoad("/icons/textfield_delete.png");
	
	public static final Image DISK1 = Images.getOrLoad("/icons/disk.png");

	public static final Image IMAGE_EDIT = Images.getOrLoad("/icons/image_edit.png");
	public static final Image IMAGE_DELETE = Images.getOrLoad("/icons/image_delete.png");

	public static final Image CONTRAST = Images.getOrLoad("/icons/contrast.png");

	public static final Image APPLICATION = getOrLoad("/icons/application.png");
	public static final Image APPLICATION_SIDE_CONTRACT = getOrLoad("/icons/application_side_contract.png");
	public static final Image APPLICATION_SIDE_EXPAND = getOrLoad("/icons/application_side_expand.png");
	public static final Image APPLICATION_SIDE_PUT = getOrLoad("/icons/application_put.png");
	public static final Image REFRESH = getOrLoad("/icons/refresh.png");

	public static final Image ARROW_UP = getOrLoad("/icons/arrow_up.png");
	public static final Image ARROW_DOWN = getOrLoad("/icons/arrow_down.png");
	public static final Image ARROW_LEFT = getOrLoad("/icons/arrow_left.png");
	public static final Image ARROW_RIGHT = getOrLoad("/icons/arrow_right.png");
	public static final Image ARROW_UNDO = getOrLoad("/icons/arrow_undo.png");
	public static final Image ARROW_REDO = getOrLoad("/icons/arrow_redo.png");
	
	public static final Image BORDER_NONE = getOrLoad("/icons/border_none.png");
	public static final Image BORDER_ALL = getOrLoad("/icons/border_all.png");
	public static final Image BORDER_CLOSED = getOrLoad("/icons/border_closed.png");
	
	public static final Image BORDER_LEFT = getOrLoad("/icons/border_left.png");
	public static final Image BORDER_RIGHT = getOrLoad("/icons/border_right.png");
	public static final Image BORDER_LEFT_RIGHT = getOrLoad("/icons/border_left_right.png");
	
	public static final Image BORDER_BOTTOM = getOrLoad("/icons/border_bottom.png");
	public static final Image BORDER_TOP = getOrLoad("/icons/border_top.png");
	public static final Image BORDER_BOTTOM_TOP = getOrLoad("/icons/border_bottom_top.png");
	
	public static final Image BORDER_HORIZONTAL_CLOSED = getOrLoad("/icons/border_horizontal_closed.png");
	public static final Image BORDER_HORIZONTAL_OPEN = getOrLoad("/icons/border_horizontal_open.png");
	
	public static final Image BORDER_VERTICAL_CLOSED = getOrLoad("/icons/border_vertical_closed.png");
	public static final Image BORDER_VERTICAL_OPEN = getOrLoad("/icons/border_vertical_open.png");
	
	public static final Image COG_EDIT = getOrLoad("/icons/cog_edit.png");
	
	public static final Image TICK = getOrLoad("/icons/tick.png");
	public static final Image FIND = getOrLoad("/icons/find.png");

	public static final Image DISK = getOrLoad("/icons/disk.png");
	public static final Image DISK_MESSAGE = getOrLoad("/icons/disk_message.png");
	public static final Image DISK_WRENCH = getOrLoad("/icons/disk_wrench.png");
	
	public static final Image PAGE_NEXT = getOrLoad("/icons/page-next.gif");
	public static final Image PAGE_PREV = getOrLoad("/icons/page-prev.gif");

	public static final Image PENCIL = getOrLoad("/icons/pencil.png");

	public static final Image GROUP = getOrLoad("/icons/group.png");
	
	public static final Image PAGE_COPY = Images.getOrLoad("/icons/page_copy.png");

	public static final Image SHAPE_SQUARE_EDIT = Images.getOrLoad("/icons/shape_square_edit.png");
	
	public static final Image WRENCH = Images.getOrLoad("/icons/wrench.png");

	static HashMap<String, Image> imageMap;

	public static Image getSystemImage(int swtSysImg) {
		return Display.getDefault().getSystemImage(swtSysImg);
	}

	public static Image getOrLoad(String path) {
		if (imageMap == null)
			imageMap = new HashMap<String, Image>();

		Image img = imageMap.get(path);
		if (img == null) {
			img = SWTResourceManager.getImage(Images.class, path);
			imageMap.put(path, img);
		}
		return img;
	}

	public static Image resize(Image image, int width, int height) {
		return resize(image, width, height, null);
	}
	
	public static Image resize(Image image, int width, int height, Color bg) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		if(bg != null) {
			gc.setBackground(bg);
		}
		int origX = image.getBounds().width;
		int origY = image.getBounds().height;
		
		double scale = ImgUtils.computeScaleFactor(origX, origY, width, height);
		
        int destX = new Double(origX*scale).intValue();
        int destY = new Double(origY*scale).intValue();
		gc.drawImage(image, 0, 0, origX, origY, 0, 0, destX, destY);
		gc.dispose();
		return scaled;
	}
}
