package com.Tomcat.Request;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;




public class RequestHandler {
	
	
	private static final String ENTER = "\r\n";
	private static final String SPACE = " ";
	
	//存放request数据
	HashMap<String, String> mapParam=new HashMap<>();
	
	//URL（不带参数）
    String urlmapping;
    
    
	String content="post request";
	 
	
	//静态资源处理函数
	public String StaticResourceHandler(String resource) {
		
		if (resource.equals("")) {
			resource="index.html";
		}
		if(resource.contains("/")) {
			resource=resource.replaceAll("/", "\\\\");
		}
		//得到webcontent路径
		String WebContentUrl=System.getProperty("user.dir")+"\\WebContent\\"+resource;
	
		
	    StringBuilder contextText = new StringBuilder();
	    
		InputStream is=null;
		int length=0;
		try {
			is = new FileInputStream(WebContentUrl);
			try {
				length = is.available();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] b = new byte[length];//把所有的数据读取到这个字节当中
			//声明一个int存储每次读取到的数据
			int i = 0;
			//定义一个记录索引的变量
			int index = 0;
			//循环读取每个数据
			try {
				is.read(b);
				//把字节数组转成字符串
				String fileData=new String(b);
				//context中为静态文件的内容
				contextText=new StringBuilder(fileData);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
		    //如果没有对应的静态文件则返回NO found page
			contextText.append("No Found Page");
			StringBuilder sb = new StringBuilder();
			// 通用头域begin
			sb.append("HTTP/1.1").append(SPACE).append("200").append(SPACE).append("OK").append(ENTER);
			sb.append("Server:myServer").append(SPACE).append("0.0.1v").append(ENTER);
			sb.append("Date:Sat," + SPACE).append(new Date()).append(ENTER);
			sb.append("Content-Type:text/html;charset=UTF-8").append(ENTER);
			sb.append("Content-Length:").append(contextText.toString().getBytes().length).append(ENTER);
			// 通用头域end
			sb.append(ENTER);// 空一行
			sb.append(contextText);// 正文部分
			return sb.toString();
		}
		
		
		StringBuilder sb = new StringBuilder();
		// 通用头域begin
		sb.append("HTTP/1.1").append(SPACE).append("200").append(SPACE).append("OK").append(ENTER);
		sb.append("Server:myServer").append(SPACE).append("0.0.1v").append(ENTER);
		sb.append("Date:Sat," + SPACE).append(new Date()).append(ENTER);
		sb.append("Content-Type:text/html;charset=UTF-8").append(ENTER);
		sb.append("Content-Length:").append(contextText.toString().getBytes().length).append(ENTER);
		// 通用头域end
		sb.append(ENTER);// 空一行
		sb.append(contextText);// 正文部分
		
		return sb.toString();
	
	
		
		
	}

	//动态资源
	public String  DynamicResourceHandler(String resource) {
		//一。GET请求解析
		if(resource.contains("?")) {
			int num=resource.indexOf("?");
			//temp为get请求参数的字符串
			String temp=resource.substring(num+1);
			//urlmapping 为请求的URL
			resource=resource.substring(0, num);
			urlmapping=resource;
			//参数（字符串类型）用&分隔成字符串数组
			String[] param=temp.split("&");
			//循环去除每个参数
			for(String Map_param:param) { 
				//每个参数用“=”号分隔 ，左边为Map_key，右边为Map_Value
				String[] paString=Map_param.split("=");
				 mapParam.put(paString[0], paString[1]);
			}
			
			try {
				//调用业务逻辑处理方法
				content=HandlerMapping(urlmapping,mapParam);
				//判断返回结果渲染指定的视图
				if(content.equals("login")) {
					return  StaticResourceHandler("User.html");
				}
				if(content.equals("redirect")) {
					return StaticResourceHandler("Error.html");
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuilder sb = new StringBuilder();
		// 通用头域begin
		sb.append("HTTP/1.1").append(SPACE).append("200").append(SPACE).append("OK").append(ENTER);
		sb.append("Server:myServer").append(SPACE).append("0.0.1v").append(ENTER);
		sb.append("Date:Sat," + SPACE).append(new Date()).append(ENTER);
		sb.append("Content-Type:text/html;charset=UTF-8").append(ENTER);
		sb.append("Content-Length:").append(content.toString().getBytes().length).append(ENTER);
		// 通用头域end
		sb.append(ENTER);// 空一行
		sb.append(content);// 正文部分
		return sb.toString();
	}

    //利用反射，根据请求的URL调用具体的方法
	public String HandlerMapping(String mapping,HashMap<String , String> map) throws Exception {
		
		 Class<?> Clazz =  Class.forName("com.Tomcat.MethodMapping.MappingHandler");
		 
		 Object object=Clazz.newInstance();
         //获取方法
         Method Method = Clazz.getMethod(mapping,HashMap.class);
         //调用
         String HandlerResult=  (String) Method.invoke(object,map);
		
	     return HandlerResult;
		}
	


}
