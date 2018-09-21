package com.Tomcat.Request;



public class RequestContent {
	
	
	RequestHandler requestHandler=new RequestHandler();
	
	 
    //从请求信息中截取请求的URL并封装在content中
	public String requestContent(byte[] bytes) {
		String content=new String(bytes);
		
		int begin=content.indexOf(" ");
		int end=content.indexOf(" ", begin+1);
		content=content.substring(begin+2, end);
		return content;
	}
	//比较URL，判断是静态资源还是动态资源
	public String requestResourceJudge(String resource) {
		
		if(resource.contains(".")||resource.equals("")){
			 return requestHandler.StaticResourceHandler(resource);
		}else {
			 return requestHandler.DynamicResourceHandler(resource);
		}
		
	}

}
