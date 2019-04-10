package org.jim.doctools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class docFile {
	private static Logger log = LogManager.getLogger(docFile.class);
    public static String [] getAllFileArray(String path)
    {
        File file = new File(path);
        if (file.exists()&&file.isDirectory()) {
        	String [] Arrayfile = file.list();
            return Arrayfile;
        }else {
        	return null;
        }
        
    }
    
    public static ArrayList<String> getAllFileList(String path,String exclude) {
    	ArrayList<String> lst=new ArrayList<String>();
    	String[] tmp=getAllFileArray(path);
    	if (tmp==null) {
    		log.error(path+"缺少试题");
    	}
    	for(String x:tmp) {
    		if (!x.contains(exclude)) {
    			lst.add(x);
    		}
    	}
    	return lst;
    }
    
    public static void copyFile(String srcPathStr, String desPathStr) throws IOException {
    	int n = 0;

        FileInputStream fis = new FileInputStream(srcPathStr);
         FileOutputStream fos = new FileOutputStream(desPathStr);

         while ((n = fis.read()) != -1) {
             fos.write(n);
        }
        fis.close();
        fos.close();
    }
    
    public static void copyDir(String sourcePath, String newPath) throws Exception {
        File file = new File(sourcePath);
        String[] filePath = file.list();
        File nf=new File(newPath);
        if (!nf.exists()) {
        	nf.mkdirs();
        } 
        if (nf.isFile()) {
        	log.error(newPath+"已经存在，且并不是文件夹");
        } else {
        	for(String p:filePath) {
        		String tp=sourcePath + file.separator +p;
        		String np=newPath+ file.separator +p;
        		File tf=new File(tp);
        		if (tf.isDirectory()) {
        			copyDir(tp,np);
        		}else {
        			copyFile(tp,np);
        		}
        	}
        }    
    }
    
    public static boolean delAllFile(String path) {  
        boolean flag = false;  
        File file = new File(path);  
        if (!file.exists()) {  
          return flag;  
        }  
        if (!file.isDirectory()) {  
          return flag;  
        }  
        String[] tempList = file.list();  
        File temp = null;  
        for (int i = 0; i < tempList.length; i++) {  
           if (path.endsWith(File.separator)) {  
              temp = new File(path + tempList[i]);  
           } else {  
               temp = new File(path + File.separator + tempList[i]);  
           }  
           if (temp.isFile()) {  
              temp.delete();  
           }  
           if (temp.isDirectory()) {  
              delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件  
              delFolder(path + "/" + tempList[i]);//再删除空文件夹  
              flag = true;  
           }  
        }  
        return flag;  
      }  
   
    
    public static void delFolder(String folderPath) {  
        try {  
           delAllFile(folderPath); //删除完里面所有内容  
           String filePath = folderPath;  
           filePath = filePath.toString();  
           java.io.File myFilePath = new java.io.File(filePath);  
           myFilePath.delete(); //删除空文件夹  
        } catch (Exception e) {  
          e.printStackTrace();   
        }  
   }  
}
