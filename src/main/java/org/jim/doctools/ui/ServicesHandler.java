package org.jim.doctools.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.jim.doctools.merge.MergerFolder;
import org.jim.doctools.util.Conf;
import org.jim.doctools.util.FlatFile;
import org.jim.doctools.util.docFile;

import net.sf.json.JSONObject;

@SuppressWarnings("serial")
public class ServicesHandler  extends HttpServlet {
	static Logger log = LogManager.getLogger(App.class);
	Bussiness BH=new Bussiness();
	Conf cf=new Conf();
	
	@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {		
        response.setContentType("text/html");
       Enumeration<String> pns=request.getParameterNames();
       String command="docProperties";
       while(pns.hasMoreElements()) {
    	   String parameterName = pns.nextElement();
    	   switch(parameterName) {
    	   		case "command":
    	   			command=request.getParameter(parameterName);
    	   			break;
    	   			default:
    	   				break;
    	   }
       }
       switch(command) {
       case "docProperties":
    	   responseOutWithJson(response,BH.getProperties(),true);
    	   break;
       case "getTemplateName":
    	   responseOutWithJson(response,BH.getTemplateName(),true);
    	   break;
       }
       
        //response.setStatus(HttpServletResponse.SC_OK);
       // response.getWriter().println("<h1>Hello from HelloServlet</h1><br><a href=\"nihao.html\">nihao</a>");
   }
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException {
		try{
		  JSONObject json=this.receivePost(request);
		  Iterator it = json.keys();  
		  HashMap<String, String> dp=BH.getProperties();
          while(it.hasNext()){  
              String key = (String) it.next();
              String databasePath;
			switch(key) {
              case "pickup":
            	  String filename= (String) ((JSONObject)(json.get(key))).get("path");
            	  BH.pickupTitle(filename);
            	  responseOutWithJson(response,"{\"info\":\"已成功抽取题目\"}",true);
            	  break;
              case "createpapers":            	  
            	  File dbf=new File(dp.get("database"));
            	  if (!dbf.exists()|| !dbf.isDirectory()) {
            		  throw new Exception("配置文件database指定的试题存放目录[ "+dp.get("database")+" ]不存在,请先用 [ 试卷抓题 ] 生成试题存放目录");
            	  }
            	  BH.createpapers();
            	  responseOutWithJson(response,"{\"info\":\"已生成试卷，请在程序目录下查看\"}",true);
            	  break;
              case "quchong":
            	  databasePath=BH.getProperties().get("database");
            	  int rcount=mergeTitleFolder(databasePath,databasePath);
            	  responseOutWithJson(response,"{\"info\":\"已清理["+databasePath+"]下 "+rcount+" 道 重复题目\"}",true); 
            	  break;
              case "pickupBatch":
            	  ArrayList<String>  jList= docFile.getAllFileList("试卷池",".txt");
            	  String nameString="";
            	  int count=1;
            	  for(String xname:jList) {
            		  BH.pickupTitle("试卷池/"+xname);
            		  if (nameString.equals("")) {
            			  nameString=(count++)+"、"+xname;
            		  }else {
            			  nameString=nameString+"   "+(count++)+"、"+xname;
            		  }            		  	   
            	  }
            	  responseOutWithJson(response,"{\"info\":\"已成功抽取试卷池的试卷:"+nameString+"\"}",true); 
            	  break;
              case "updateDocProperties": 
            	  JSONObject  ndpitem=(JSONObject) json.get(key);
            	  for(String dbkey:dp.keySet()) {
            		  Iterator nit = ndpitem.keys();  
            		  while(nit.hasNext()) {
            			  String ikey = (String) nit.next();
            			  if (dbkey.equals(ikey)) {
            				 String nv=(String)ndpitem.get(ikey);
            				  dp.put(dbkey, nv);
            			  }
            		  }
            	  }
            	  BH.setProperties(dp);
            	  responseOutWithJson(response,"{\"info\":\"已更新配置\"}",true);
            	  break;
              case "saveDocProperties":
            	  FlatFile.saveProperties(dp);
            	  responseOutWithJson(response,"{\"info\":\"已写入配置文件 app.properties\"}",true);
            	  break;
              case "clearUsedFlag":
            	  docFile.delFolder("./status/used");
            	  responseOutWithJson(response,"{\"info\":\"已执行清除\"}",true);
            	  break;
              default:  
            	  responseOutWithJson(response,"{\"info\":\"null\"}",true);
              }
          }           
		}catch(Exception e) {
			responseOutWithJson(response,"{\"error\":\""+e.getMessage()+"\"}",false);
		} 	      
	  }
	
	public  JSONObject receivePost(HttpServletRequest request) throws UnsupportedEncodingException, IOException  {
		// 读取请求内容
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		//将json字符串转换为json对象
		JSONObject json=JSONObject.fromObject(sb.toString());
		return json;
	}

    /** 
     * 以JSON格式输出 
     * @param response 
     */  
    protected void responseOutWithJson(HttpServletResponse response, 
            Object responseObject,Boolean status) {  
        //将实体对象转换为JSON Object转换  
        JSONObject responseJSONObject = JSONObject.fromObject(responseObject);  
        response.setCharacterEncoding("UTF-8");  
        response.setContentType("application/json; charset=utf-8");  
        if (status) {
        	response.setStatus(HttpServletResponse.SC_OK);
        }else {
        	response.setStatus(500);
        }
        PrintWriter out = null;  
        try {  
            out = response.getWriter();  
            out.append(responseJSONObject.toString());  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (out != null) {  
                out.close();  
            }  
        }  
    } 

	  private int mergeTitleFolder(String AFolder,String BFolder) throws Exception {
		  int result=0;
		  result=result+  MergerFolder.run(AFolder+"\\单选", BFolder+"\\单选", Float.parseFloat(BH.getProperties().get("单选_merge_per")));
		  result=result+ MergerFolder.run(AFolder+"\\多选", BFolder+"\\多选",Float.parseFloat( BH.getProperties().get("多选_merge_per")));
		  result=result+  MergerFolder.run(AFolder+"\\不定项", BFolder+"\\不定项", Float.parseFloat(BH.getProperties().get("不定项_merge_per")));
		  result=result+  MergerFolder.run(AFolder+"\\判断", BFolder+"\\判断", Float.parseFloat(BH.getProperties().get("判断_merge_per")));
		  result=result+  MergerFolder.run(AFolder+"\\简答", BFolder+"\\简答",Float.parseFloat( BH.getProperties().get("简答_merge_per")));
		  result=result+  MergerFolder.run(AFolder+"\\填空", BFolder+"\\填空", Float.parseFloat(BH.getProperties().get("填空_merge_per")));
		  result=result+  MergerFolder.run(AFolder+"\\未知", BFolder+"\\未知", Float.parseFloat(BH.getProperties().get("未知_merge_per")));
		  return result;
	  }
	
    protected void handle( String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello from HelloServlet2</h1>");
        baseRequest.setHandled(true);
    }
}
