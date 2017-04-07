package com.znvoid.demo1.net;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Ping {
	private boolean isOver = false;
	

	public void pingAll(String hostAddress) {
		// 首先得到本机的IP，得到网段

		int k = 0;
		k = hostAddress.lastIndexOf(".");
		String ss = hostAddress.substring(0, k + 1);
		for (int i = 2; i <= 254; i++) {
			// 遍歷所有局域网Ip
			String iip = ss + i;
			if (!hostAddress.equals(iip)) {
				MyThreadPool.getInstance().submit(new ping(iip));
			}
		}

		

	}



	


	public boolean isEnd() {
		return !isOver;
	}
	class ping implements Runnable{
		private final String ip;
		public ping(String ip) {
			this.ip=ip;
		}
		@Override
		public void run() {
			byte[] sendBuf = "HF-A11ASSISTHREAD".getBytes();
			try {
				DatagramSocket sendSocket = new DatagramSocket();
				InetAddress broadcastAddress = InetAddress.getByName(ip);
				DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 48899);
				sendSocket.send(sendPacket);
				sendSocket.close();
				Log.e("TAG", ip);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
