package com.znvoid.demo1.fragment;

import java.io.File;
import java.util.ArrayList;

import com.znvoid.demo1.BookReadingActivity;
import com.znvoid.demo1.R;
import com.znvoid.demo1.adapt.BookDeskAdapt;
import com.znvoid.demo1.daim.BookImf;
import com.znvoid.demo1.sql.BooksSqlOpenHelp;
import com.znvoid.demo1.util.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class DeskFragment extends Fragment {
	private GridView gridView;
	private BookDeskAdapt adapt;
	private BooksSqlOpenHelp sqlOpenHelp;
	private Context Context;
	private int FILE_SELECT_CODE = 0x10001;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Context = getActivity();
		View view = inflater.inflate(R.layout.book_desk, null);
		gridView = (GridView) view.findViewById(R.id.bookShelf);
		adapt = new BookDeskAdapt(Context);

		gridView.setAdapter(adapt);
		sqlOpenHelp = new BooksSqlOpenHelp(Context);
		setClickListener();
		readData();
		return view;
	}

	public void setClickListener() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BookImf bookImf = (BookImf) adapt.getItem(position);
				if (bookImf.getPath().equals("deful")) {
					// 调用自定义文件管理
					// Intent intent=new Intent(Context,
					// FileListActivity.class);
					// startActivity(intent);
					importExcel();

				} else {

					/*
					 * 暂时不用 Intent intent=new Intent(Context,
					 * BookpageActivity.class); intent.putExtra("path",
					 * bookImf.getPath()); startActivity(intent);
					 */
					if (new File(bookImf.getPath()).exists()) {
						Intent intent=new Intent(Context,BookReadingActivity.class);
						intent.putExtra("path",bookImf.getPath()); 
						startActivity(intent);
					}else {
						Toast.makeText(Context, "文件不存在了！！！", 0).show();
					}
					
				}

			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final BookImf bookImf = (BookImf) adapt.getItem(position);
				if (bookImf.getPath().equals("deful")) {

				} else {

					Utils.showDialog(getActivity(), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							sqlOpenHelp.delete(bookImf);
							readData();

						}
					});

				}

				return false;
			}
		});
	}

	@Override
	public void onResume() {

		readData();
		super.onResume();
	}

	public void readData() {
		ArrayList<BookImf> books = sqlOpenHelp.loadall();

		if (books != null) {
			adapt.setdata(books);
		}

	}

	/*
	 * 调用系统文件管理器
	 */
	private void importExcel() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		// intent.setDataAndType(Uri.fromFile(new File("/sdcard")), "*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(intent, FILE_SELECT_CODE);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(getActivity(), "打开文件管理器失败", Toast.LENGTH_SHORT).show();// 可以连接到下载文件管理器的连接让用户下载文件管理器
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK && requestCode == FILE_SELECT_CODE) {

			String path = data.getData().getPath();
			// System.out.println(path);
			if (path.endsWith(".txt")) {
				if (!sqlOpenHelp.find(path)) {

					adapt.add(new BookImf(path));
					sqlOpenHelp.add(new BookImf(path));

				}

			} else {
				Toast.makeText(getActivity(), "非txt文件，添加失败！！", Toast.LENGTH_SHORT).show();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
