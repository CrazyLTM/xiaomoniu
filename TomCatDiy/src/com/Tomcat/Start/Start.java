package com.Tomcat.Start;


import java.io.InputStream;
import java.io.OutputStream;

public class Start {
    //将请求内容读到byte数组中并返回byte数组
	public byte[]  init(InputStream iStream) throws Exception {
		byte[] bs = new byte[65536];
		iStream.read(bs);
		return bs;
		}
	
}
