package org.jim.doctools.merge;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jim.doctools.ui.App;
import org.jim.doctools.util.FlatFile;
import org.jim.doctools.util.docFile;

public class MergerFolder {
	static Logger log = LogManager.getLogger(MergerFolder.class);

	/**
	 * BFolder合并到AFolder，按照内容重复率大于 Percent清理BFolder， 去重
	 * 
	 * @param AFolder
	 * @param BFolder
	 * @param Percent
	 * @throws Exception
	 */
	public static int run(String AFolder, String BFolder, float Percent) throws Exception {
		int result = 0;
		if (new File(AFolder).exists()) {
			ArrayList<String> AList = docFile.getAllFileList(AFolder, "_answer");
			ArrayList<String> BList = docFile.getAllFileList(BFolder, "_answer");
			DocRepetitionRate drr = new DocRepetitionRate();
			ArrayList<String> removeList = new ArrayList<String>();
			int Acount = 0;
			int savecount = 0;
			int Atotal = AList.size();
			ArrayList<String> record = FlatFile.read("status/Repetition/" + BFolder + ".log");
			for (String a : AList) {
				Acount++;
				int Bcount = 0;
				String tmpA = AFolder + "/" + a;
				System.out.print("\rA:" + Acount + "/" + Atotal + "   ");
				// 同一个文件夹，排除历史已经比较内容
				if (AFolder.contentEquals(BFolder)) {
					Boolean pass = true;
					for (String r : record) {
						if (r.contentEquals(tmpA)) {
							pass = false;
							break;
						}
					}
					if (!pass)
						continue;
				}
				if (removeList.contains(tmpA)) {
					continue;
				}
				for (String b : BList) {
					Bcount++;
					String tmpB = BFolder + "/" + b;
					if (Bcount % 50 == 0)
						System.out.print(">");
					if (!tmpA.contentEquals(tmpB)) {
						try {
							if (Percent == 100) {
								if (drr.RepetitionPercentByByte(tmpA, tmpB) >= Percent) {
									removeList.add(tmpB);
								}
							}
						} catch (Exception e) {
							String eMessage = tmpA + "去重复报错：" + e.getMessage();
							System.out.println(eMessage);
							log.error(eMessage);
							throw e;
						}
					}
				}
				// 标记文件已经检查
				if (AFolder.contentEquals(BFolder)) {
					record.add(tmpA);
				}
				savecount++;
				// 每30秒保存结果，可以反复执行去重
				if (savecount % 30 == 0) {
					FlatFile.saveProperties(record, "status/Repetition/" + BFolder + ".log");
				}
			}
			FlatFile.saveProperties(record, "status/Repetition/" + BFolder + ".log");
			for (String m : removeList) {
				File f = new File(m);
				if (f.exists()) {
					f.delete();
				}
				f = new File(m.replace(".docx", "_answer.docx"));
				if (f.exists()) {
					f.delete();
				}
				result++;
			}
		}
		if (!AFolder.contentEquals(BFolder)) {
			docFile.copyDir(BFolder, AFolder);
		}
		return result;
	}
}
