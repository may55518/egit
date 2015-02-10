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



	public static SIMView theSimView;
	private static byte menuEntry;
	private static ToolkitRegistry reg;
	//menu entry
	private static final  byte[]  MenuText  ={'m','a','i','n',' ', 'm','e','n','u'};
	private static final  byte[]  MyText  ={'m','a','y',' ',' ','t','e','s','t'};
	//sub menu
	private static final  byte[]  sub_menu1  ={'i','t','e','m','1'};
	private static final  byte[]  MyText1  ={'i','t','e','m','1','t','e','s','t'};
	private static final  byte[]  sub_menu2  ={'i','t','e','m','2'};
	private static final  byte[]  MyText2  ={'i','t','e','m','2','t','e','s','t'};
	private static final  byte[]  sub_menu3  ={'i','t','e','m','3'};
	private static final  byte[]  MyText3  ={'i','t','e','m','3','t','e','s','t'};

	public static final byte ITEM_ID_1= 1;
	public static final byte ITEM_ID_2 = (byte) (ITEM_ID_1 + 1);
	public static final byte ITEM_ID_3 = (byte) (ITEM_ID_2 + 1);

	public static final byte SCENE_SI = 1;
	public static final byte SCENE_ITEM1 = 2;
	public static final byte SCENE_ITEM2 = 3;
	public static final byte SCENE_ITEM3 = 4;



	samples(){
	reg = ToolkitRegistry.getEntry();;
	menuEntry = reg.initMenuEntry(MenuText, (short) 0,
			                      (short) MenuText.length, (byte) 0, false, (byte) 0,(short) 0);;
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
		byte gr=0;
	    //short off = 0;
	    byte scene = SCENE_SI;
	    do {
	    	switch (scene) {
	    	case SCENE_SI:
	    		 gr = initSelectItem();
	    		 ProactiveResponseHandler proResHdlr = ProactiveResponseHandler
							.getTheHandler();
					byte itemID = proResHdlr.getItemIdentifier();
					switch (itemID) {
					case ITEM_ID_1:
						scene = SCENE_ITEM1;
						gr = initDisplay(MyText1, (short) 0, (short) MyText1.length, 
								(byte) 0x81, (byte) 0x04);
						break;
					case ITEM_ID_2:
						scene = SCENE_ITEM2;
						gr = initDisplay(MyText2, (short) 0, (short) MyText2.length, 
								(byte) 0x81, (byte) 0x04);
						break;
					case ITEM_ID_3:
						scene = SCENE_ITEM3;
						gr = initDisplay(MyText3, (short) 0, (short) MyText3.length, 
								(byte) 0x81, (byte) 0x04);
						break;
					}
					if (gr == RES_CMD_PERF_BACKWARD_MOVE_REQ) {
						continue;
						//gr = RES_CMD_PERF_SESSION_TERM_USER;
					}
					break;
	    	}
	    	if (scene!=SCENE_SI) {
	    		scene =SCENE_SI ;
	    	}
	    }while (gr != RES_CMD_PERF_SESSION_TERM_USER);

		 
	}


	private static byte initDisplay(byte[] dtBUF, short offset, short len,
				byte qualifier, byte dcs) {
			ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
			proHdlr.initDisplayText(qualifier, dcs, dtBUF, offset, len);
			return proHdlr.send();
		}


	private byte initSelectItem() {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.init(PRO_CMD_SELECT_ITEM, (byte) 0x00, DEV_ID_ME);
		proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), ITEM_ID_1, sub_menu1,
				(short) 0, (short) sub_menu1.length);
		proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), ITEM_ID_2, sub_menu2,
				(short) 0, (short) sub_menu2.length);
		proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), ITEM_ID_3, sub_menu3,
				(short) 0, (short) sub_menu3.length);
		return proHdlr.send();
	  }

	} //end of class sample
