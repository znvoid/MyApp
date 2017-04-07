package com.znvoid.demo1;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.znvoid.demo1.daim.Contact;
import com.znvoid.demo1.fragment.AccountFragm;
import com.znvoid.demo1.fragment.ChatFragment;
import com.znvoid.demo1.fragment.DeskFragment;
import com.znvoid.demo1.fragment.NetFragment;
import com.znvoid.demo1.fragment.ViewPagerFragment;
import com.znvoid.demo1.fragment.WifilistFragment;
import com.znvoid.demo1.net.TCPClinetForFile;
import com.znvoid.demo1.server.ServiceIssue;
import com.znvoid.demo1.server.TCPService;
import com.znvoid.demo1.sql.MsgSQL;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.util.WifiUtil;
import com.znvoid.demo1.view.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener {
	//侧滑布局DrawerLayout
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	//侧边菜单布局
	 NavigationView navigationView ;

	private ListView mDrawerList;
	private LinearLayout mDrawerView;
	//圆形头像 名称
	private CircleImageView cim;
	private TextView textView;
	private SharedPreferences sp;
	private Context context;
	private ChatFragment chatFragment=new ChatFragment();//聊天界面
	private ViewPagerFragment viewPagerFragment = new ViewPagerFragment();//ViewPagerFragment
	private WifilistFragment wifilistFragment = new WifilistFragment();//WiFi显示界面
	private NetFragment netFragment = new NetFragment();//局域网设备显示界面
	private AccountFragm accountFragment = new AccountFragm();//
	private DeskFragment deskFragment = new DeskFragment();//书桌Fragment
	private Fragment mContent = new Fragment();// 当前Fragment
	//
	private String mIP;//本机ip
	private String mTitle;//标题
	private WifiUtil wifiUtil;//WiFi工具类
	private Toolbar toolbar;
	private SearchView searchView;
	private Contact mContact;
	private MsgSQL sql;
	//广播接收
	private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == "com.zn.demo.CHATMESSAGEFILE") {
				Bundle bundle = intent.getExtras();
				Contact contact = (Contact) bundle.getSerializable("message");
				if (contact != null) {
					Log.e("TCPServer", "main接收到广播");
					mContact = contact;
					shoewDailog();
				}

			}

		}

	};
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TCPClinetForFile.FILE_FAIL:
				Log.e("TCPServer", "接收文件失败");
				Toast.makeText(context, "接收文件失败", Toast.LENGTH_SHORT).show();
				break;
			case TCPClinetForFile.FILE_SUCCESD:
				Log.e("TCPServer", "接收文件成功");
				Contact contact = (Contact) msg.obj;
				contact.setDirection(1);
				sql.add(contact);
				ServiceIssue.sendb(context, contact, null);
				Toast.makeText(context, "接收文件成功", Toast.LENGTH_SHORT).show();
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkWifi();
		initsetting();// 初始化设置
		startService(new Intent(this, TCPService.class));
	}

	private void checkWifi(){
		sp = getSharedPreferences("configs", MODE_PRIVATE);
		wifiUtil = new WifiUtil(this);
		if (wifiUtil.checkState()==WifiManager.WIFI_STATE_ENABLED){
			mIP = wifiUtil.getIP();
			if (sp.getString("ID", "NULL").equals("NULL")) {

				Editor editor = sp.edit();
				String id=Utils.getId(this);
				editor.putString("ID",id );
				editor.putString("author", id);
				editor.commit();

			}

		}else {
			showToast("未连接网络");
		}


	}
	private void initsetting() {

		context = this;
		//初始化数据库
		sql=new MsgSQL(context);
		//ImageLoader初始化设置
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration  
                .createDefault(this);  
        ImageLoader.getInstance().init(configuration);  
		toolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		 navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView != null) {
			navigationView.setNavigationItemSelectedListener(this);
		}
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.openDrawerContent,
				R.string.closeDrawerContent) {
			@Override
			public void onDrawerOpened(View drawerView) {
				mTitle = (String) toolbar.getTitle();

				toolbar.setTitle("菜单");
				invalidateOptionsMenu();
				cim.setImageBitmap(Utils.getRes(context, sp.getString("head", "head_1")));
				textView.setText(sp.getString("author", "ID:" + Utils.getId(context)));
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				if ("菜单".equals(getSupportActionBar().getTitle())) {

					toolbar.setTitle(mTitle);
				}

				invalidateOptionsMenu();
				super.onDrawerClosed(drawerView);
			}

		};
		mDrawerLayout.addDrawerListener(mDrawerToggle);
		//设置头像及名称
		cim = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
		textView = (TextView) navigationView.getHeaderView(0). findViewById(R.id.profile_tv);
		cim.setImageBitmap(Utils.getRes(context, sp.getString("head", "head_1")));
		textView.setText(sp.getString("author", "ID:" + Utils.getId(context)));

		switchContent(chatFragment);
		switchContent(viewPagerFragment);
		//注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.zn.demo.CHATMESSAGEFILE");
		LocalBroadcastManager.getInstance(context).registerReceiver(messageReceiver, filter);


		// 设置头像图片点击事件监听
		cim.setOnClickListener(this);
	}
//显示Toast
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.profile_image://头像被点击
			showToast("更换头像吗");
			break;

		default:
			break;
		}
	}
	/*
	 * 切换fragment，
	 */
	public void switchContent(Fragment fragment) {
		
		if (mContent != fragment) {

			if (mContent == viewPagerFragment) {

				chatFragment = (ChatFragment) getFragmentManager().findFragmentByTag(ChatFragment.class.getName());
//
				if (chatFragment != null) {
					//getFragmentManager().popBackStack();
					if (!chatFragment.isHidden()) {
						mContent = chatFragment;
					}
				}


			}

			if (!fragment.isAdded()) {
				getFragmentManager().beginTransaction().hide(mContent)
						.add(R.id.mian_frame, fragment, fragment.getClass().getName()).commit();

			} else {
				getFragmentManager().beginTransaction().hide(mContent).show(fragment).commit();
			}

			mContent = fragment;

		}else if (fragment==viewPagerFragment) {
			if (chatFragment!=null&&!chatFragment.isHidden()) {
				getFragmentManager().beginTransaction().hide(chatFragment).show(fragment).commit();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		MenuItem item = menu.findItem(R.id.action_websearch);
		searchView = (SearchView) MenuItemCompat.getActionView(item);
		final ImageView searchView_close = (ImageView) searchView.findViewById(R.id.search_close_btn);
		searchView.setQueryHint("百度搜索");
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String text) {

				webSercher(text);
				// searchView.clearFocus();
				searchView_close.performClick();
				searchView_close.performClick();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {

				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(navigationView);
		menu.findItem(R.id.action_websearch).setVisible(!isDrawerOpen);

		return super.onPrepareOptionsMenu(menu);
	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * 打开浏览器并进行百度搜索
	 * @param string 要搜索的内容
     */
	private void webSercher(String string) {
		if ("".equals(string)) {
			return;
		}

		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri uri = Uri.parse("http://www.baidu.com/s?wd=" + string);
		intent.setData(uri);
		startActivity(intent);


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// 退出程序，如果聊天界面显示则切换到viewPagerFragment
			if (!chatFragment.isHidden()) {
				switchContent(viewPagerFragment);
				return true;
			}

			
		}
		return super.onKeyDown(keyCode, event);
	}

	//是否接收文件的提示对话框
	public void shoewDailog() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("来自" + mContact.getName() + "(ID:" + mContact.getId() + ")");

		builder.setTitle("是否接收文件");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Contact sContact = new Contact(Utils.getId(context), Utils.getName(context), Utils.getHead(context),
						mContact.getLastMsg(), mContact.getTime(), mIP, mContact.getMsgType(), mContact.getDirection());

				new TCPClinetForFile(mContact, mHandler, sContact).strat();

				dialog.dismiss();

			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()){
			case R.id.drawlayout_menu_account:
				toolbar.setTitle("账户");
				switchContent(accountFragment);
				break;
			case R.id.drawlayout_menu_chat:
				toolbar.setTitle("聊天");
				switchContent(viewPagerFragment);
				break;
			case R.id.drawlayout_menu_wifi:
				toolbar.setTitle("wifi");
				switchContent(wifilistFragment);
				break;
			case R.id.drawlayout_menu_lan:
				toolbar.setTitle("局域网");
				switchContent(netFragment);
				break;
			case R.id.drawlayout_menu_read:
				toolbar.setTitle("书桌");
				switchContent(deskFragment);
				break;

		}
		showToast(item.getTitle() + "被点击");
		mDrawerLayout.closeDrawer(navigationView);

		return true;
	}
}
