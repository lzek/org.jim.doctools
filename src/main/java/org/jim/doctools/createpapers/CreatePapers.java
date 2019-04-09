package org.jim.doctools.createpapers;


import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jim.doctools.util.AppProperties;
import org.jim.doctools.util.NumToCN;
import org.jim.doctools.util.Conf;
import org.jim.doctools.util.FlatFile;
import org.jim.doctools.util.JPoi;
import org.jim.doctools.util.NumToAlphabet;
import org.jim.doctools.util.XwpfWrite;
import org.jim.doctools.util.docFile;

public class CreatePapers {
	 Logger log = LogManager.getLogger(CreatePapers.class);
	 HashMap<String,String> docProperties=new HashMap<String,String>();
	 private ArrayList<String> tmpUsedList=new ArrayList<String>();
	 
	 public CreatePapers(HashMap<String,String> dp){
		 init(dp);
	}	
	 
	CreatePapers(String[] args){
		init(AppProperties.get());
	}	
	
	private void init(HashMap<String,String> dp) {
		docProperties=dp;
	}
	
	/**
	 * 获取试卷替换参数，主要用于生成试卷头
	 * @return
	 */
	private Map<String, String> getParams(){
		Map<String, String> params = new HashMap<String, String>();
		  params.put("${school}", docProperties.get("school"));
	      params.put("${semester}", docProperties.get("semester"));
	      params.put("${subject}",docProperties.get("subject"));
	      params.put("${class}", docProperties.get("class"));
	      params.put("${time}", docProperties.get("time"));
	      params.put("${stage}", docProperties.get("stage"));
		return params;
	}
	
	/**
	 * 获取单一问题及答案
	 * @param file
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	private Map<String,Map<String,String>> getSingleQA(String file,String type) throws Exception{
		Map<String,String> tmpOptionAndAnswerItem=new HashMap<String,String>(); 		
		Map<String,Map<String,String>> mapResult=new HashMap<String,Map<String,String>>();
		JPoi jpoi=new JPoi();
		ArrayList<String> questionRows=jpoi.getContextRows(docProperties.get("database")+"\\"+type+"\\"+file);
		String questionItem="";
		String optionsItem="";	
		int count=-1;
		for(String tmp:questionRows) {
			if (tmp.equals("")) {
				continue;
			}
			if (++count>-1) {
				//拆分题目中的题目和选项，非选择题应该不影响
				Pattern r=Pattern.compile("\\bA[\\.．、].*",Pattern.DOTALL | Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
				Matcher m=r.matcher(tmp);
				questionItem=tmp;
						
				while(m.find()) {
					if (m.start()>0) {
						questionItem=tmp.substring(0, m.start()-1).trim();
						optionsItem=tmp.substring(m.start(), m.end()).trim();
					}
					break;
				}
			} else {
				optionsItem=optionsItem+"  "+tmp;
			}
		}
		
		//获取答案
		String answerItem="";
		String answerswitch=docProperties.get("file_answer");
		String answerFilename=docProperties.get("database")+"\\"+type+"\\"+file.replace(".docx", "_answer.docx");
		File answerFile=new File(answerFilename);
		if (answerFile.exists()&&answerswitch.contains("true")) answerItem=jpoi.getContext(answerFilename);
		//封装选项和答案
		tmpOptionAndAnswerItem.put(optionsItem,answerItem);
		//封装题目，选项和答案
		mapResult.put(questionItem, tmpOptionAndAnswerItem);
		return mapResult;	
	}
	
	/**
	 * 根据配置获取题型需要的 文件名组
	 * @param typename
	 * @return
	 * @throws Exception 
	 */
	private ArrayList<String> getAllFileNamesByType(String typename) throws Exception {
		String ctype_tmp=docProperties.get(typename+"_random");
		int ctype_random=-1;
		if (ctype_tmp!=null &&!ctype_tmp.equals("")) {
			ctype_random=Integer.parseInt(ctype_tmp);
			if (ctype_random<=0) {
				ctype_tmp=docProperties.get(typename+"_names");
			}else {
				ArrayList<String> lstFile=docFile.getAllFileList(docProperties.get("database")+"\\"+typename,"_answer");
				if (lstFile!=null && !lstFile.isEmpty()) {
					Random ra =new Random();
					ctype_tmp="";
					int total=ctype_random;
					int usedCount=0;
					ArrayList<String> usedlist=new ArrayList<String>();
					//int total=lstFile.size()<ctype_random?lstFile.size():ctype_random;
					if (docProperties.get("avoid_used_title").equals("true")) {
						usedlist=FlatFile.read("./status/used/"+typename+"_used.log");
						usedCount=usedlist.size();;
					}		
					int nValid=(lstFile.size()-usedCount)-total;
					if (nValid<0) {
						String usedword="";
						String adviseword="";
						if (usedCount>0 && docProperties.get("avoid_used_title").equals("true"))  {
							usedword="，系统标识已用题目数："+usedCount+"道";
							adviseword="可以通过关闭 '不选已用题目' 控制, 或者清除已用标识重新启用, ";
						}
						throw new Exception(typename+"可用题目不足"+usedword+"，处理办法："+adviseword+"再或者补充"+(-nValid)+"题");
					}
					//最多尝试取题次数，为最大题目数的10000倍，避免死循环 
					for(int i=0,j=0;i<total&& j< total * 10000 ;i++,j++) {
						int index=ra.nextInt(lstFile.size());
						String nChooseName=lstFile.get(index);
						Boolean bpass=false;
						for(String x:usedlist) {
							if (x.equals(nChooseName)) {
								bpass=true;
								i--;
								break;
							}
						}
						if (bpass) continue;
						if (!ctype_tmp.contains(nChooseName) 
								&&!(nChooseName).contains("answer")
								) {
							if (!ctype_tmp.equals("")) {
								ctype_tmp=ctype_tmp+","+lstFile.get(index);
							}else {
								ctype_tmp=lstFile.get(index);
							}							
						}else {
							--i;
						}
					}
					
				}else {
					ctype_tmp="";
				}
			}
		} else {
			ctype_tmp=docProperties.get(typename+"_names");
		}
		ArrayList<String> lstResult=new ArrayList<String>();
		if (ctype_tmp!=null&&!ctype_tmp.equals("")) {
			for(String name:ctype_tmp.split(",")) {
				if (!name.contains(typename+"-")) name=typename + "-"+name;
				if (!name.contains(".docx")) name=name+".docx";
				lstResult.add(name);
			}
		} 		
		for(String sline:lstResult) {
			tmpUsedList.add("./status/used/"+typename+"_used.log,"+ sline+"\r\n");
		}		
		return lstResult;
	}
	
	/**
	 * 按顺序返回映射表，并且包含类型和标题
	 * @return List<Map<String,String>> List<Map<类型名,标题>>
	 */
	private   List<Map<String,String>> getTypeOrder(){
		Map<String, Integer> map = new TreeMap<String, Integer>();
		//ArrayList<Map<String,Integer>> lstResult=new ArrayList<Map<String,Integer>>();
		for(String key:Conf.getNameList()) {
			String tmp=docProperties.get(key+"_order") ;
			if (tmp==null||tmp.equals("")) tmp="-1";
			map.put(key,Integer.parseInt(tmp));
		}
        //这里将map.entrySet()转换成list
        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());

        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //升序排序
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }  
        });
        //形成类型和标题
        List<Map<String,String>> typeOrder=new ArrayList<Map<String,String>>();
        
        int order=1;
        for(Map.Entry<String,Integer> mapping:list){ 
        	if (mapping.getValue()<0) continue;
        	Map<String,String> itemOrder=new  HashMap<String,String>();        	
        	itemOrder.put(mapping.getKey(), NumToCN.getCNNum(order)+"、"+mapping.getKey()+"题");        	
        	typeOrder.add(itemOrder);
        	order++;
          } 
        for (Map<String, String> item:typeOrder) {
        	for(String key:item.keySet()) {
        		log.info(item.get(key));
        	}        	
        }
		return typeOrder;
	}
	
	/**
	 * 选择试题，生成A-B卷和答案
	 * @param newpath
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void run() throws Exception {
		String newpath=docProperties.get("file_prefix");
		if (newpath.equals("template")) {
			log.error("新文件名不可以是template");
	    	  throw new Exception("新文件名不可以是template");
	      }
	      if (newpath==null || newpath.equals("")) {
	    	  newpath="newExam";
	      }
		//生成试卷卷头
		XwpfWrite xw=new XwpfWrite();
		Map<String, String> params =getParams();
		
		//解析题型顺序 orderExam
		List<Map<String,String>> orderExam=getTypeOrder();		
		
		//获取问题全文，形成原始套题 questionsExam
		ArrayList<Object> questionsExam=new ArrayList<Object>();
		for (Map<String, String> item:orderExam) {
        	for(String title:item.keySet()) {
        		log.info("提取类型:"+title);
        		ArrayList<String> filenames=getAllFileNamesByType(title);
        		for(String filename:filenames) {
        			Map<String,Map<String,String>> qca=getSingleQA(filename,title);
        			Map<String,Object> qcaWithType=new HashMap<String,Object>();
        			qcaWithType.put(title, qca);
        			questionsExam.add(qcaWithType);
        		}
        	}        	
        }
		//原始套题生成子套题
		String cps=docProperties.get("diffcopies");
		Integer diffcopies=1;
		if (cps==null || cps.equals("")) {
			diffcopies=1;
		}else {
			diffcopies=Integer.parseInt(cps);
		}
		for(int i=0;i<diffcopies;i++) {
			params.put("${No}", NumToAlphabet.getAlphabet(i+1));
			String npath=newpath+"_"+NumToAlphabet.getAlphabet(i+1);
			//套题打印				  
			 xw.setExamTitle(npath,params);//打印分卷名
			 xw.setExamTitle(npath+"_答案",params);//打印分卷名（答案）
			 for(Map<String,String> titlemap:orderExam) {
				 for(String ctype:titlemap.keySet()) {
					 xw.setQuestionsTitle(npath,titlemap.get(ctype)+" ("+docProperties.get(ctype+"_total")+"分)");//打印试题
					 xw.setQuestionsTitle(npath+"_答案",titlemap.get(ctype)+" ("+docProperties.get(ctype+"_total")+"分)");//打印试题（答案）
										
					 List<Map<String,Map<String,String>>> listQcaByType=new ArrayList<Map<String,Map<String,String>>>();//当前类型的题目组
					 for(int j=0;j<questionsExam.size();j++) {
						 Map<String, Object> qcaWithType=(Map<String, Object>) (questionsExam.get(j));
						 for(String key:qcaWithType.keySet()) {
							 if (!key.equals(ctype)) {
								 continue;
							 }else {
								 Map<String,Map<String,String>> qca=(Map<String,Map<String,String>>) (qcaWithType.get(key));
								 listQcaByType.add(qca);			 
							 }
						 }						 
					 }
					 
					 //获取试题布局配置
					 String title_order_random=docProperties.get("title_order_random");
					 Boolean tor=false;
					 if(title_order_random!=null&&title_order_random.equals("true")) {
						 tor=true;
					 }
					 int nTotal=listQcaByType.size();
					 for(int k=0;k<nTotal;k++) {
						 int count=k+1; //试题序号
						 int nListQT=listQcaByType.size();
						 Random rd=new Random();
						 int index=k; //获取题目序号
						 if (tor) index=rd.nextInt(nListQT); //随机出题顺序号
						 Map<String,Map<String,String>> qca=listQcaByType.get(index);//获取题目
						 if (tor) listQcaByType.remove(index);
						 xw.setQuestions(npath,qca,Conf.getKeyByName(ctype),count);
					 }
				 }
			 }			 
		}	
		if (docProperties.get("avoid_used_title").equals("true")) {
			//记录用例哪些文档
			for(String x:tmpUsedList) {
				FlatFile.write(x.split(",")[0], x.split(",")[1]);
			}
		}		
		System.out.println(".end");
	}	
}
