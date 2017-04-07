package com.znvoid.demo1.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.znvoid.demo1.WifiUtil;
import com.znvoid.demo1.util.TCPData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
//未用
public class TCPServerThread extends Thread {
	/**
	 * 默认服务器端口
	 */
	private static final Integer DEFAULT_PORT = 12345;

	/**
	 * 服务器Socket
	 */
	private static ServerSocket serverSocket;

	private Context context;

	/**
	 * 客户端Socket的MAP集
	 */
	private Map<String, Socket> socketMap;

	Handler mHandler;
	public static final int SERVER_RECEIVED_MESSAGE = 0x1001;
	public static final int SERVER_SEND_FAIL = 0x1002;
	public static final int SERVER_SEND_SUCCEED = 0x1003;
	public static final String CHATTEST = "CHATMESSAGE V1.0 CHATTEST";

	public boolean isRun = true;

	public TCPServerThread(Context context,Handler mHandler) {
		super();
		this.mHandler = mHandler;

	}

	/**
	 * 启用端口
	 * 
	 * @param port
	 */
	private void init(Integer port) {
		try {
			serverSocket = new ServerSocket(port);

			socketMap = new HashMap<String, Socket>();
		} catch (IOException e) {// 可能报端口占用的错误
			e.printStackTrace();
		}
	}

	/**
	 * 等待连接
	 * 
	 * @throws IOException
	 */
	private void waitConnect() throws IOException {
		System.out.println("=========等待连接=========");
		Socket clientSocket = serverSocket.accept();
		// receiveMessage(clientSocket);//先打印出信息在启动发送线程
		saveClientSocket(clientSocket);
		receiveMessage(clientSocket);
	}

	/**
	 * 保存socket
	 * 
	 * @param socket
	 */
	private void saveClientSocket(Socket socket) {
		String clientAddress = socket.getInetAddress().getHostAddress();
		socketMap.put(clientAddress, socket);

		// clientAddresses.add(clientAddress);
		System.out.println("++++++++++++客户端" + socket + "连接上服务器++++++++++++");

		// if(socketMap.size() == 1){
		// //startSend();
		// }
	}

	/**
	 * 接收消息
	 * 
	 * @param socket
	 */
	private void receiveMessage(Socket socket) {
		ReMesssage reMesssage = new ReMesssage(socket);
		reMesssage.start();

	}

	private class ReMesssage extends Thread {
		private Socket resocket;

		public ReMesssage(Socket resocket) {
			super();
			this.resocket = resocket;
		}

		@Override
		public void run() {

			Log.e("Light", "--------等待接收------");

			while (!resocket.isClosed()) {
				Log.e("Light", "--------" + resocket + "------");
				Log.e("Light", "--------开始接受------");
				try {
					InputStream inputStream = resocket.getInputStream();
					byte buffer[] = new byte[1024 * 1024];
					int temp = 0;
					Log.e("Light", "--------接受到1------");

					// 从InputStream当中读取客户端所发送的数据
					while ((temp = inputStream.read(buffer)) != -1) {
						String mesg = new String(buffer, 0, temp);
						if (CHATTEST.equals(mesg)) {//收到测试请求
							Log.e("Light", "收到测试请求");
							OutputStream os=resocket.getOutputStream();
							os.write(creatMsgForTest().getBytes());
							os.flush();
							
						} else {
							Message msg = new Message();
							msg.what = SERVER_RECEIVED_MESSAGE;
							msg.obj = mesg;
							mHandler.sendMessage(msg);
						}

					}

				} catch (IOException e) {
					Log.e("Light", "--------异常关闭------");
					try {
						resocket.close();
					} catch (IOException e1) {

						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}

			super.run();
		}

	}

	public void sendMessage(String ip, String msg) {
		Socket msocket = socketMap.get(ip);
		System.out.println("--fasong---" + msocket + "-----");
		if (msocket != null) {
			if (!msocket.isClosed()) {
				try {
					OutputStream os = msocket.getOutputStream();
					os.write(msg.getBytes());
					os.flush();
					Message msg1 = mHandler.obtainMessage();
					msg1.what = SERVER_SEND_SUCCEED;
					msg1.obj = msg;
					mHandler.sendMessage(msg1);

				} catch (IOException e) {

					e.printStackTrace();
				}
			} else {
				Log.e("Light", "closed");
				Message msg1 = mHandler.obtainMessage();
				msg1.obj = msg;
				msg1.what = SERVER_SEND_FAIL;
				mHandler.sendMessage(msg1);// 结果返回给UI处理
			}

		} else {

			Message msg1 = mHandler.obtainMessage();

			msg1.obj = msg;
			msg1.what = SERVER_SEND_FAIL;
			mHandler.sendMessage(msg1);// 结果返回给UI处理
		}

	}

	@Override
	public void run() {
		init(DEFAULT_PORT);
		boolean flagr = true;
		while (isRun) {
			try {
				waitConnect();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

	public void closeServer() {

		try {
			serverSocket.close();
			socketMap.clear();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public String creatMsgForTest() {
		String result;
		SharedPreferences sp = context.getSharedPreferences("configs", context.MODE_PRIVATE);

		sp.getString("head", "head");
		WifiUtil wifiUtil = new WifiUtil(context);

		result = TCPData.CHATMESSAGE + TCPData.DIV + sp.getString("author", "???") + TCPData.DIV
				+ sp.getString("head", "head") + TCPData.DIV + wifiUtil.getIP() +TCPData.DIV
				+ wifiUtil.getMacAddress();

		return result;
	}
}
