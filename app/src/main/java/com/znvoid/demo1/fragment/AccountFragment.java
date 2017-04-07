package com.znvoid.demo1.fragment;




import com.znvoid.demo1.R;
import com.znvoid.demo1.WifiUtil;
import com.znvoid.demo1.util.Utils;
import com.znvoid.demo1.view.CircleImageView;

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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


public class AccountFragment extends Fragment implements OnClickListener {
	private Context context;
	private CircleImageView cView;
	private CircleImageView cView_other;
	private EditText dText;
	private EditText dText_othr;
	private Button button;
	private GridView gridView;
	private GridView gridView_other;
	private String head;
	private String head_other;
	private SharedPreferences sharedPreferences;
	private int[] headids = { R.drawable.head_1, R.drawable.head_2, R.drawable.head_3, R.drawable.head_4,
			R.drawable.head_5, R.drawable.head_6, R.drawable.head_7, R.drawable.head_8, R.drawable.head_9,
			R.drawable.head_10, R.drawable.head_11 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context=getActivity();
		String mIP=new WifiUtil(context).getIP();
		sharedPreferences=context.getSharedPreferences("configs",context.MODE_PRIVATE);
		head=sharedPreferences.getString("head", "head_1");
		head_other=sharedPreferences.getString("head_other", "head_1");
		View view = inflater.inflate(R.layout.zhanghu, null);
		cView = (CircleImageView) view.findViewById(R.id.head);
		cView_other = (CircleImageView) view.findViewById(R.id.head_other);
		cView.setImageBitmap(Utils.getRes(context,sharedPreferences.getString("head", "head_1")));
		cView_other.setImageBitmap(Utils.getRes(context,sharedPreferences.getString("head_other", "head_1")));
		dText = (EditText) view.findViewById(R.id.dt_author);
		dText_othr = (EditText) view.findViewById(R.id.dt_other);
		dText.setText(sharedPreferences.getString("author", mIP));
		dText_othr.setText(sharedPreferences.getString("other", "机器人"));
		button=(Button) view.findViewById(R.id.bt_saveaccunt);
		button.setOnClickListener(this);
		gridView = (GridView) view.findViewById(R.id.gv_head);
		gridView_other = (GridView) view.findViewById(R.id.gv_head_other);
		gridView.setAdapter(new GvAdapt());
		gridView_other.setAdapter(new GvAdapt());
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cView.setImageResource(headids[position]);
				head="head_"+(position+1);
			}
		});
		
		gridView_other.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cView_other.setImageResource(headids[position]);
				head_other="head_"+(position+1);
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
			ImageView imageView=new ImageView(context);
			imageView.setImageResource(headids[position]);
			
			return imageView;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_saveaccunt:
			Editor editor=sharedPreferences.edit();
			editor.putString("head", head);
			editor.putString("head_other", head_other);
			editor.putString("author",dText.getText().toString().trim() );
			editor.putString("other",dText_othr.getText().toString().trim() );
			editor.commit();
			Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		
	}
}
