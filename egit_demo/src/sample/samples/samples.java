package sample.samples;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISOException;
import javacard.framework.Util;
import sim.access.SIMSystem;
import sim.access.SIMView;
import sim.toolkit.EnvelopeHandler;
import sim.toolkit.ProactiveHandler;
import sim.toolkit.ProactiveResponseHandler;
import sim.toolkit.ToolkitConstants;
import sim.toolkit.ToolkitException;
import sim.toolkit.ToolkitInterface;
import sim.toolkit.ToolkitRegistry;
public class samples extends Applet implements ToolkitInterface, ToolkitConstants {
//Display DisplayText
private static   final  byte[]  MenuText  ={(byte)68,(byte)105,(byte)115,(byte)112,(byte)108,(byte)97,(byte)121,(byte)84,(byte)101,(byte)120,(byte)116};;
private static   final  byte  MenuItem_addr_1  =0;;
private static   final  byte  MenuItem_len_1  =11;;
public static SIMView theSimView;
private static byte menuEntry;
private static ToolkitRegistry reg;
private static final  byte[]  MyText  ={'m','a','y',' ',' ','t','e','s','t'};



samples(){
reg = ToolkitRegistry.getEntry();;
menuEntry = reg.initMenuEntry(MenuText, (short) MenuItem_addr_1,(short) MenuItem_len_1, (byte) 0, false, (byte) 0,(short) 0);;
////;
}
public void process(APDU arg0) throws ISOException {}
public static void install(byte[] bArray, short bOffset, byte bLength) {
	theSimView = SIMSystem.getTheSIMView();
	samples stk = new samples();
	stk.register();
}
public void processToolkit(byte event) throws ToolkitException {
	if (event == ToolkitConstants.EVENT_MENU_SELECTION) {
		EnvelopeHandler theEnv = EnvelopeHandler.getTheHandler();
		byte menuId = theEnv.getItemIdentifier();
		if (menuId == menuEntry)
			gotoSTK();
	}
}
private void gotoSTK(){
	//byte gr=0;
	//short off = 0;
	 short buf_length = (short) MyText.length;
	 short i = buf_length;
	 initDisplay(MyText, (short) 0, (short) buf_length, (byte) 0x81,(byte) 0x04);
}


private static byte initDisplay(byte[] dtBUF, short offset, short len,
			byte qualifier, byte dcs) {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.initDisplayText(qualifier, dcs, dtBUF, offset, len);
		return proHdlr.send();
	}

}

