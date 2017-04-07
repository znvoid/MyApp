package com.znvoid.demo1.fragment;




import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.znvoid.demo1.R;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.util.WifiUtil;
import com.znvoid.demo1.view.CircleImageView;

/**
 * 账户界面
 * @author zn
 *
 */
public class AccountFragm extends Fragment implements OnClickListener {
	private Context context;
	private CircleImageView cView;

	private EditText dText;
	private TextView tv;
	
	private Button button;
	private GridView gridView;
	
	private String head;
	private Switch mSwitch;
	
	
	
	
	private SharedPreferences sharedPreferences;
	private int[] headids = { R.drawable.head_1, R.drawable.head_2, R.drawable.head_3, R.drawable.head_4,
			R.drawable.head_5, R.drawable.head_6, R.drawable.head_7, R.drawable.head_8, R.drawable.head_9,
			R.drawable.head_10, R.drawable.head_11 };
	private String mIP;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context=getActivity();
		mIP=new WifiUtil(context).getIP();
		
		sharedPreferences=context.getSharedPreferences("configs",context.MODE_PRIVATE);
		head=sharedPreferences.getString("head", "head_1");
		//head_other=sharedPreferences.getString("head_other", "head_1");
		View view = inflater.inflate(R.layout.account, null);
		cView = (CircleImageView) view.findViewById(R.id.account_head);
		
		cView.setImageBitmap(Utils.getRes(context,sharedPreferences.getString("head", "head_1")));
		
		dText = (EditText) view.findViewById(R.id.account_name);
		
		dText.setText(sharedPreferences.getString("author", mIP));

		button=(Button) view.findViewById(R.id.account_bt_save);
		button.setOnClickListener(this);
		tv=(TextView) view.findViewById(R.id.account_tv1);
		mSwitch=(Switch) view.findViewById(R.id.account_switch);
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					tv.setText("人物头像及昵称");
					head=sharedPreferences.getString("head", "head_1");
					dText.setText(sharedPreferences.getString("author", mIP));
					
				}else {
					head=sharedPreferences.getString("head_other", "head_1");
					dText.setText(sharedPreferences.getString("other", "机器人"));
					tv.setText("机器人头像及昵称");
				}
				cView.setImageBitmap(Utils.getRes(context,head));
			}
		});
		
		gridView = (GridView) view.findViewById(R.id.account_gv);
				gridView.setAdapter(new GvAdapt());
	
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cView.setImageResource(headids[position]);
				head="head_"+(position+1);
			}
		});
		
	
		
		
		
		return view;

	}

	class GvAdapt extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return headids.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return headids[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			
			View view=LayoutInflater.from(context).inflate(R.layout.accountitem, parent, false);
			ImageView imageView=(ImageView) view.findViewById(R.id.item_circleImageView);
			
			
//			ImageView imageView=new CircleImageView(context);
//			
//			LayoutParams params=new LayoutParams(100, 100);
//			imageView.setLayoutParams(params);
			imageView.setImageResource(headids[position]);
			
			return view;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.account_bt_save:
			
			Editor editor=sharedPreferences.edit();
			if (!mSwitch.isChecked()) {
				
				editor.putString("head", head);
				editor.putString("author",dText.getText().toString().trim() );
				
				
			} else {
				editor.putString("head_other", head);
				editor.putString("other",dText.getText().toString().trim() );
			}
				
			editor.commit();
			Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		
	}
	
}
