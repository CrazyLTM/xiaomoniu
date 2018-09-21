package com.Tomcat.MethodMapping;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class MappingHandler {
	
	 HashMap<String, String> hashMap;
	 HashMap<String, String> ProMap= new HashMap<>();;
	 
	 
	 public HashMap<String, String> getHashMap() {
		return hashMap;
	}


	public void setHashMap(HashMap<String, String> hashMap) {
		this.hashMap = hashMap;
	}

    //处理login请求，根据用户名和密码返回对应的结果
	public String login(HashMap<String , String> mapParams)  {
		   
		   Properties properties=new Properties();
		   try {
			String filepath=System.getProperty("user.dir")+"\\src\\conf.properties";
			InputStream iStream = new BufferedInputStream(new FileInputStream(filepath));
			properties.load(iStream);
			Iterator<String> it = properties.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				ProMap.put(key, properties.getProperty(key));
			}
			iStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		   setHashMap(mapParams);
		   String accout=hashMap.get("accout");
		   String password=hashMap.get("password");
		   Boolean boolean1=password.equals(ProMap.get(accout));
		   if(boolean1)  return "login";
		   return "redirect";
	}


	
	

}
