package stk_demo;

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

public class STKSample extends Applet implements ToolkitInterface,
		ToolkitConstants {
	public static SIMView theSimView;
	private static byte menuEntry;
	private static byte[] tempbuf;
	private static byte[] tempbuf1;
	private static byte[] SMSC;

	public static final byte[] STK_SAMPLE = { 's', 't', 'k', ' ', 'd', 'e',
			'm', 'o' };
	public static final byte[] sendUssd = { 's', 'e', 'n', 'd', ' ', 'U', 's',
			's', 'd' };
	public static final byte[] sendSms = { 's', 'e', 'n', 'd', ' ', 'S', 'm',
			's' };
	public static final byte[] playTone = { 'p', 'l', 'a', 'y', ' ', 'T', 'o',
			'n', 'e' };

	public static final byte[] ALPHA_GI = { 'I', 'n', 'p', 'u', 't', ' ', 's',
			'e', 'n', 'd', 'i', 'n', 'g', ' ', 'c', 'o', 'n', 't', 'e', 'n',
			't', ':' };

	public static final byte[] ALPHA_SS = {(byte)14, 's', 'e', 'n', 'd', 'i', 'n', 'g',
			' ', 'S', 'M', 'S', '.', '.', '.' };
	public static final byte[] ALPHA_SU = { (byte)15,'s', 'e', 'n', 'd', 'i', 'n', 'g',
			' ', 'U', 'S', 'S', 'D', '.', '.', '.' };

	public static final byte[] DT_PART1 = { 'I', 'n', 'p', 'u', 't', 't', 'e',
			'd', ' ', 'c', 'o', 'n', 't', 'e', 'n', 't', ' ', 'i', 's', ':' };
	public static final byte[] DT_PART2 = { (byte) 0x0D, (byte) 0x0A,
			(byte) 0x73, (byte) 0x65, (byte) 0x6E, (byte) 0x64, (byte) 0x3F };

	public static final byte[] dstNumber = { (byte) 5, (byte) 5, (byte) 0x81,
			(byte) 0x01, (byte) 0x00, (byte) 0xF3 }; // destination number:
														// 10003

	public static final byte ITEM_ID_SU = 1;
	public static final byte ITEM_ID_SS = (byte) (ITEM_ID_SU + 1);
	public static final byte ITEM_ID_PT = (byte) (ITEM_ID_SS + 1);

	public static final byte SCENE_SI = 1;
	public static final byte SCENE_GI_FOR_SMS = 2;
	public static final byte SCENE_GI_FOR_USSD = 3;
	public static final byte SCENE_DT_FOR_SMS = 4;
	public static final byte SCENE_DT_FOR_USSD = 5;
	public static final byte SCENE_SU = 6;
	public static final byte SCENE_SS = 7;
	public static final byte SCENE_PT = 8;

	private static ToolkitRegistry reg;

	STKSample() {
		reg = ToolkitRegistry.getEntry();
		menuEntry = reg
				.initMenuEntry(STK_SAMPLE, (short) 0,
						(short) STK_SAMPLE.length, (byte) 0, false, (byte) 0,
						(short) 0);
		tempbuf = new byte[255];
		tempbuf1 = new byte[50];
		SMSC = new byte[7];
	}


	public void process(APDU arg0) throws ISOException {
		// TODO Auto-generated method stub

	}


	public void processToolkit(byte event) throws ToolkitException {
		if (event == ToolkitConstants.EVENT_MENU_SELECTION) {
			EnvelopeHandler theEnv = EnvelopeHandler.getTheHandler();
			byte menuId = theEnv.getItemIdentifier();
			if (menuId == menuEntry)
				gotoSTK();
		}

	}

	private void gotoSTK() {
		byte scene = SCENE_SI;
		byte gr = 0;
		short off = 0;
		do {
			switch (scene) {
			case SCENE_SI:
				gr = initSelectItem();
				if (gr == RES_CMD_PERF) {
					ProactiveResponseHandler proResHdlr = ProactiveResponseHandler
							.getTheHandler();
					byte itemID = proResHdlr.getItemIdentifier();
					switch (itemID) {
					case ITEM_ID_SU:
						scene = SCENE_GI_FOR_USSD;
						break;
					case ITEM_ID_SS:
						scene = SCENE_GI_FOR_SMS;
						break;
					case ITEM_ID_PT:
						scene = SCENE_PT;
						break;
					}
				}
				if (gr == RES_CMD_PERF_BACKWARD_MOVE_REQ) {
					gr = RES_CMD_PERF_SESSION_TERM_USER;
				}
				break;
			case SCENE_GI_FOR_SMS:
				gr = initGetInput(ALPHA_GI, (byte) 0x04, (byte) 0x00,
						(short) 1, (short) 20);
				if (gr == RES_CMD_PERF) {
					ProactiveResponseHandler myRespHdlr = ProactiveResponseHandler
							.getTheHandler();
					myRespHdlr.copyTextString(tempbuf1, (short) 1);
					tempbuf1[0] = (byte) myRespHdlr.getTextStringLength();
					scene = SCENE_DT_FOR_SMS;
				}
				if (gr == RES_CMD_PERF_BACKWARD_MOVE_REQ) {
					scene = SCENE_DT_FOR_SMS;
				}

				break;
			case SCENE_GI_FOR_USSD:
				gr = initGetInput(ALPHA_GI, (byte) 0x04, (byte) 0x00,
						(short) 1, (short) 20);
				if (gr == RES_CMD_PERF) {
					ProactiveResponseHandler myRespHdlr = ProactiveResponseHandler
							.getTheHandler();
					myRespHdlr.copyTextString(tempbuf1, (short) 1);
					tempbuf1[0] = (byte) myRespHdlr.getTextStringLength();
					scene = SCENE_DT_FOR_USSD;
				}
				if (gr == RES_CMD_PERF_BACKWARD_MOVE_REQ) {
					scene = SCENE_SI;
				}
				break;
			case SCENE_DT_FOR_SMS:
				off = Util.arrayCopy(DT_PART1, (short) 0, tempbuf, (short) 0,
						(short) DT_PART1.length);
				off = Util.arrayCopy(tempbuf1, (short) 1, tempbuf, off,
						(short) tempbuf1[0]);
				off = Util.arrayCopy(DT_PART2, (short) 0, tempbuf, off,
						(short) DT_PART2.length);
				gr = initDisplay(tempbuf, (short) 0, off, (byte) 0x81,
						(byte) 0x04);
				if (gr == RES_CMD_PERF) {
					scene = SCENE_SS;
				}
				if (gr == RES_CMD_PERF_BACKWARD_MOVE_REQ) {
					scene = SCENE_SI;
				}

				break;
			case SCENE_DT_FOR_USSD:
				off = Util.arrayCopy(DT_PART1, (short) 0, tempbuf, (short) 0,
						(short) DT_PART1.length);
				off = Util.arrayCopy(tempbuf1, (short) 1, tempbuf, off,
						(short) tempbuf1[0]);
				off = Util.arrayCopy(DT_PART2, (short) 0, tempbuf, off,
						(short) DT_PART2.length);
				gr = initDisplay(tempbuf, (short) 0, off, (byte) 0x81,
						(byte) 0x04);
				if (gr == RES_CMD_PERF) {
					scene = SCENE_SU;
				}
				if (gr == RES_CMD_PERF_BACKWARD_MOVE_REQ) {
					scene = SCENE_GI_FOR_USSD;
				}
				break;
			case SCENE_PT:
				initPlayTone();
				scene = SCENE_SI;
				break;
			case SCENE_SS:
				sendSMS(tempbuf1, (short) 0, dstNumber, (short) 0, (byte) 0,
						ALPHA_SS, (short) 0);
				scene = SCENE_SI;
				break;
			case SCENE_SU:
				sendUssd(tempbuf1, (short) 0);
				scene = SCENE_SI;
				break;
			}

		} while (gr != RES_CMD_PERF_SESSION_TERM_USER);

	}

	private static byte sendSMS(byte[] smsContent, short offset, byte[] dstNo,
			short offset1, byte dcs, byte[] alphaID, short alOff) {
		ProactiveHandler myProHdlr = ProactiveHandler.getTheHandler();
		myProHdlr.init(PRO_CMD_SEND_SHORT_MESSAGE, (byte) 0x01, DEV_ID_NETWORK);
		select(SIMView.FID_DF_TELECOM, SIMView.FID_EF_SMSP, tempbuf, (short) 0,
				(short) 15);
		
		short reclength = (short) (tempbuf[14] & (short) 0x00FF);

		theSimView.readRecord((short) 1,
				(byte) SIMView.REC_ACC_MODE_ABSOLUTE_CURRENT,
				(short) (reclength - 15), tempbuf, (short) 0, (short) 1);
		theSimView.readRecord((short) 1,
				(byte) SIMView.REC_ACC_MODE_ABSOLUTE_CURRENT,
				(short) (reclength - 14), SMSC, (short) 0,
				(short) (tempbuf[0] & (short) 0x00FF));
		if (alphaID != null && alOff != (short) 0xFFFF) {
			myProHdlr
					.appendTLV(
							(byte) (ToolkitConstants.TAG_ALPHA_IDENTIFIER | ToolkitConstants.TAG_SET_CR),
							alphaID, (short) (alOff + 1),
							(short) alphaID[alOff]);
		}
		myProHdlr
				.appendTLV(
						(byte) (ToolkitConstants.TAG_ADDRESS | ToolkitConstants.TAG_SET_CR),
						SMSC, (short) 0x00,
						(short) (tempbuf[0] & (short) 0x00FF));
		Util.arrayFillNonAtomic(tempbuf, (short) 0, (short) tempbuf.length,
				(byte) 0xFF);

		tempbuf[0] = (byte) 0x11;// TP-MTI
		tempbuf[1] = (byte) 0x00;// TP-MR

		// byte length = (byte) 0x05;// TPDUlength
		// tempAry[2] = length;// length // TP-DA length
		// tempAry[3] = (byte) 0x91;// type//
		// Util.arrayCopy(ADN, (short)0, command, (short)4,
		// (short)ADN.length);// TP-DA phoneLength

		// length = (byte) 0x03;// ADN length
		Util.arrayCopy(dstNo, (short) (offset1 + 1), tempbuf, (short) 2,
				(short) (dstNo[offset1]));// TP-DA
		byte length = (byte) (dstNo[offset1] + 2);
		tempbuf[(byte) (length++)] = (byte) 0x00;// TP-PID
		tempbuf[(byte) (length++)] = dcs;// // TP-DCS 04:8 bit
		// 00:7-bit alpha charater
		tempbuf[(byte) (length++)] = (byte) 0x08;// // TP-VP
		// 00:7-bit alpha charater
		// TP-VP
		// command[(byte)(len+7)] = (byte)0x00;
		tempbuf[(byte) (length++)] = (byte) smsContent[offset];// TP-UDL
		Util.arrayCopy(smsContent, (short) (offset + 1), tempbuf,
				(short) (length), smsContent[offset]);// TP-UD
		myProHdlr
				.appendTLV(
						(byte) (ToolkitConstants.TAG_SMS_TPDU | ToolkitConstants.TAG_SET_CR),
						tempbuf, (short) 0,
						(short) (length + smsContent[offset]));
		return myProHdlr.send();

	}

	private static short select(short DF, short EF, byte[] FCI, short offset,
			short len) {

		theSimView.select(SIMView.FID_MF);
		theSimView.select(DF);
		theSimView.select(EF, FCI, (short) offset, (short) len);
		return Util.makeShort(FCI[(short) (offset + 2)],
				FCI[(short) (offset + 3)]);

	}

	private static byte sendUssd(byte[] UssdString, short usdOff) {
		ProactiveHandler proHdlr;
		proHdlr = ProactiveHandler.getTheHandler();
		byte dcs = 0x0F;
		proHdlr.init(PRO_CMD_SEND_USSD, (byte) 0, DEV_ID_NETWORK);// initialize
		convert(UssdString, (short) usdOff, tempbuf, (short) 0, (byte) 1);// packed
		proHdlr.appendTLV(TAG_USSD_STRING, dcs, tempbuf, (short) 1, Util
				.makeShort((byte) 0, tempbuf[0]));
		proHdlr
				.appendTLV(
						(byte) (ToolkitConstants.TAG_ALPHA_IDENTIFIER | ToolkitConstants.TAG_SET_CR),
						ALPHA_SU, (short) 0, (short) ALPHA_SU.length);

		return proHdlr.send();
	}

	private void initPlayTone() {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.init(PRO_CMD_PLAY_TONE, (byte) 0, DEV_ID_EARPIECE);
		proHdlr.send();
	}

	private static byte initDisplay(byte[] dtBUF, short offset, short len,
			byte qualifier, byte dcs) {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.initDisplayText(qualifier, dcs, dtBUF, offset, len);
		return proHdlr.send();
	}

	private static byte initGetInput(byte[] alphaID, byte DCS, byte qualifier,
			short minLen, short maxLen) {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.initGetInput(qualifier, DCS, alphaID, (short) 0,
				(short) alphaID.length, minLen, maxLen);

		return proHdlr.send();
	}

	private byte initSelectItem() {
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.init(PRO_CMD_SELECT_ITEM, (byte) 0x00, DEV_ID_ME);
		proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), ITEM_ID_SU, sendUssd,
				(short) 0, (short) sendUssd.length);
		proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), ITEM_ID_SS, sendSms,
				(short) 0, (short) sendSms.length);
		proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), ITEM_ID_PT, playTone,
				(short) 0, (short) playTone.length);
		return proHdlr.send();
	}

	/**
	 * function: convert
	 * 
	 * @param src
	 *            is the source array to be converted
	 * @param off
	 *            is the offset of the source array LV, start from the length
	 * @param dst
	 *            is the destination array after converting
	 * @param writeoff
	 *            is the offset of the destination array LV
	 * @param attribute
	 *            is 1:7bit->8 bit packed; //0: 8 bit->7bit unpacked
	 */
	private static void convert(byte[] src, short inoff, byte[] dst,
			short outoff, byte attribute) {
		short len; // total length of string
		byte src1; // source bye 1
		byte src2; // source byte 2
		short temp = (short) 0;
		byte count = (byte) 1; // bits to be moved
		short writeoff = outoff;
		short off = inoff;
		len = (short) (((short) src[off]) & 0x00FF); // string length
		temp = (short) (off + len);

		if (len <= (short) 1) {
			dst[writeoff] = src[off];
			dst[(short) (writeoff + 1)] = src[(short) (off + 1)];
			return;
		} else if (attribute == (byte) 1) { // 1:7bit->8 bit packed
			off = (short) (off + (short) 1);
			writeoff = (short) (writeoff + (short) 1);
			do {
				src1 = src[off];
				src2 = src[(short) (off + (short) 1)];
				src1 = (byte) ((byte) (src1 & (byte) 0x7F) >> (byte) (count - 1));
				src2 = (byte) (src2 << (byte) (8 - count));
				src1 = (byte) (src1 | src2);
				dst[writeoff] = src1;
				off++;
				writeoff++;
				if (count == 7) { // at most 7 bits
					off++;
					count = (byte) 1; // count from begin
				} else {
					count = (byte) (count + (byte) 1);
				}
			} while (temp > off); // all bytes convert
			if (len % (byte) 8 != (byte) 0) {// 8n //pack the last byte
				src1 = src[off];
				src1 = (byte) ((byte) (src1 & (byte) 0x7F) >> (byte) (count - 1));
				if (count == (byte) 7) {
					src1 = (byte) (src1 | (byte) 0x1A);// add <CR>
				}
				dst[writeoff] = src1;
			} else {// no 8n
				if (src[(short) (off - 1)] == (byte) 0x0D) {// last byte is <CR>
					dst[writeoff] = (byte) 0x0D;
				} else {
					writeoff--;
				}
			}
		}
		dst[outoff] = (byte) (writeoff - outoff); // length
		return;
	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		theSimView = SIMSystem.getTheSIMView();
		STKSample stk = new STKSample();
		stk.register();

	}
}
