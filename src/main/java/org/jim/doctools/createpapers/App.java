package org.jim.doctools.createpapers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jim.doctools.util.AppProperties;
import org.jim.doctools.util.Conf;

public class App {
	 static Logger log = LogManager.getLogger(App.class);
	public static void main(String[] args) throws Exception {
		AppProperties mp=new AppProperties();
		Conf cf=new Conf();
		
		CreatePapers st=new CreatePapers(args);
		st.run();
		log.info("试卷已经生成");
	}

}
