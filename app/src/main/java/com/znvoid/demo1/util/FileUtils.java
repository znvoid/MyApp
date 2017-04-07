package com.znvoid.demo1.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	private String SDPATH;

	private int FILESIZE = 4 * 1024;
	/*
	 * 文件编码是否找到
	 */
	private boolean found = false;
	/*
	 * 文件编码格式
	 */
	private String encoding = null;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		// 得到当前外部存储设备的目录( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 将一个字符串写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName, String input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			output.write(input.getBytes());
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 传入一个文件(File)对象，检查文件编码
	 * 
	 * @param file
	 *            File对象实例
	 * @return 文件编码，若无，则返回null
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String guessFileEncoding(File file) throws FileNotFoundException, IOException {
		return guessFileEncoding(file, new nsDetector());
	}

	/**
	 * <pre>
	 * 获取文件的编码
	 * &#64;param file
	 *            File对象实例
	 * &#64;param languageHint
	 *            语言提示区域代码 @see #nsPSMDetector ,取值如下：
	 *             1 : Japanese
	 *             2 : Chinese
	 *             3 : Simplified Chinese
	 *             4 : Traditional Chinese
	 *             5 : Korean
	 *             6 : Dont know(default)
	 * </pre>
	 * 
	 * @return 文件编码，eg：UTF-8,GBK,GB2312形式(不确定的时候，返回可能的字符编码序列)；若无，则返回null
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String guessFileEncoding(File file, int languageHint) throws FileNotFoundException, IOException {
		return guessFileEncoding(file, new nsDetector(languageHint));
	}

	/**
	 * 获取文件的编码
	 * 
	 * @param file
	 * @param det
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String guessFileEncoding(File file, nsDetector det) throws FileNotFoundException, IOException {
		// Set an observer...
		// The Notify() will be called when a matching charset is found.

		det.Init(new nsICharsetDetectionObserver() {
			@Override
			public void Notify(String charset) {
				encoding = charset;
				found = true;
			}
		});

		BufferedInputStream imp = new BufferedInputStream(new FileInputStream(file));
		byte[] buf = new byte[1024];
		int len;
		boolean done = false;
		boolean isAscii = false;

		while ((len = imp.read(buf, 0, buf.length)) != -1) {
			// Check if the stream is only ascii.
			isAscii = det.isAscii(buf, len);
			if (isAscii) {
				break;
			}
			// DoIt if non-ascii and not done yet.
			done = det.DoIt(buf, len, false);
			if (done) {
				break;
			}
		}
		imp.close();
		det.DataEnd();

		if (isAscii) {
			encoding = "ASCII";
			found = true;
		}

		if (!found) {
			String[] prob = det.getProbableCharsets();
			// 这里将可能的字符集组合起来返回
			for (int i = 0; i < prob.length; i++) {
				if (i == 0) {
					encoding = prob[i];
				} else {
					encoding += "," + prob[i];
				}
			}

			if (prob.length > 0) {
				// 在没有发现情况下,也可以只取第一个可能的编码,这里返回的是一个可能的序列
				return encoding;
			} else {
				return null;
			}
		}
		return encoding;
	}

	/*
	 * 搜索文件，未找到返回空 FileUtils.getSpecificTypeOfFile(this, new
	 * String[]{".doc",".apk"});
	 */

	public static List<File> getSpecificTypeOfFile(Context context, String[] extension) {
		List<File> result = new ArrayList<File>();
		// 从外存中获取
		Uri fileUri = Files.getContentUri("external");
		// 筛选列，这里只筛选了：文件路径和不含后缀的文件名
		String[] projection = new String[] { FileColumns.DATA, FileColumns.TITLE };
		// 构造筛选语句
		String selection = "";
		for (int i = 0; i < extension.length; i++) {
			if (i != 0) {
				selection = selection + " OR ";
			}
			selection = selection + FileColumns.DATA + " LIKE '%" + extension[i] + "'";
		}
		// 按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
		String sortOrder = FileColumns.DATE_MODIFIED;
		// 获取内容解析器对象
		ContentResolver resolver = context.getContentResolver();
		// 获取游标
		Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
		if (cursor == null)
			return null;
		// 游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
		if (cursor.moveToLast()) {
			do {
				// 输出文件的完整路径
				String data = cursor.getString(0);

				System.out.println(data);
				result.add(new File(data));
			} while (cursor.moveToPrevious());
		}
		cursor.close();
		return result;
	}

	/*
	 * 文件遍历
	 */
	public static List<File> FindFile(File file, String key_search) {
		List<File> list = new ArrayList<File>();
		if (file.isDirectory()) {
			File[] all_file = file.listFiles();
			if (all_file != null) {
				for (File tempf : all_file) {
					if (tempf.isDirectory()) {
						if (tempf.getName().toLowerCase().lastIndexOf(key_search) > -1) {
							list.add(tempf);
						}
						list.addAll(FindFile(tempf, key_search));
					} else {
						if (tempf.getName().toLowerCase().lastIndexOf(key_search) > -1) {
							list.add(tempf);
						}
					}
				}
			}
		}
		return list;
	}

	public static File creatFileStream(String fileName) {
		Log.e("TAG", fileName);
		File file = null;
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//

		} else {
			File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			file = sureFileName(sdDir.toString() + "/znvoid", fileName);
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("TAG", file.getAbsolutePath());

		}

		return file;
	}

	/**
	 * 判定sdcard上dirPath文件下是否有filename文件，有就改变文件名,为了创建新的文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static File sureFileName(String dirPath, String fileName) {

		File dirfile = new File(dirPath);
		if (!dirfile.exists()) {
			dirfile.mkdirs();// 创建文件夹

		}

		File file = new File(dirPath, fileName);
		if (file.exists()) {
			boolean flag = true;
			int i = 1;
			int t = fileName.lastIndexOf(".");
			while (flag) {
				if (t != -1) {

					String name = fileName.substring(0, t) +"-"+ i + fileName.substring(t, fileName.length());
					file = new File(dirPath, name);
				} else {
					file = new File(dirPath, fileName + i);
				}

				if (!file.exists()) {
					flag = false;
				}

			}

		}

		return file;
	}

}
