/**
 * 
 */
package org.jim.doctools.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * @author lijie
 *
 */
public final class AppProperties {
	Logger log = LogManager.getLogger(AppProperties.class);
	private  static HashMap<String,String> docProperties=new HashMap<String, String>();
	
    public AppProperties() {
    	//Properties prop = new Properties();     
         try{
        	 //InputStream inputStream = null;
        	 //try{
        	//	 inputStream=new FileInputStream("app.properties");
        	// }catch(Exception e) {
        	//	 log.debug("../app.properties");
        	//	 inputStream=new FileInputStream("../app.properties");
        	// }
        	 
             //BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));///加载属性列表
             //prop.load(bf);
            // Iterator<String> it=prop.stringPropertyNames().iterator();
        	 ArrayList<String> prop=FlatFile.loadProperties();
             for(String x:prop){
            	 int nPos=x.indexOf("=");
            	 if (nPos<0) continue;
                String key=x.substring(0, nPos);
                String value="";
                if (nPos+1<=x.length()) {
                	value=x.substring(nPos+1);
                };
                 docProperties.put(key, value);
             }
         }
         catch(Exception e) {
        	 e.printStackTrace();
         }
    } 
    
    public static String get(String key) {
    	return docProperties.get(key);
    }
    
    public static Boolean containsKey(String key) {
    	return docProperties.containsKey(key);
    }
    
    public static Set<String> keySet() {
    	return docProperties.keySet();
    }
    
    public static HashMap<String,String>  get(){
    	return docProperties;
    }
}
