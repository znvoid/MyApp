package com.znvoid.demo1.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.znvoid.demo1.util.TCPData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
//未用
public class TCPServer extends Thread {
	/**
	 * 默认服务器端口
	 */
	private static final Integer DEFAULT_PORT = 12345;

	public static final int SERVER_RECEIVED_MESSAGE = 0x1001;
	public static final int SERVER_SEND_FAIL = 0x1002;
	public static final int SERVER_SEND_SUCCEED = 0x1003;
	public static final int SERVER_CLIENT_UNCOON = 0x1004;
	public static final int SERVER_STOP = 0x1005;
	public static final String CHATTEST = "CHATMESSAGE V1.0 CHATTEST";
	public final String TAG = "TCPServer";
	private Handler mHandler;
	
	private boolean isRun = true;
	
	

	ServerSocket serverSocket;
	
	

	//private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

	private Context context;

	public TCPServer(Context context,Handler mHandler) {
		super();
		this.context=context;
		this.mHandler = mHandler;

	}

	@Override
	public void run() {
		super.run();
		try {
			doit();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			//isRun = false;
			
			try {
				serverSocket.close();
				Log.e(TAG, "server stop");
				//handmessage("server stop", SERVER_STOP);
				
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	
	private void doit() throws Exception {

		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		 serverSocket = serverChannel.socket();

		serverSocket.bind(new InetSocketAddress(DEFAULT_PORT));

		serverChannel.configureBlocking(false);

		Selector selector = Selector.open();

		// Register the ServerSocketChannel with the Selector
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		while (isRun) {
			
			int n = selector.select();
		
			if (n == 0) {
				// nothing to do
				continue;
			}

			Iterator<SelectionKey> it = selector.selectedKeys().iterator();

			while(it.hasNext()){
			SelectionKey key = (SelectionKey) it.next();

			if (key.isAcceptable()) {
				Socket socket = ((ServerSocketChannel) key.channel()).accept().socket();
				Log.e(TAG, "接收到" + socket);
				SocketChannel sc = socket.getChannel();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
			} else if (key.isConnectable()) {
				Log.e(TAG, "Connectable");
				
				String ip=((SocketChannel) key.channel()).socket().getInetAddress().getHostAddress();
				
				//handmessage(ip, SERVER_CLIENT_UNCOON);
				
				
				
				key.cancel();
			} else if (key.isReadable()) {
				Log.e(TAG, "Readable");
				readDataFromSocket(key);

			} else if (key.isWritable()) {

			}
			it.remove();
			}
		}

	}

	private void readDataFromSocket(SelectionKey key) throws Exception {

		SocketChannel socketChannel = (SocketChannel) key.channel();
		if (!socketChannel.isConnected()) {
			return;
		}
		ByteBuffer buffer = (ByteBuffer)key.attachment();
		if (buffer == null) {
			buffer = ByteBuffer.allocate(4096);
			key.attach(buffer);
		}
		int count;
		//buffer.clear();
		//over:
		while ((count = socketChannel.read(buffer)) > 0) {
			buffer.flip(); // Make buffer readable
			while(true){
				buffer.mark();
				if(buffer.remaining()<=5){
					break;
				}
				int type = buffer.get();
				int length = buffer.getInt();
				if (buffer.remaining()>=length) {
					byte msgBytes[] = new byte[length];
					 String re=Charset.forName("UTF-8").decode(buffer.get(msgBytes,0,length)).toString();
						if (CHATTEST.equals(re)) {
							
							socketChannel.write(ByteBuffer.wrap(TCPData.creatTestMessage(context).getBytes()));
							key.cancel();
							
						}else {
							handmessage(re, SERVER_RECEIVED_MESSAGE);
						}
				}else{
					buffer.reset();
					break;
				}
				//buffer.
//				String re = Charset.forName("gbk").decode(buffer).toString();
				
			}
			buffer.compact();
			//buffer.clear(); // Empty buffer

		}
		
		
		if (count < 0) {
			// Close channel on EOF, invalidates the key
			socketChannel.close();
		}

	}

	private void handmessage(String text, final int type) {
		Message msg = mHandler.obtainMessage();
		msg.what = type;
		msg.obj = text;
		mHandler.sendMessage(msg);

	}
	
	
}
