package com.znvoid.demo1.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.znvoid.demo1.R;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.daim.RequestHead;
import com.znvoid.demo1.sql.MsgSQL;
import com.znvoid.demo1.util.TCPData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TCPService extends Service {

	private Handler mHandler;

	private Context context;

	ServerSocket serverSocket;
	Selector selector;

	private MsgSQL sqlOpenHelp;

	private SoundPool soundPool;
	private int soundId;
	@Override
	public void onCreate() {
		Log.e("TCPServer", "onCreate");
		soundPool= new SoundPool(4, AudioManager.STREAM_SYSTEM,5);
		soundId= soundPool.load(getApplicationContext(), R.raw.mr, 1);
		context = getApplicationContext();
		sqlOpenHelp = new MsgSQL(context);
		new TCPThread().start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.e("TCPServer", "onDestroy");
		try {
			serverSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		Log.e("TCPServer", "bandign");
		return new MyIBiner();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.e("TCPServer", "unbindService");

		return super.onUnbind(intent);
	}

	class MyIBiner extends Binder implements ISevice {

		@Override
		public void setHandlerr(Handler handler) {

			addHandler(handler);
		}

	}

	private void addHandler(Handler mHandler) {

		this.mHandler = mHandler;
	}

	private void clearHandler() {
		mHandler = null;
	}

	class TCPThread extends Thread {
		/**
		 * 默认服务器端口
		 */

		private final int DEFAULT_PORT = 12345;
		public static final String CHATTEST = "CHATMESSAGE V1.0 CHATTEST";
		public final String TAG = "TCPServer";

		private boolean isRun = true;
		private int time=0;
		private ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 4);
		public TCPThread() {
			super();

		}

		@Override
		public void run() {
			super.run();
			while (time<5) {
				try {
					begin:
					doit();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					// isRun = false;

					try {
						serverSocket.close();
						Log.e(TAG, "server stop");
						// handmessage("server stop", SERVER_STOP);

					} catch (IOException e1) {

						e1.printStackTrace();
					}
					e.printStackTrace();
					time--;
					Log.e(TAG, "server restart "+time);
				}
			}
		}

		private void doit() throws Exception {

			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverSocket = serverChannel.socket();

			serverSocket.bind(new InetSocketAddress(DEFAULT_PORT));

			serverChannel.configureBlocking(false);

			selector = Selector.open();

			// Register the ServerSocketChannel with the Selector
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			while (isRun) {

				int n = selector.select();

				if (n == 0) {
					// nothing to do
					continue;
				}

				Iterator<SelectionKey> it = selector.selectedKeys().iterator();

				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();

					if (key.isAcceptable()) {
						Socket socket = ((ServerSocketChannel) key.channel()).accept().socket();
						Log.e(TAG, "接收到" + socket);
						SocketChannel sc = socket.getChannel();
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
					} else if (key.isConnectable()) {
						Log.e(TAG, "Connectable");

						String ip = ((SocketChannel) key.channel()).socket().getInetAddress().getHostAddress();

						// handmessage(ip, SERVER_CLIENT_UNCOON);

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
			int count;
			buffer.clear();

			while ((count = socketChannel.read(buffer)) > 0) {
				buffer.flip(); // Make buffer readable
				if (buffer.remaining() <= 5) {
					break;
				}
				int type = buffer.get();
				int length = buffer.getInt();
				Log.e(TAG, "接收到" + type+" changdu:"+length);
				if (buffer.remaining()>=length) {

					byte msgBytes[] = new byte[length];
					buffer.get(msgBytes, 0, length);
					String re = new String(msgBytes, "UTF-8");
					re = re.replace("\\", "");
					
					
					Log.e(TAG, "接收到" + re);
					RequestHead requestHead = TCPData.parseJsonHead(re);
					if (requestHead == null) {
						break;
					}
					Contact contact = TCPData.parseJsonConact(re);

					if (type==1&&requestHead.getheadParam("Content-Type").endsWith("message/test")) {
						Log.e(TAG, "接收到ok");
						if (contact != null) {
							sqlOpenHelp.updataContact(contact);
							socketChannel.write(ByteBuffer.wrap(TCPData.creatTestMessage(context).getBytes()));
							key.cancel();
						}

					} else if (type==2&&requestHead.getheadParam("Content-Type").endsWith("message/string")) {

						contact.setDirection(1);
						sqlOpenHelp.add(contact);

						// 发送广播
						sendb(contact);

					} else if (type==3&&requestHead.getheadParam("Content-Type").endsWith("message/get")) {
						Log.e(TAG, "文件获取");
					if(	sqlOpenHelp.findMsg(contact)){
						Log.e(TAG, "能获取");
						String fileName=contact.getLastMsg();
						Log.e(TAG, fileName);
						File file=new File(fileName);
						
						byte[] head=ServiceIssue.fileLenghtDecoed(file.length());
						ByteBuffer mBuffer=ByteBuffer.allocate(1024*4);
						byte[] bs=new byte[1024*4];
						socketChannel.write(ByteBuffer.wrap(head,0,30));
						FileInputStream fileInputStream=new FileInputStream(file);
						Log.e(TAG, "文件大小"+fileInputStream.available());
						Log.e(TAG, "开始发送");
						int total=0;
						int temp=0;
						while ((temp=fileInputStream.read(bs))!=-1) {
							total=total+temp;
							Log.e(TAG, "发送....."+total);
							mBuffer.clear();
							mBuffer.put(bs);
							mBuffer.flip();
							while(mBuffer.hasRemaining()) {
								socketChannel.write(mBuffer);
							}

						
							
						}
						fileInputStream.close();	
						Log.e(TAG, "发送文件结束");
						key.cancel();	
						
						
					}else {
						Log.e(TAG, "不能获取");
						key.cancel();	
					}
				
					}else if (type==4&&requestHead.getheadParam("Content-Type").endsWith("flie/picture")) {
						Log.e(TAG, "接收到图片");
						ServiceIssue.sendb(context, contact, "com.zn.demo.CHATMESSAGEFILE");
					}


				}
				buffer.clear(); // Empty buffer

			}
			if (count < 0) {
				// Close channel on EOF, invalidates the key
				socketChannel.close();
			}

		}

		}

		/**
		 * 发送广播
		 * 
		 * @param contact
		 */
		public void sendb(Contact contact) {
			Intent intent = new Intent("com.zn.demo.CHATMESSAGE");
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("message", contact);
			intent.putExtras(mBundle);
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			soundPool.play(soundId, 1, 1, 0, 0, 1);
		}

	}

