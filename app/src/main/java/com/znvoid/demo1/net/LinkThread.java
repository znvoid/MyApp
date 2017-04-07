package com.znvoid.demo1.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.util.TCPData;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class LinkThread extends Thread {

	private Handler mHandler;
	private Context context;
	private String id;
	private String ip;
	public static final int LINK_SUCCESED = 0X5001;
	public static final int LINK_FAIL = 0X5002;
	public static final int LINK_CLASH = 0X5003;

	public LinkThread(Context context, Handler handler, Contact contact) {
		this.mHandler = handler;
		this.context = context;
		id = contact.getId();
		ip = contact.getIp();

	};

	@Override
	public void run() {

		conn(ip);
		super.run();
	}

	public void conn(String ip) {

		try {
			Log.e("Light", "开始连接" + ip);
			Socket client = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(ip, 12345);
			client.connect(remoteAddr, 2000);
			client.setSoTimeout(4000);
			Log.e("Light", "连接到" + ip);
			// Message msg = pHandler.obtainMessage();
			// msg.what = SEARCH_SUCCESS;
			// msg.obj=ip;
			// pHandler.sendMessage(msg);
			
			byte[] buffer = TCPData.preforData(TCPData.creatTestMessage(context), 1);
			OutputStream os = client.getOutputStream();
			os.write(buffer, 0, buffer.length);
			os.flush();

			InputStream in = client.getInputStream();
			Log.e("Light", "构建输入流完毕");
			byte[] buf = new byte[1024 * 4];
			int temp = 0;
			StringBuffer stringBuffer = new StringBuffer();
			while ((temp = in.read(buf)) != -1) {
				Log.e("Light", "收到服务器检查链接结果");

				stringBuffer.append(new String(buf, 0, temp));
				if (temp < 1024 * 4) {
					break;
				}

			}
			String re = stringBuffer.toString();
			re = re.replace("\\", "");
			Contact contact = TCPData.parseJsonConact(re);

			if (contact != null) {
				Log.e("Light", contact.getId());
				if (id.equals(contact.getId())) {
					Message rmsg = mHandler.obtainMessage();
					rmsg.what = LINK_SUCCESED;
					rmsg.obj = contact;
					mHandler.sendMessage(rmsg);
				} else {
					Message rmsg = mHandler.obtainMessage();
					rmsg.what = LINK_CLASH;
					rmsg.obj = contact;
					mHandler.sendMessage(rmsg);

				}

			}else {
				client.close();
				Message msg = mHandler.obtainMessage();
				msg.what = LINK_FAIL;
				msg.obj = ip;
				mHandler.sendMessage(msg);
			}
		

		} catch (Exception e) {
			Log.e("Light", "连接失败" + ip);
			Message msg = mHandler.obtainMessage();
			msg.what = LINK_FAIL;
			msg.obj = ip;
			mHandler.sendMessage(msg);//

		}

	}
}
