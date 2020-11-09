package eu.transkribus.swt_gui.dialogs;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import eu.transkribus.swt.util.MessageDialogStyledWithToggle;

public class EventDialogTest {
	public static void main(String[] args) throws IOException, URISyntaxException {
		Shell shell = new Shell();
		String msg = createMessageWithUrl(null);
//		String msg = createMessageWithUrl(10000);
//		String msg = createArbitraryMessage(10000);
		
		MessageDialogStyledWithToggle d = new MessageDialogStyledWithToggle(shell, "Test", null, msg, MessageDialog.INFORMATION, new String[] { "Geh Weida" }, 
				0, "Schleich Di", false);
		d.open();
		
		//old message dialog does not support links or resizing/scrolling
//		DialogUtil.showMessageDialogWithToggle(shell, "Notification", msg, "Do not show this message again", false,
//				SWT.NONE, "OK");
	}

	private static String createMessageWithUrl(Integer length) {
		final String msg = "Dear User,\n"
				+ "due to maintenance work the Transkribus server and our website will be unavailable on\n"
				+ "\n"
				+ "Friday, June 7, in the time between 11:00 - 11:30 CEST.\n"
				+ "\n"
				+ "Running jobs will not be affected by this.\n"
				+ "Please plan your work accordingly. We apologize for any inconvenience.\n"
				+ "Find more information at https://transkribus.eu\n"
				+ "\n"
				+ "Best regards,\n"
				+ "the Transkribus team";
		if(length == null) {
			return msg;
		}
		StringBuffer sb = new StringBuffer();
		while(sb.length() < length) {
			sb.append(msg);
		}
		return sb.toString();
	}
	
	private static String createArbitraryMessage(int length) {
		char c = 'a';
		StringBuffer sb = new StringBuffer(c);
		for(int i = 0; i < length; i++) {
			sb.append(c++);
		}
		return sb.toString();
	}
}
