package com.znvoid.demo1.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.znvoid.demo1.R;
import com.znvoid.demo1.adapt.MyChatAapter;
import com.znvoid.demo1.daim.Chat;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.daim.ImageBean;
import com.znvoid.demo1.net.LinkThread;
import com.znvoid.demo1.net.Ping;
import com.znvoid.demo1.net.SearchThread;
import com.znvoid.demo1.net.TCPClient;
import com.znvoid.demo1.net.TCPClinetForFile;
import com.znvoid.demo1.popup.SelectorPopup;
import com.znvoid.demo1.popup.SelectorPopup.CallbackListener;
import com.znvoid.demo1.sql.MsgSQL;
import com.znvoid.demo1.util.TCPData;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.util.WifiUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment implements OnClickListener, CallbackListener {

	private ListView listView;
	private ImageButton sendButton;
	private ImageButton showpButton;
	private EditText messageInputEdi;
	private MyChatAapter adapt;
	private Context context;
	private ProgressDialog progress;

	private String myIP;
	private String myid;
	private MsgSQL sqlOpenHelp;
	private TCPClient tcpClient;
	// MyConn conn;

	private boolean canLink = false;
	private static final String MESSAGE_NOTIFICATION = "com.zn.demo.CHATMESSAGE";
	private static final int TIME_OUT = 0X12;
	
	private final Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case TCPClient.CLIENT_SEND_FAIL:
				Log.e("Light", "发送失败");
				canLink = false;
				Toast.makeText(context, "发送失败！", Toast.LENGTH_SHORT).show();
				break;
			case TCPClient.CLIENT_SEND_SUCCSSED:

				Contact contact = (Contact) msg.obj;
				contact.setId(mContact.getId());
				contact.setHead(mContact.getHead());
				contact.setIp(mContact.getIp());
				contact.setName(mContact.getName());
				Chat chat1 = TCPData.contact2Chat(contact);
				if (chat1 != null) {
					// chat1.setDirection(0);
					chat1.setAuthor(Utils.getName(context));
					
					adapt.add(chat1);
					sqlOpenHelp.add(contact);
				}

				break;
			case LinkThread.LINK_SUCCESED:
				canLink = true;
				 progress.dismiss();
				Contact contact3 = (Contact) msg.obj;
				mContact.setName(contact3.getName());
				mContact.setHead(contact3.getHead());
				tcpClient.changeIp(mContact.getIp());
				Toast.makeText(context, "测试连接成功！可以开始了", Toast.LENGTH_SHORT).show();
				break;
			case LinkThread.LINK_FAIL:
				// System.out.println("LINK_FAIL");
				progress.dismiss();
				refesh();
				progress.setTitle("测试失败，开始尝试搜索......");
				progress.show();

				break;
			case LinkThread.LINK_CLASH:
				refesh();
				progress.setTitle("ip冲突，开始尝试搜索......");
				progress.show();

				break;
			case TIME_OUT:
				new SearchThread(TCPData.creatTestMessage(context), mhandler).start();
			
				break;
				
			case SearchThread.SEARCH_FINSH:
				progress.dismiss();
				List<Contact> list = (List<Contact>) msg.obj;

				if (list.size() > 0) {

					for (Contact contact2 : list) {

						if (contact2.getId().equals(mContact.getId())) {
							mContact.setIp(contact2.getIp());
							mContact.setName(contact2.getName());
							mContact.setHead(contact2.getHead());
							canLink = true;
							tcpClient.changeIp(mContact.getIp());
							Toast.makeText(context, "搜索成功", Toast.LENGTH_LONG).show();
							break;
						}
					}

				}
				if (!canLink) {
					Toast.makeText(context, "搜索失败，对象不在服务区", Toast.LENGTH_LONG).show();
				}
				break;
				case 0x45://图片搜索
					progress.dismiss();
					if (dataList.isEmpty()) {
						break;
					}
					
					selectorPopup.show(dataList);
					
					break;
			}
		};

	};
	private Intent intent;
	private TCPClinetForFile clinet;
	private Contact mContact;
	private boolean isRefesh;
	private SelectorPopup selectorPopup;
	private List<ImageBean> dataList=new ArrayList<ImageBean>();


	
	public void handleResult(Contact contact) {
		if (!mContact.getId().equals(contact.getId())) {
			return;
		}
		
		this.canLink=true;
		mContact.setIp(contact.getIp());
		mContact.setHead(contact.getHead());
		mContact.setName(contact.getName());
		System.out.println("handleResult");
		adapt.add(TCPData.contact2Chat(contact));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		context = getActivity();
		selectorPopup = new SelectorPopup(getActivity());
		selectorPopup.setCallbackListener(this);
		View view = inflater.inflate(R.layout.chatframgment, null);

		// 获取本机ip
		WifiUtil wifiutil = new WifiUtil(context);
		myIP = wifiutil.getIP();
		myid = Utils.getId(context);
		// 创建tcpsServer
		// tcpServerThread = new TCPServerThread(context,mhandler);
		tcpClient=new TCPClient(mhandler);
		

		if (null == progress) {
			progress = new ProgressDialog(context);
		}
		
		sqlOpenHelp = new MsgSQL(context);
		messageInputEdi = (EditText) view.findViewById(R.id.messageInput);
		sendButton = (ImageButton) view.findViewById(R.id.sendButton);

		showpButton=(ImageButton) view.findViewById(R.id.showP);
		showpButton.setOnClickListener(this);
		listView = (ListView) view.findViewById(R.id.listchat);
		

		sendButton.setOnClickListener(this);
		
		return view;
	}

	public void beginToShow(Contact contact,boolean canLink) {
		this.canLink=canLink;
		mContact=contact;
		if (adapt==null) {
			adapt = new MyChatAapter(getActivity(), mContact.getId());
			sqlOpenHelp = new MsgSQL(context);
			adapt.setdata(sqlOpenHelp.loadMsg(mContact));
			listView.setAdapter(adapt);
		}else {
			adapt.setMId(mContact.getId());
			adapt.setdata(sqlOpenHelp.loadMsg(mContact));
		}
		
		if (!myid.equals(mContact.getId())) {
			if (!canLink) {
				linkService();
			}else {
				tcpClient.changeIp(contact.getIp());
			}
			
		}
		
		
	}
	
	public void linkService() {
		new LinkThread(context, mhandler, mContact).start();
		progress.setTitle("正在尝试连接...");
		progress.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.sendButton:// 发送按钮 事件
			if (messageInputEdi.getText().toString().trim().equals("")) {
				break;
			}
			Log.e("TCPServer", myid+"-------"+myIP);
			if (myid.equals(mContact.getId())) {
				String tString=messageInputEdi.getText().toString().trim();
				if (tString.equals("#")) {
					break;
				}
				Contact contact1=makeSendMsg();
				
				contact1.setName(Utils.getOtherName(context));
				contact1.setHead(Utils.getOtherHead(context));
				
				
				if (tString.startsWith("#")) {
					contact1.setLastMsg(tString.substring(1));
					contact1.setDirection(1);
					
					
				}else {
					
					contact1.setDirection(0);
					
				}
				sqlOpenHelp.add(contact1);
				adapt.add(TCPData.contact2Chat(contact1));
				
				messageInputEdi.setText("");
				
				break;
			}else {
				startSendMsg(makeSendMsg());
			}

			break;
			case R.id.showP:
				getImages();
				break;
				
		}

	}

	private void startSendMsg(Contact contact){
		if (!canLink) {
			linkService();
			return;
		}
		if (!tcpClient.isConn()) {
			tcpClient.Start( mContact.getIp());
		}
//		new TCPClientThread(mhandler, contact,mContact.getIp()).start();
		//tcpClient.Start( mContact.getIp());
		try {
			tcpClient.send(contact);
		} catch (Exception e) {
			Toast.makeText(context, "发送队列满了", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		messageInputEdi.setText("");
	
	}

	// class MyConn implements ServiceConnection
	
	

	//
	// // 绑定一个服务成功的时候 调用 onServiceConnected
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// ISevice iService = (ISevice) service;
	// if (iService != null) {
	//
	// iService.setHandlerr(mhandler);
	// }
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	//
	// }
	// }

	public void refesh() {
		if (!isRefesh) {
			isRefesh = !isRefesh;
			new Thread() {

				public void run() {
					Ping ping = new Ping();
					ping.pingAll(myIP);
				};
			};

			mhandler.sendEmptyMessageAtTime(TIME_OUT, 2000);
		}

	}

	@Override
	public void onDestroy() {
		tcpClient.stop();
		super.onDestroy();
	}
	
	
	public Contact makeSendMsg() {
		String id = Utils.getId(context);
		String name = Utils.getName(context);
		String head = Utils.getHead(context);
		String lastMsg = messageInputEdi.getText().toString().trim();
		String time = Utils.getSysTime();
		String msgType = "message/string";
		int direction = 0;

		return new Contact(id, name, head, lastMsg, time, myIP, msgType, direction);
		
	}
	
	private void getImages()
    {
		dataList.clear();
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(context, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        progress.setTitle("正在加载图片...");
		progress.show();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

               

                // 只查询jpeg和png的图片

//                        MediaStore.Images.Media.DATE_MODIFIED);

                String str[] = { MediaStore.Images.Media._ID,
						MediaStore.Images.Media.DISPLAY_NAME,
						MediaStore.Images.Media.DATA};
				Cursor mCursor = getActivity().getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, str,
						null, null, null);
                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext())
                {
                    

                	String path=mCursor.getString(2);
                  //  Log.e("TAG", path);
                    dataList.add(new ImageBean(path));
  
                }
                mCursor.close();

                // 通知Handler扫描图片完成
                mhandler.sendEmptyMessage(0x45);

            }
        }).start();

    }

	@Override
	public void onComplete(List<String> list) {
		String msgSting=list.get(list.size()-1);
		sendPicture(msgSting);
		
	
		
	}
	private void sendPicture(String path){
		
		Contact pcontact=makeSendMsg();
		pcontact.setLastMsg(path);
		pcontact.setMsgType("flie/picture");
		
		if (myid.equals(mContact.getId())) {
			
			Chat chat=	TCPData.contact2Chat(pcontact);
			adapt.add(chat);
			sqlOpenHelp.add(pcontact);
		}else {
			//发送
//			new TCPClientThread(mhandler,pcontact, mContact.getIp()).start();
			if (!tcpClient.isConn()) {
				tcpClient.Start( mContact.getIp());
			}
			try {
				tcpClient.send(pcontact);
			} catch (Exception e) {
				Toast.makeText(context, "发送队列满了", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}	
		
		
	
	}
	
}
