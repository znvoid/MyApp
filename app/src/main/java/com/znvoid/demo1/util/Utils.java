package com.znvoid.demo1.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.znvoid.demo1.daim.ClientScanResultSO;
import com.znvoid.demo1.imf.DialogOnClick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
	private static  float scale;
	public static Bitmap getRes(Context context, String name) {
		ApplicationInfo appInfo = context.getApplicationInfo();
		int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
		return BitmapFactory.decodeResource(context.getResources(), resID);
	}

	public static ArrayList<ClientScanResultSO> checkClient(List<ClientScanResultSO> list) {
		
		if (list.size()==0) {
			return null;
		}
		
		ArrayList<ClientScanResultSO> result = getClientList();

		for (int i = 0; i < result.size(); i++) {

			for (int j = 0; j < list.size(); j++) {
				if (result.get(i).getIp().equals(list.get(j).getIp())) {
					result.set(i, list.get(j));
					break;

				}

			}

		}

		return result;
	}

	public static ArrayList<ClientScanResultSO> getClientList() {
		BufferedReader br = null;
		ArrayList<ClientScanResultSO> result = null;

		try {
			result = new ArrayList<ClientScanResultSO>();
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");

				if ((splitted != null) && (splitted.length >= 4)) {

					String mac = splitted[3];

					if (mac.matches("..:..:..:..:..:..") && !"00:00:00:00:00:00".equals(mac)) {

						result.add(new ClientScanResultSO(splitted[0], splitted[3], "head_0", false, "???"));
					}
				}
			}

		} catch (Exception e) {
			Log.e("getClientList", e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				Log.e("getClientList", e.getMessage());
			}
		}

		return result;
	}
	public static String getSysTime() {
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy年MM月dd日    HH:mm:ss");       
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
		String    str    =    formatter.format(curDate);
		return str;
	}
	
	
	public static void showDialog(final Context context,final DialogInterface.OnClickListener OnClickListener) {

		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确认删除吗？");

		builder.setTitle("提示");
		
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				OnClickListener.onClick(dialog, which);
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
	public static void showDialog(final Context context,String Message,final int eventid ,final DialogOnClick Listener) {
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(Message);
		
		builder.setTitle("提示");
		
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Listener.onClick(eventid);
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
	private static String Id;
	public static String getId(Context context) {
		if (Id==null) {

			String mac = new WifiUtil(context).getMacAddress();

			if (mac != null && !"NULL".equals(mac)) {
				String[] strings = mac.split(":");
				if (strings.length == 6) {
					return strings[0] + strings[1] + strings[2] + strings[3] + strings[4] + strings[5];
				} else {
					TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
					String d = (tm == null) ? "NULL" : tm.getDeviceId();

					Id= (d == null || d.matches("0+")) ? ("NULL" + (int) (Math.random() * 0x5f734d9)) : d;

				}

			} else {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String d = (tm == null) ? "NULL" : tm.getDeviceId();

				Id= (d == null || d.matches("0+")) ? ("NULL" + (int) (Math.random() * 0x5f734d9)) : d;
			}

		}
		return Id;

		
	}
	
	public static String getName(Context context) {
		SharedPreferences sp=context.getSharedPreferences("configs",Context. MODE_PRIVATE);
		
		
		return sp.getString("author", getId(context));
	}
	public static String getOtherName(Context context) {
		SharedPreferences sp=context.getSharedPreferences("configs",Context. MODE_PRIVATE);
		
		
		return sp.getString("other", "机器人");
	}
	public static String getHead(Context context) {
		SharedPreferences sp=context.getSharedPreferences("configs",Context. MODE_PRIVATE);
		return sp.getString("head", "head_1");
	}
	public static String getOtherHead(Context context) {
		SharedPreferences sp=context.getSharedPreferences("configs",Context. MODE_PRIVATE);
		return sp.getString("head_other", "head_1");
	}
	public static void init(Context context) {
		 scale = context.getResources().getDisplayMetrics().density;
	}
	 public static  int dip2px( float dpValue) {  
         
	        return (int) (dpValue * scale + 0.5f);  
	    }
	 public static int px2dip(float pxValue) {  
	         
	        return (int) (pxValue / scale + 0.5f);  
	    } 
}
