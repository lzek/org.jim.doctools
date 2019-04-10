package org.jim.doctools.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jim.doctools.createpapers.CreatePapers;
import org.jim.doctools.pickup.PickupQuestionsAndAnswer;
import org.jim.doctools.util.AppProperties;
import org.jim.doctools.util.FlatFile;
import org.jim.doctools.util.docFile;

public class Bussiness {
	private static Logger log = LogManager.getLogger(Bussiness.class);
	private HashMap<String, String> docProperties = new HashMap<String, String>();

	public Bussiness() {
		AppProperties ap = new AppProperties();
		docProperties = ap.get();
		try {
			upgradeConfFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 抽取入口
	public boolean pickupTitle(String filename) throws Exception {
		String[] args = new String[2];
		args[0] = "/poi";
		args[1] = filename;
		String tmpdbpath = docProperties.get("tempdatabase");
		String dbpath = docProperties.get("database");
		File file = new File(docProperties.get("tempdatabase"));
		// if (file.exists()&& !dbpath.equals(tmpdbpath)) {
		// if (file.isDirectory()) {
		// docFile.delAllFile(docProperties.get("tempdatabase"));
		// }
		// else {
		// log.error("无法创建工作文件夹，已存在同名文件
		// ‘"+docProperties.get("tempdatabase")+"’，请先移除后再运行");
		// System.exit(1);
		// }
		// }
		PickupQuestionsAndAnswer myDoc = new PickupQuestionsAndAnswer(args, docProperties);
		myDoc.run();
		log.info(".end pickup");
		return true;
	}

	// 生成试卷
	public boolean createpapers() throws Exception {
		CreatePapers st = new CreatePapers(docProperties);
		st.run();
		return true;
	}

	public HashMap<String, String> getProperties() {
		return docProperties;
	}

	public void setProperties(HashMap<String, String> dp) {
		docProperties = dp;
	}

	// 获取模板
	public String getTemplateName() {
		String[] tmp = docFile.getAllFileArray(".");
		String result = "";
		for (String x : tmp) {
			if (x.startsWith("template") && x.endsWith(".docx")) {
				if (result.contentEquals("")) {
					result = " {code:'" + x + "',name:'" + x.replace("template", "").replace(".docx", "") + "'}";
				} else {
					result = result + "," + " {code:'" + x + "',name:'" + x.replace("template", "").replace(".docx", "")
							+ "'}";
				}
			}
		}
		result = "{template:[" + result + "]}";
		return result;
	}

	private void upgradeConfFile() throws IOException {
		FlatFile.upgradePropertiesFromStandard(docProperties);
		FlatFile.saveProperties(docProperties);
	}
}
