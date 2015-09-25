package net.doll.holx.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lsk.open.core.utils.KingsFileUtils;
import com.lsk.open.core.utils.StringUtils;

public class KingsSystemUtils {
	/** 品牌名称 **/
	public static String brand;
	/** 主板信息 **/
	public static String board;
	/** android id **/
	// 设备的唯一串号，在2.3以上版本基本保持稳定，但会在刷机或手机重置情况下，发生变化（可以忽略，作为新设备记录）
	public static String android_id;

	static public boolean copyFileFromAssets(Context aContext, String aSrc,
			String aTar, boolean bForce) {
		File src = new File(aTar);
		if (src.exists()) {
			if (bForce)
				src.delete();
			else
				return true;
		}

		try {
			src.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		AssetManager assetManager = aContext.getAssets();
		try {
			InputStream inputStream = assetManager.open(aSrc);

			OutputStream outStream = new FileOutputStream(aTar);
			BufferedInputStream bin = null;
			BufferedOutputStream bout = null;

			bin = new BufferedInputStream(inputStream);

			bout = new BufferedOutputStream(outStream);

			byte[] b = new byte[1024];

			int len = bin.read(b);

			while (len != -1) {
				bout.write(b, 0, len);
				len = bin.read(b);
			}

			bin.close();
			bout.close();

		} catch (IOException e) {
			return true;
		}
		return false;
	}

	/**
	 * 隐藏系统键盘
	 */
	public static void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		return line;
	}

	static public boolean copyFileFromFile(String aSrc, String aTar,
			boolean bForce) {
		File src = new File(aTar);
		if (src.exists()) {
			if (bForce)
				src.delete();
			else
				return true;
		}

		try {
			src.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			InputStream inputStream = new FileInputStream(new File(aSrc));

			OutputStream outStream = new FileOutputStream(aTar);
			BufferedInputStream bin = null;
			BufferedOutputStream bout = null;

			bin = new BufferedInputStream(inputStream);

			bout = new BufferedOutputStream(outStream);

			byte[] b = new byte[1024];

			int len = bin.read(b);

			while (len != -1) {
				bout.write(b, 0, len);
				len = bin.read(b);
			}

			bin.close();
			bout.close();

		} catch (IOException e) {
			return true;
		}
		return false;
	}

	/**
	 * 根据进程ID获取进程名称
	 * 
	 * @param pid
	 * @return
	 */
	public static String getProcessName(int pid) {
		String processName = null;
		try {
			if (processName == null) {
				processName = KingsFileUtils.readFile("/proc/" + pid + "/cmdline",
						'\0');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return processName;
	}

//	private static SimpleDateFormat formatter = new SimpleDateFormat(
//			"yyyy-MM-dd HH:mm:ss");
//	private static SimpleDateFormat shortFormatter = new SimpleDateFormat(
//			"yyyy-MM-dd");

	private DateFormat getFormatter(){
		return new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 转为日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date format2Date(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date.contains("/"))
			formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	/**
	 * 转为日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String format2ShortDate(String date) {
		try {
			if (date != null)
				return date.substring(0, date.lastIndexOf("-") + 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String dateFormat(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date == null)
			return null;
		long time = format2Date(date).getTime();
		long current = System.currentTimeMillis();
		long lastTime = current - time;
		if (lastTime > 0) {
			if (lastTime <= 5 * 60 * 1000) {
				return "刚刚";
			} else if (lastTime < 60 * 60 * 1000) {
				return (int) (lastTime / (60 * 1000)) + "分钟前";
			} else if (lastTime < 24 * 60 * 60 * 1000
					&& lastTime >= 60 * 60 * 1000) {
				return (int) (lastTime / (60 * 60 * 1000)) + "小时前";
			} else {
				return formatter.format(new Date(time));
			}
		} else {
			return formatter.format(new Date(time));
		}
	}

	public static String apkSizeFormat(double dSize, String ext) {

		return String.valueOf(apkSizeFormat.format(dSize)) + ext;
	}

	private static NumberFormat apkSizeFormat = new DecimalFormat("0.##");

	/**
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static byte[] decompress(byte[] source) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(source);
		GZIPInputStream gis = new GZIPInputStream(bin);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int length = 0;
		while ((length = gis.read(buf)) > 0) {
			bos.write(buf, 0, length);
		}
		return bos.toByteArray();
	}

	public static byte[] compress(byte[] source) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(source);
		gzip.close();
		return out.toByteArray();
	}

	/**
	 * 取得评论图形
	 * 
	 * @param d
	 * @return
	 */
	public static float getFloatRateVaue(double d) {
		return (float) d;// 0x4000000000000000L
	}

	/**
	 * 取得详情评分
	 * 
	 * @param d
	 * @return
	 */
	public static float getDrawRateVaue(double d) {
		return ((int) Math.ceil(d * 2)) * 1.0f / 2;// 0x4000000000000000L
	}

	//TODO lijc what's this
	public static String getSignature() {
		StringBuilder signature = new StringBuilder();
		String app_key = "391ef5";
		String time = Long.toString(System.currentTimeMillis());
		String app_secret = "1c603acd5214fc2aaa607281030d0415";
		signature.append(app_key);
		signature.append(time);
		signature.append(app_secret);
		String md5Val = StringUtils.md5Encoding(signature.toString());
		return md5Val.substring(0, 25);
	}

	public static String bytes2kb(long bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		if (returnValue > 1)
			return (returnValue + "  MB ");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		return (returnValue + "  KB ");
	}

	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px转化成dip
	 */
	public static int px2dip(Context context, float pxValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 字符串转JSON
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray toJSONArray(String json) throws JSONException {
		if (!isEmpty(json)) {
			// if (json.indexOf("[") == 0) {
			// json = json.substring(1, json.length());
			// }
			// if (json.lastIndexOf("]") == json.length()) {
			// json = json.substring(0, json.length() - 1);
			// }
		}
		return new JSONArray(json);
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static String dataFormat(double data) {
		double d = data >= 0 ? data : 0 - data;

		String buffer = "";

		NumberFormat df = NumberFormat.getNumberInstance();
		df.setMaximumFractionDigits(1);

		String unitStr = "B";

		if (d < 1000) {
			buffer = d + "";
		} else if (d < 1024000) {
			buffer = df.format(((double) d) * 1f / 1024) + "";
			unitStr = "KB";
		} else if (d < 1048576000) {
			buffer = df.format(((double) d) * 1f / 1048576) + "";
			unitStr = "MB";
		} else {
			buffer = df.format(((double) d) * 1f / 1073741824) + "";
			unitStr = "GB";
		}
		int value;
		if (buffer.length() >= 6) {
			if ((value = buffer.indexOf(".")) > 0) {
				if (value != -1) {
					buffer = buffer.toString().substring(0, value + 2);
				}
			}
		}

		return buffer + unitStr;
	}

	/**
	 * 得到应用进程的ID
	 */
	public static int getMyProcessID(String DaemonProcessName) {
		for (File processDir : getAllProcessDirs()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(processDir, "cmdline"))));
				String processName = br.readLine();
				if (processName == null) {
					continue;
				}
				processName = processName.trim();
				if (DaemonProcessName.equals(processName)) {
					try {
						return Integer.parseInt(processDir.getName());
					} catch (NumberFormatException e) {
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return -1;
	}

	private static File[] getAllProcessDirs() {
		File procDir = new File("/proc");
		return procDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				try {
					Integer.parseInt(pathname.getName());
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		});
	}

	/**
	 * 按进程名停止进程
	 */
	public static void stopMyProcess(String proceName) {
		int daemonPid = getMyProcessID(proceName);
		if (daemonPid != -1) {
			android.os.Process.killProcess(daemonPid);
		}
	}

	public static boolean isScreenLocked(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean screen = pm.isScreenOn();
		return screen;
	}
	
	public static boolean isSameDay(long time){
    	Date nowDate  = new Date(System.currentTimeMillis());
    	Date oldDate  = new Date(time);
    	if(nowDate.getDate()==oldDate.getDate()){
    		return true;
    	}
    	return false;
    }
	
	public static final String FOLDER_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ttbag/";
	public static final String PAY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.kingslock/";
	public static final String FOLDER_ERROR=  FOLDER_ROOT+"error/";//错误日志目录
	
	public static void initFiles(){
		File root = new File(FOLDER_ROOT);
		if(!root.exists()){
			root.mkdirs();
		}
		File error = new File(FOLDER_ERROR);
		if(!error.exists()){
			error.mkdirs();
		}
	}
	
	
	public static String getErrorPath(){
		File root = new File(KingsSystemUtils.FOLDER_ROOT);
		if(!root.exists())
			root.mkdirs();
		
		File update=new File(KingsSystemUtils.FOLDER_ERROR);
		if(!update.exists()){
			update.mkdirs();
		}
		return update.getAbsolutePath();
	}
}
