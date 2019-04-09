package org.jim.doctools.zzz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class XwpfReader {
	/**
	    * 通过XWPFWordExtractor访问XWPFDocument的内容
	    * @throws Exception
	    */
	   public void testReadByExtractor(String file) throws Exception {
	      InputStream is = new FileInputStream(file);
	      XWPFDocument doc = new XWPFDocument(is);
	      XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
	      String text = extractor.getText();
	      System.out.println(text);
	      CoreProperties coreProps = extractor.getCoreProperties();
	      this.printCoreProperties(coreProps);
	      this.close(is);
	   }
	  
	   /**
	    * 输出CoreProperties信息
	    * @param coreProps
	    */
	   private void printCoreProperties(CoreProperties coreProps) {
	      System.out.println(coreProps.getCategory());   //分类
	      System.out.println(coreProps.getCreator()); //创建者
	      System.out.println(coreProps.getCreated()); //创建时间
	      System.out.println(coreProps.getTitle());   //标题
	   }
	  
	   /**
	    * 关闭输入流
	    * @param is
	    */
	   private void close(InputStream is) {
	      if (is != null) {
	         try {
	            is.close();
	         } catch (IOException e) {
	            e.printStackTrace();
	         }
	      }
	   }
}
