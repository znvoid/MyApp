package com.znvoid.demo1.net;


import android.os.Handler;
import android.util.Log;

import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.daim.RequestHead;
import com.znvoid.demo1.util.FileUtils;
import com.znvoid.demo1.util.TCPData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Selector;

/**
 * 接受文件
 * 
 * @author zn
 *
 */
public class TCPClinetForFile {

	public static final int FILE_FAIL = 0X6001;
	public static final int FILE_SUCCESD = 0X6002;

	private int port = 12345;
	private String ip;
	private Contact mContact;// 服务接受到的，接收文件成功返回
	private Contact sContact;// 需发送的
	private Handler mHandler;
	private OutputStream outputStream;
	private InputStream in;
	private File file ;

	public TCPClinetForFile(Contact contact, Handler handler, Contact sContact) {

		this.ip = contact.getIp();
		mContact = contact;
		mHandler = handler;
		this.sContact=sContact;
		
	}

	public void strat() {
		
		new Thread(){@Override
		public void run() {
			
			try {
				initClient(ip);
				send();
				receive();
				
				
			} catch (Exception e) {
				if (file!=null) {
					file.delete();
				}
				
				Log.e("TAG", "TCPClinetForFile"+e.toString());
				mHandler.sendMessage(mHandler.obtainMessage(FILE_FAIL));
				
			}
			
			
			
			super.run();
		}}.start();
		
		
		
	}
	
	private void initClient(String ip) throws IOException {

		Selector.open();
		Socket nSocket = new Socket();
		
		
		SocketAddress remoteAddr = new InetSocketAddress(ip, port);
		nSocket.connect(remoteAddr, 3000);
		nSocket.setSoTimeout(9000);
		in = nSocket.getInputStream();
		// inr= new BufferedReader(new InputStreamReader(
		// nSocket.getInputStream())) ;

		outputStream = nSocket.getOutputStream();
		Log.e("socket thread", "TCPClinetForFile:获取双流成功");

	}

	private void send() throws Exception {
		RequestHead requestHead = new RequestHead();
		requestHead.setheadParam("Content-Type", "message/get");

		String mMsg = TCPData.makeSendDate(requestHead, sContact);
		
		outputStream.write(TCPData.preforData(mMsg, 3));
		outputStream.flush();
		Log.e("socket thread", "TCPClinetForFile:输出成功");
	}

	private void receive() throws Exception {

		Log.e("TAG", "开始接收");
		int length = 0;
		int total = 0;
		byte[] buffer = new byte[30];
		int i = in.read(buffer);
		Log.e("TAG", "读完首部"+ new String(buffer));
		if (i == 30) {
			String line = new String(buffer).trim();
			if (line.startsWith("File-Length:")) {
				total = Integer.parseInt(line.substring(12));
				Log.e("TAG","文件大小："+total);
				String path = mContact.getLastMsg();
				String fileName = path.substring(path.lastIndexOf("/") + 1);
				 file = FileUtils.creatFileStream(fileName);
				Log.e("TAG","文件路径"+ file.getAbsolutePath());
				mContact.setLastMsg(file.getAbsolutePath());
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024*4];
				int n = 0;
				while (total > length) {
					
					n = in.read(buf);
						length = length + n;
						Log.e("TAG","进度："+length/(total*1.0));
						fileOutputStream.write(buf, 0, n);
					

				}
				fileOutputStream.flush();
				fileOutputStream.close();
				mHandler.sendMessage(mHandler.obtainMessage(FILE_SUCCESD, mContact));
			}else {
				Log.e("TAG","首部不对");
				mHandler.sendMessage(mHandler.obtainMessage(FILE_FAIL));
			}
		}else {
			Log.e("TAG","buff!=30");
			mHandler.sendMessage(mHandler.obtainMessage(FILE_FAIL));
		}

	}

	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}
}
