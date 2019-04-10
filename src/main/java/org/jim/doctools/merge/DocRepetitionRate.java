package org.jim.doctools.merge;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jim.doctools.util.JPoi;
import org.jim.doctools.util.docFile;

public class DocRepetitionRate {
	private static Logger log = LogManager.getLogger(DocRepetitionRate.class);

	public float RepetitionPercentByLine(String A, String B) throws Exception {
		JPoi jpoi = new JPoi();
		ArrayList<String> AContents = jpoi.getContextRows(A);
		ArrayList<String> BContents = jpoi.getContextRows(B);
		float same = 0.0f;
		for (String Aline : AContents) {
			for (String Bline : BContents) {
				if (Aline.equals(Bline))
					same++;
			}
		}
		float total = AContents.size() >= BContents.size() ? AContents.size() : BContents.size();
		if (total <= 0)
			total = 1;
		return same * 100 / total;
	}

	public float RepetitionPercentByByte(String FileADoc, String FileBDoc) throws Exception {
		JPoi jpoi = new JPoi();
		ArrayList<String> AContents = jpoi.getContextRows(FileADoc);
		ArrayList<String> BContents = jpoi.getContextRows(FileBDoc);
		return RepetitionPercentByByte(AContents, BContents);
	}

	public float RepetitionPercentByByte(ArrayList<String> AContents, ArrayList<String> BContents) throws Exception {
		float same = 0.0f;
		byte[] ABytes = new byte[0];
		byte[] BBytes = new byte[0];
		for (String Aline : AContents) {
			ABytes = byteMerger(Aline.getBytes(), ABytes);
		}
		for (String Bline : BContents) {
			BBytes = byteMerger(Bline.getBytes(), BBytes);
		}
		int bStartPos = 0;
		for (int aPos = 0; aPos < ABytes.length; aPos++) {
			for (int bPos = bStartPos; bPos < BBytes.length; bPos++) {
				if (ABytes[aPos] == BBytes[bPos]) {
					same++;
					bStartPos = bPos;
					break;
				}
			}
		}
		float total = ABytes.length >= BBytes.length ? ABytes.length : ABytes.length;

		if (total <= 0)
			total = 1;
		return same * 100 / total;
	}

	// 使用两个 for 语句
	// java 合并两个byte数组
	public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
		byte[] bt3 = new byte[bt1.length + bt2.length];
		int i = 0;
		for (byte bt : bt1) {
			bt3[i] = bt;
			i++;
		}

		for (byte bt : bt2) {
			bt3[i] = bt;
			i++;
		}
		return bt3;
	}

}
