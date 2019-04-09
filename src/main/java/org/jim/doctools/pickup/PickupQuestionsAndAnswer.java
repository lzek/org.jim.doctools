package org.jim.doctools.pickup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jim.doctools.merge.MergerFolder;
import org.jim.doctools.ui.App;
import org.jim.doctools.util.AppProperties;
import org.jim.doctools.util.FlatFile;
import org.jim.doctools.util.JPoi;
import org.jim.doctools.util.XwpfWrite;
import org.jim.doctools.util.docFile;


public class PickupQuestionsAndAnswer {
	Logger log = LogManager.getLogger(App.class);
	HashMap<String,String> docProperties=new HashMap<String,String>();
	
	PickupQuestionsAndAnswer(String [] args){
		initArgs(args) ;	
	}
	
	public PickupQuestionsAndAnswer(String [] args,HashMap<String,String> dp){
		initArgs(args,dp) ;	
	}
	
	private ArrayList<String> contextRows=new ArrayList<String>();
	private ArrayList<String> sgtitle=new ArrayList<String>();
	private ArrayList<String> mtitle=new ArrayList<String>();
	private ArrayList<String> igtitle=new ArrayList<String>();
	private ArrayList<String> tftitle=new ArrayList<String>();
	private ArrayList<String> eqtitle=new ArrayList<String>();
	private ArrayList<String> cmtitle=new ArrayList<String>();
	private ArrayList<String> unknown=new ArrayList<String>();
	private String fpath="";
	private String type="poi";	
	private String batch="";	


	 private void initArgs(String[] args) {  
		 initArgs(args,AppProperties.get());
		 SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		 batch=f.format(new Date());
	 }
	
    /**
     * 初始化，使用poi或者docx4处理，记录文档路径
     * @param args 
     */
    private void initArgs(String[] args,HashMap<String,String> dp) {   
    	docProperties=dp;
    	if (docProperties.containsKey("default")) {
    		type=docProperties.get("default");
    	}
    	
    	for(String arg:args){
    		switch (arg.toLowerCase()) {
	    		case "/poi":	
	    		case "/docx4j":
	    			type=arg.toLowerCase().substring(1);
	    			break;
	    		case "/path":
	    			break;
    			default:
    				fpath=arg;	
    		}
    	}
    	SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		 batch=f.format(new Date());
    }
    
    /**
     * 提取试卷全文列表
     * @return 试卷全文列表
     * @throws Exception 
     */
    private  void getExaminationPaperText() throws Exception {
    	JPoi jpoi=new JPoi();
    	switch (type) {
		case "poi":			
			contextRows=jpoi.getContextRows(fpath);
			break;
		case "docx4j":
			log.error("docx4j hasnot used");
			throw new Exception("docx4j hasnot used");
		default:
			contextRows=jpoi.getContextRows(fpath);	
    	}    	
    }   
   
    /**
     *  获取当前题目类型
     * @param row
     * @param keytype
     * @return
     */
   private String[] getCurrentPart(String row) {
	   String[] result=new String[2];
	   result[0]="";
	   result[1]="false";
	   	for(String key:docProperties.keySet()) {
	   		if (key.startsWith("c_")&&!key.contains("-")) {
	   			String rgx=docProperties.get(key);
	   			//for(String rgx:docProperties.get(key).split(",")) {
	   				Pattern r = Pattern.compile(rgx,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
	   				Matcher m = r.matcher(row);
	   				while(m.find()) {   					 
	   			         log.debug("探测试题类型："+m.group());
	   			         result[0]=key;
	   			         result[1]="true";
	   			         return result;
	   			      }
	   			//}
	   		}
	   	} 
	   	return result;
   }
    
   /**
    * 保存单题目到题目组
    * @param key
    * @param nqa
    */
   private void saveToTitleObj(String key,String nqa){
   	switch (key) {
		case "c_singleChoice":
				sgtitle.add(nqa);
				break;
		case "c_multipleChoice":
			mtitle.add(nqa);
			break;
		case "c_indefiniteChoice":
			igtitle.add(nqa);
			break;
		case "c_TFChoice":
			tftitle.add(nqa);
			break;
		case "c_essayQuestion":
			eqtitle.add(nqa);
			break;
		case "c_completion":
			cmtitle.add(nqa);
			break;
		case "未知":
			this.unknown.add(nqa);
			break;
			default:
		}
   }
   
   
    /**
     * 将接收到的分类题目的全文，按照正则拆分成单条题目
     * @param key 题目分类
     * @param titleText 分类题目的全文
     */   
    private void pickupSingleTitle() {
    	String qaType="未知";
    	String currentQaType=qaType;
    	String nqa="";
    	boolean newQuestion=true;
    	for (String row:contextRows) {  
    		String[] ks=getCurrentPart(row); 
    		qaType=ks[0];
    		String gotType=ks[1];
    		if (gotType.equals("true") ) {  		
    			//更换新题目时保存题目到题目组
				if (newQuestion==false && !nqa.equals("") ) {
						//重复题目的情况
						String isTitle=getCurrentPart(nqa)[1];
						if (isTitle.equals("false")) {
							saveToTitleObj(currentQaType,nqa);
						}							
				}				
    			newQuestion=true;
    			nqa="";
    			
    			currentQaType=qaType;
    			continue;
    		}
    		
    		row=row.trim();
    		 
    		if (!row.equals("")) {
	    		if (nqa.equals("")) {
					nqa=cleanTitleNum(row);
				}else {
					nqa=nqa+"####"+row;
				}
    		}
			newQuestion=false;	
			
			//存在空行即认为新问题开始
    		if (row.equals("")) {
    			newQuestion=true;
    			if (!nqa.equals("")) saveToTitleObj(currentQaType,nqa);
    			nqa="";
    			continue;
    		} 
    		
    		//允许设置判断和填空题，每行都是新题目
    		if (this.docProperties.get("tf_completion_eachAsNew").equals("true")&&
    				(currentQaType.equals("c_completion")||currentQaType.equals("c_TFChoice"))
    			) {
    			newQuestion=true;
    			if (!nqa.equals("")) saveToTitleObj(currentQaType,nqa);
    			nqa="";
    			continue;
    		} 

    	}
    	//处理最后获取的题目
    	if (newQuestion==false) {
    		if (!nqa.equals("")) saveToTitleObj(currentQaType,nqa);
		}
    }
    

    
    /**
     * 清除单条题目的序号
     * @param key 题目分类
     * @param titleText 单条题目
     * @return 清除了序号的题目
     */
    private String  cleanTitleNum(String titleText) {
    	Pattern r=Pattern.compile(docProperties.get("order_num_rgx"),Pattern.DOTALL | Pattern.CASE_INSENSITIVE|Pattern.MULTILINE) ;
    	Matcher m =r.matcher(titleText);
    	String title=titleText;
		while(m.find()) {    
			title=titleText.substring(m.end());	
			break;
		}
		return title;
    }
    
    
    /**
     * 将试卷中的题目提取后分文件夹存放
     * @throws Exception 
     */
   public void run() throws Exception {	   
	   String tempdatabasePath=docProperties.get("tempdatabase");
	   String databasePath=docProperties.get("database");
	   
	   getExaminationPaperText();
	   this.pickupSingleTitle();	   
	   
	   save(this.sgtitle,tempdatabasePath+"\\单选","单选");
	   save(this.mtitle,tempdatabasePath+"\\多选","多选");
	   save(this.igtitle,tempdatabasePath+"\\不定项","不定项");
	   save(this.tftitle,tempdatabasePath+"\\判断","判断");
	   save(this.eqtitle,tempdatabasePath+"\\简答","简答");
	   save(this.cmtitle,tempdatabasePath+"\\填空","填空");
	   save(this.unknown,tempdatabasePath+"\\未知","未知");
	   
	   log.info("试卷["+fpath+"]题目已经提取到临时库:["+tempdatabasePath+"]");
	   //tempdatabase去重操作
	   mergeTitleFolder(tempdatabasePath,tempdatabasePath);
	   
	 //合并到databasePath
	   mergeTitleFolder(databasePath,tempdatabasePath);
	   
	   File file=new File(tempdatabasePath);
	   if ( file.exists() && !tempdatabasePath.contentEquals(databasePath)) {
		   docFile.delFolder(tempdatabasePath);
		   file.delete();
	   }
   }   
   
   private void mergeTitleFolder(String AFolder,String BFolder) throws Exception {
	   MergerFolder.run(AFolder+"\\单选", BFolder+"\\单选", Float.parseFloat(docProperties.get("单选_merge_per")));
	   MergerFolder.run(AFolder+"\\多选", BFolder+"\\多选",Float.parseFloat( docProperties.get("多选_merge_per")));
	   MergerFolder.run(AFolder+"\\不定项", BFolder+"\\不定项", Float.parseFloat(docProperties.get("不定项_merge_per")));
	   MergerFolder.run(AFolder+"\\判断", BFolder+"\\判断", Float.parseFloat(docProperties.get("判断_merge_per")));
	   MergerFolder.run(AFolder+"\\简答", BFolder+"\\简答",Float.parseFloat( docProperties.get("简答_merge_per")));
	   MergerFolder.run(AFolder+"\\填空", BFolder+"\\填空", Float.parseFloat(docProperties.get("填空_merge_per")));
	   MergerFolder.run(AFolder+"\\未知", BFolder+"\\未知", Float.parseFloat(docProperties.get("未知_merge_per")));
   }
   
   /**
    * 将单组题目存放到文件夹中
    * @param listTitle
    * @param path 存放路径
    * @param FileName 文件前缀名
    */
   private void save(ArrayList<String> listTitle,String path,String FileName) {
	   XwpfWrite xw=new XwpfWrite();
	// 指定路径如果没有则创建并添加
	   File fileParent = new File(path);
	   //判断是否存在
	   if (!fileParent.exists()) {
		   fileParent.mkdirs();
	   }
	   long currentTime=0;
	   Boolean  blankWithUnderline =false;
	   if (FileName.equals("填空")) blankWithUnderline=true;
	   for(String word:listTitle) {
		   currentTime=System.currentTimeMillis();
		   try {
			   String rgx="";
			   switch(FileName) {
				   case "单选":
					   rgx=docProperties.get("c_singleChoice-answer-pattern");
					   break;
				   case "多选":
					   rgx=docProperties.get("c_multipleChoice-answer-pattern");
					   break;
				   case "不定项":
					   rgx=docProperties.get("c_indefiniteChoice-answer-pattern");
					   break;
				   case "填空":
					   rgx=docProperties.get("c_completion-answer-pattern");
					   break;
				   case "简答":
					   rgx=docProperties.get("c_essayQuestion-answer-pattern");
					   break;
				   case "判断":
					   rgx=docProperties.get("c_TFChoice-answer-pattern");
					   default:
			   }
			   //获取答案
			   if (!rgx.equals("")) {
				   String answer="";
				   Pattern r=Pattern.compile(rgx,Pattern.DOTALL | Pattern.CASE_INSENSITIVE|Pattern.MULTILINE) ;
			    	Matcher m =r.matcher(word);
					while(m.find()) {    
						answer=m.group()	;
						break;
					} 
					
					Pattern r2=Pattern.compile("[（\\(][ ,\\.]*[\\)）]",Pattern.DOTALL | Pattern.CASE_INSENSITIVE|Pattern.MULTILINE) ;
					Matcher m2 =r2.matcher(answer);
					
					if (!m2.find()) {
						 if (!answer.trim().equals("")) {
							 word=word.replace(answer, "(    )").trim();
							 for(int i=0;i<26 && !FileName.equals("填空");i++) {
								 answer= answer.replace("(", "").replace(")", "").trim();
								 answer= answer.replace("（", "").replace("）", "").trim();
								 answer=answer.replace(".", ",");
								 answer=answer.replace(" ", ",");
								 answer=answer.replace("、", ",");
								 answer = answer.replaceAll("([A-Z])([A-Z])", "$1,$2");
							 }							 
							 if (!answer.equals("")) xw.savaPickupQuestions(answer,path+"\\"+FileName+"-"+currentTime+"_answer.docx",blankWithUnderline);
						 }
					} else {
						answer="";
					}
			   }
			   switch(FileName) {
			   case "单选":
			   case "多选":
			   case "不定项":
				   word=word.replace("A", "####A");
				   word=word.replace("########A", "####A");
				   break;
			   case "判断":
				   Pattern r3=Pattern.compile("[（\\(][\\s]*[\\)）]",Pattern.DOTALL | Pattern.CASE_INSENSITIVE|Pattern.MULTILINE) ;
					Matcher m3 =r3.matcher(word);
					while(m3.find()) {    
						word=word.replace(m3.group(),"");
						break;
					} 
					
				   default:
		   }
			   if (!word.equals(""))  {
				   xw.savaPickupQuestions(word,path+"\\"+FileName+"-"+currentTime+".docx",blankWithUnderline);
				   FlatFile.write("grabSource.log", "\r\n"+batch+","+FileName+"-"+currentTime+","+fpath);
			   }
		} catch (Exception e) {
			e.printStackTrace();
		}
	   }	   
   }   
}
