package org.jim.doctools.merge;

import java.io.File;
import java.util.ArrayList;

import org.jim.doctools.util.docFile;

public class MergerFolder {
	/**
	 * BFolder合并到AFolder，按照内容重复率大于 Percent清理BFolder， 去重
	 * @param AFolder
	 * @param BFolder
	 * @param Percent
	 * @throws Exception
	 */
	public static void run(String AFolder,String BFolder,float Percent) throws Exception {
		if (new File(AFolder).exists()) {
			ArrayList<String> AList=docFile.getAllFileList(AFolder, "_answer");
			ArrayList<String> BList=docFile.getAllFileList(BFolder, "_answer");
			DocRepetitionRate drr=new DocRepetitionRate();
			ArrayList<String> removeList=new ArrayList<String>();
			for(String a:AList) {
				String tmpA=AFolder+"/"+a;
				if (removeList.contains(tmpA)) {
					continue;
				}
				for(String b:BList) {
					String tmpB=BFolder+"/"+b;
					if (!tmpA.contentEquals(tmpB)) {
						if (drr.RepetitionPercentByByte(tmpA,tmpB)>=Percent) {
							removeList.add(tmpB);
						}
					}
				}
			}
			for(String m:removeList) {
				File f=new File(m);
				if (f.exists()) {
					f.delete();
				}
			    f=new File(m.replace(".docx", "_answer.docx"));
				if (f.exists()) {
					f.delete();
				}
			}
		}		
		if (!AFolder.contentEquals(BFolder)) {
			docFile.copyDir(BFolder, AFolder);
		}
	}
}
