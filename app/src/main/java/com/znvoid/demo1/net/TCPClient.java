package com.znvoid.demo1.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.daim.RequestHead;
import com.znvoid.demo1.util.TCPData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TCPClient {

	private String ip;
	private boolean isConn = false;
	private Socket socket;
	private int port = 12345;
	private OutputStream out;
	private String TAG = "socket thread";;
	public static final int CLIENT_SEND_SUCCSSED = 0X2003;
	public static final int CLIENT_SEND_FAIL = 0X2002;
	private BlockingQueue<Contact> msgQueue;
	Handler handler;

	public TCPClient(Handler handler) {

		this.handler = handler;
		msgQueue = new ArrayBlockingQueue<Contact>(20);

	}

	public void Start(final String ip) {
		this.ip = ip;
		if (!isConn) {

			new Thread() {
				@Override
				public void run() {
					try {
						initClient(ip);
					} catch (Exception e) {

						e.printStackTrace();
						Message msg = handler.obtainMessage();
						// msg.obj = mess;
						msg.what = CLIENT_SEND_FAIL;
						handler.sendMessage(msg);

						isConn = false;
						msgQueue.clear();
						try {
							out.close();
						} catch (Exception e1) {

							e1.printStackTrace();
						}

					}

				}
			}.start();

		}

	}

	private void initClient(String ip) throws Exception {

		Log.i(TAG, "initClient");

		socket = new Socket();
		SocketAddress remoteAddr = new InetSocketAddress(ip, port);
		socket.connect(remoteAddr, 3000);

		out = socket.getOutputStream();

		Log.i(TAG, "输入输出流获取成功");
		isConn = true;
		while (isConn) {
			Contact mContact = msgQueue.take();
			if (!isConn || mContact.getId().equals("NULL")) {
				Log.i(TAG, isConn + "------" + mContact.getId());
				break;
			}
			Log.i(TAG, "开始发送" + Thread.currentThread().getName());
			RequestHead requestHead = new RequestHead();
			requestHead.setheadParam("Content-Type", mContact.getMsgType());
			String mMsg = TCPData.makeSendDate(requestHead, mContact);
			if (mContact.getMsgType().equals("message/string")) {
				out.write(TCPData.preforData(mMsg, 2));
			} else if (mContact.getMsgType().equals("flie/picture")) {
				Log.e("Light", mContact.getLastMsg());
				out.write(TCPData.preforData(mMsg, 4));
			}

			out.flush();
			Message msg = handler.obtainMessage();
			msg.obj = mContact;
			msg.what = CLIENT_SEND_SUCCSSED;
			handler.sendMessage(msg);// 结果返回给UI处理
			Log.i(TAG, "发送成功");
		}
		Log.i(TAG, "线程结束" + Thread.currentThread().getName());

	}

	public void send(Contact contact) throws Exception {
		if (msgQueue == null) {
			msgQueue = new ArrayBlockingQueue<Contact>(20);
		}
		msgQueue.add(contact);

	}

	public void stop() {
		stop(false);
	}

	public void stop(Boolean flag) {
		isConn = false;
		if (msgQueue != null) {

			msgQueue.clear();
			if (!flag) {
				msgQueue.add(new Contact("NULL", "NULL", "NULL", "NULL"));
			}
			//
		}

		try {
			if (socket != null) {
				socket.close();
			}
			if (out != null) {
				out.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket = null;
		out = null;
	}

	public void changeIp(String ip) {
		stop(true);
		Start(ip);
	}

	public boolean isConn() {
		return isConn;
	}

}
