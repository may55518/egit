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
	//test git new for merge test
	private static final  byte[]  MenuText  ={'m','a','i','n',' ', 'm','e','n','u'};
	private static final  byte[]  MyText  ={'m','a','y',' ',' ','t','e','s','t'};
	//sub menu
	private static final  byte[]  sub_menu1  ={'i','n','p','u','t'};
	private static final  byte[]  MyText1  ={'i','t','e','m','1','t','e','s','t'};
	private static final  byte[]  sub_menu2  ={'i','t','e','m','2'};
	private static final  byte[]  MyText2  ={'i','t','e','m','2','t','e','s','t'};
	private static final  byte[]  sub_menu3  ={'i','t','e','m','3'};
	private static final  byte[]  MyText3  ={'i','t','e','m','3','t','e','s','t'};
	private static final  byte[]  ok  ={'o','k'};

	public static final byte ITEM_ID_1= 1;
	public static final byte ITEM_ID_2 = (byte) (ITEM_ID_1 + 1);
	public static final byte ITEM_ID_3 = (byte) (ITEM_ID_2 + 1);

	public static final byte SCENE_SI = 1;
	public static final byte SCENE_ITEM1 = 2;
	public static final byte SCENE_ITEM2 = 3;
	public static final byte SCENE_ITEM3 = 4;
	public static final byte output_text = 5;
    //for display input message
	private static final byte[] ALPHA_GI = {'I','n','p','u','t',' ','y','o','u','r',' ','m','e','s','s','a','g','e',':'};
	private static byte[] tempbuf;
	private static byte[] tempbuf1;
	public static final byte[] DT_PART1 = { 'I', 'n', 'p', 'u', 't', 't', 'e',
		'd', ' ', 'c', 'o', 'n', 't', 'e', 'n', 't', ' ', 'i', 's', ':' };
    public static final byte[] DT_PART2 = { (byte) 0x0D, (byte) 0x0A,
		(byte) 0x73, (byte) 0x65, (byte) 0x6E, (byte) 0x64, (byte) 0x3F };


	samples(){
	reg = ToolkitRegistry.getEntry();;
	menuEntry = reg.initMenuEntry(MenuText, (short) 0,
			                      (short) MenuText.length, (byte) 0, false, (byte) 0,(short) 0);;
			              		tempbuf = new byte[255];
			            		tempbuf1 = new byte[50];
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
	    short off = 0;
	    byte scene = SCENE_SI;
	    do {
	    	switch (scene) {
	    	case SCENE_SI:
	    		 gr = initSelectItem();
	    		 ProactiveResponseHandler proResHdlr = ProactiveResponseHandler
							.getTheHandler();
					byte itemID = proResHdlr.getItemIdentifier();
					switch (itemID) {
					//get input string and display
					case ITEM_ID_1: 
						scene = SCENE_ITEM1;
						gr = initGetInput(ALPHA_GI, (byte) 0x04, (byte) 0x00,
								(short) 1, (short) 20);
						
						if (gr == RES_CMD_PERF) {
							ProactiveResponseHandler myRespHdlr = ProactiveResponseHandler
									.getTheHandler();
							myRespHdlr.copyTextString(tempbuf, (short) 1);
							tempbuf[0] = (byte) myRespHdlr.getTextStringLength();
							gr = initDisplay(tempbuf, (short) 1, (short) tempbuf[0], 
									(byte) 0x81, (byte) 0x04);
						
						}
					    /*combine tempbuf = DT_PAET1 + tempbuf1 + DT_PART2
					     *off is final length of final buf  
						*/
						
						
						
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
	    	case output_text:
	    		gr = initDisplay(tempbuf, (short) 0, off, (byte) 0x81,
						(byte) 0x04);
	    		scene =  SCENE_SI;
	    		break;
	    	}
	    	if (scene!=SCENE_SI) {
	    		scene =SCENE_SI ;
	    	}
	    }while (gr != RES_CMD_PERF_SESSION_TERM_USER);

		 
	}
	
	private static byte initGetInput(byte[] alphaID, byte DCS, byte qualifier,
			short minLen, short maxLen) {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.initGetInput(qualifier, DCS, alphaID, (short) 0,
				(short) alphaID.length, minLen, maxLen);

		return proHdlr.send();
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
