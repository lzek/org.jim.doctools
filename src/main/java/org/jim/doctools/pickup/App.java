package org.jim.doctools.pickup;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.jim.doctools.util.AppProperties;
import org.jim.doctools.util.docFile;
/**
 * Hello world!
 *
 */
public class App 
{
	static Logger log = LogManager.getLogger(App.class);
    public static void main( String[] args ) throws Exception
    {
    	AppProperties mp=new AppProperties();
    	File file=new File(AppProperties.get("tempdatabase"));
    	if (file.exists()) {
    		if (file.isDirectory()) {
    			docFile.delAllFile(AppProperties.get("tempdatabase"));
    			}
    		else {
    			log.error("无法创建工作文件夹，已存在同名文件 ‘"+AppProperties.get("tempdatabase")+"’，请先移除后再运行");
    			System.exit(1);
    		}
    	}
    	//PickupQuestions myDoc=new PickupQuestions(args);
    	PickupQuestionsAndAnswer myDoc=new PickupQuestionsAndAnswer(args);
    	myDoc.run();
    	System.out.println(".end");
    }   
}
