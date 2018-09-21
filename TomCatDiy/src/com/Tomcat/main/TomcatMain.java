package com.Tomcat.main;



import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.Tomcat.Request.RequestContent;
import com.Tomcat.Start.Start;

public class TomcatMain {
	
	public static void main(String[] args) throws Exception {
		
		//初始化start类
		Start start=new Start();
		//初始化requestcontent
		RequestContent requestContent=new RequestContent();
		//打开socket 端口
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(15389);
		while(true) {
		Socket client = ss.accept();
		//多线程操作
		Thread thread=new Thread(()-> {
			InputStream iStream;
			OutputStream oStream;
			try {
				iStream = client.getInputStream();
				oStream = client.getOutputStream();
				
				//调用Start函数：istream为输入流
				byte[] bytes=start.init(iStream);
				//得到URL请求
				String url=requestContent.requestContent(bytes);
				//判断URL请求
				String content=requestContent.requestResourceJudge(url);
				oStream.write(content.getBytes());
				iStream.close();
				oStream.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		thread.start();
		}
	

	}

	

}
