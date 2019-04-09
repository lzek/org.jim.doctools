package org.jim.doctools.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Conf {
	private static Map<String,String> cfType=new HashMap<String,String>();
	public Conf(){
		cfType.put("c_singleChoice", "单选");
		cfType.put("c_multipleChoice", "多选");
		cfType.put("c_indefiniteChoice", "不定项");
		cfType.put("c_completion", "填空");
		cfType.put("c_TFChoice", "判断");
		cfType.put("c_essayQuestion", "简答");
	}
	
	public final static String getName(String key) {
		return cfType.get(key);
	}
	
	public final static String getKeyByName(String Name) {
		for(String key:cfType.keySet()) {
			if (Name.equals(cfType.get(key))) return key;
		}
		return null;
	}
	
	public final static ArrayList<String> getKeyList() {
		ArrayList<String> result=new ArrayList<String>();
		for(String key:cfType.keySet()) {
			result.add(key);
		}
		return result;
	}
	
	public final static ArrayList<String> getNameList() {
		ArrayList<String> result=new ArrayList<String>();
		for(String key:cfType.keySet()) {
			result.add(cfType.get(key));
		}
		return result;
	}
}
