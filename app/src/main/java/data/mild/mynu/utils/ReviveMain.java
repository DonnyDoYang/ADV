package data.mild.mynu.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.lsk.open.core.MyLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 进程复活
 */
public class ReviveMain {
	private static  String DaemonProcessName = "_dm";//运行过程中修改

	public static void main(String[] args) {
		
		// 主进程ID
		final int hostProcessID;
		final String pname;
		final String dmName;
		if (args.length > 2) {
			try {
				hostProcessID = Integer.parseInt(args[0]);
				pname=args[1];
				dmName=args[2];
			} catch (NumberFormatException e) {
				return;
			}
		} else {
			return;
		}
		
		Method setArgV0;
		try {
			setArgV0 = android.os.Process.class.getDeclaredMethod("setArgV0",
					new Class[] { String.class });
			setArgV0.setAccessible(true);
			setArgV0.invoke(android.os.Process.class,
					new Object[] { dmName });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	
		while (true) {
			try {
				File hostProcess = new File("/proc/" + hostProcessID);
				if (!hostProcess.exists()) {
					// 证明主进程已经被kill掉，所以重新启动服务
//					String cmdLine = "am startservice --user 0 -a com.gc.tt.kingsdaemon.self";
					String cmdLine = new StringBuffer(StrHelp1.A_PREFIX).append(StrHelp2.START_SERVIE).append(StrHelp3.START_SERVIE).append(pname).append(".self").toString();
					if (Build.VERSION.SDK_INT <= 15) {
//						cmdLine = "am startservice -a com.gc.tt.kingsdaemon.self";
						cmdLine = new StringBuffer(StrHelp1.A_PREFIX).append(StrHelp2.START_SERVIE).append(StrHelp3.START_SERVIE1).append(pname).append(".self").toString();
					}
					try {
						Runtime.getRuntime().exec(cmdLine);
					} catch (IOException e) {
						e.printStackTrace();
					}
					android.os.Process.killProcess(android.os.Process.myPid());
				}
				Thread.sleep(10000);// 十秒
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 得到守护进程的ID
	 */
	public static int getDaemonProcessID(String dname) {
		Log.d("Donny","getDaemonProcessID==="+dname);
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
				if (dname.equals(processName)) {
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

	public static void startDaemonProcess(Context c) {
		Log.d("donny","startDaemonProcess");
		String dmName=getDaemonName(c.getPackageName());
		int daemonPid = getDaemonProcessID(dmName);
		if (daemonPid == -1) {
			try {
				// System.getenv()读取系统环境变量
				Map<String, String> envMap = new HashMap<String, String>(
						System.getenv());
				envMap.put("CLASSPATH", c
						.getApplicationInfo().sourceDir);
				String[] envs = new String[envMap.size()];
				int index = 0;
				for (Entry<String, String> entry : envMap.entrySet()) {
					envs[index++] = entry.getKey() + "=" + entry.getValue();
				}
				// app_process用于启动java类,最终会调main函数
				Runtime.getRuntime().exec(
						"/system/bin/app_process /system/bin "
								+ ReviveMain.class.getName() + " "
								+ android.os.Process.myPid() + " "
								+ c.getPackageName() + " "
								+ dmName
								+ "\n", envs);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止守护进程
	 */
	public static void stopDaemonProcess(Context c) {
		int daemonPid = getDaemonProcessID(getDaemonName(c.getPackageName()));
		if (daemonPid != -1) {
			android.os.Process.killProcess(daemonPid);
		}
	}
	
	public static String getDaemonName(String pname){
		String dmName="king";
		
		if(pname!=null&&!pname.equals("")){
			String[] name=pname.split("\\.");
			if(name!=null&&name.length>0){
				String d_name=new StringBuffer().append(name[0]).append(DaemonProcessName).toString();
				dmName=d_name;
			}
			
		}
		MyLog.e("RevivieMain","dmName=="+dmName);
		return dmName;
	}
}
