package data.mild.mynu.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import data.mild.mynu.data.SharePreferenceSdkData;
import data.mild.mynu.net.NetworkTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lsk.open.core.MyLog;
import com.lsk.open.core.net.NetHeadUtils;




public class ErrorRecordUtil {

	private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	public static final String LOG_TYPE_EXCEPTION = "Exception";

	public static final String LOG_TYPE_CRASH_EXCEPTION = "Crash_Exception";
	
	public static final String INFO_NAME = "crash.log";
	public static final String CLASS_INDEX_FIELD_NAME = "ERR_TAG";

	private static final UncaughtExceptionHandler sDefUncaughtExceptionHandler = Thread
			.getDefaultUncaughtExceptionHandler();

	public static boolean start(final Context context) {

		try {
			if (sDefUncaughtExceptionHandler == Thread
					.getDefaultUncaughtExceptionHandler()) {
				Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					@Override
					public void uncaughtException(Thread thread, Throwable ex) {

                        if(System.currentTimeMillis()-SharePreferenceSdkData.getErrorReportRTTimes(context)>60*1000){
                            SharePreferenceSdkData.setErrorReportRTTimes(context, System.currentTimeMillis());
                        }else{
                            //程序出现连续的崩溃,　此时会停止复活进程并将自己杀死
                            ReviveMain.stopDaemonProcess(context);
                            return;
                        }

						if (!handleException(context ,ex) && sDefUncaughtExceptionHandler != null) {  
				            // 如果用户没有处理则让系统默认的异常处理器来处理  
							sDefUncaughtExceptionHandler.uncaughtException(thread, ex);  
				        } else {  
				            // Sleep一会后结束程序  
				            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序  
				            try {  
				                Thread.sleep(2000);  
				            } catch (InterruptedException e) {  
				            }  
				            android.os.Process.killProcess(android.os.Process.myPid());  
				            System.exit(10);  
				        }  
						if(errorInfoOS!=null){
							try {
								errorInfoOS.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				});
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	 private static boolean handleException(final Context context, final Throwable ex) {  
	        final String result = outException(context,ex, LOG_TYPE_CRASH_EXCEPTION);
			final JSONObject jo =new JSONObject();
			try {
				jo.put("e_code", ex.toString());
				jo.put("e_content", result);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
//			new Thread(){
//				public void run() {
//					try {
////						int serverIndex = SharePreferenceSdkData.getServerUrlIndex(context);
////						String serverUrl = NetworkTask.urls[serverIndex];
////						DefaultHttpClient client = new DefaultHttpClient();
////						HttpPost post = new HttpPost(serverUrl+"ApiLog.json");
//
//						HashMap<String,String> header = NetHeadUtils.getSendHeader(context);
//						for (Entry<String, String> entry : header.entrySet()) {
//							post.addHeader(entry.getKey(), entry.getValue());
//						}
//						post.setEntity(new StringEntity(jo.toString(), "utf-8"));
//						HttpResponse response = client.execute(post);
//						int code = response.getStatusLine().getStatusCode();
//						if(200 == code){
//							ErrorRecordUtil.deleteErrorLogFile();
//						}
//					} catch (ClientProtocolException e1) {
//						e1.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				};
//			}.start();
	        return false;  
	    }  
	
	/**
	 * 日志详情输出流
	 */
	private static FileOutputStream errorInfoOS = null;
	
	/**
	 * 输出异常信息
	 * 
	 * @param e
	 *            异常信息
	 * @param type
	 *            异常类新
	 */
	public static final String outException(Context context,Throwable e, String type) {
		// if (!ENABLE) {
		// return;
		// }
		if (e == null) {
			return "";
		}
		if (type == null) {
			type = LOG_TYPE_EXCEPTION;
		}
		if (!start(context)) {
			return "";
		}
		long startIndex = -1;
		
		if (errorInfoOS == null) {
			try {
				File indexFile = getInfoLogFile();
				if (!indexFile.exists()) {
					indexFile.createNewFile();
				}
				errorInfoOS = new FileOutputStream(indexFile, true);
			} catch (Exception e1) {
				e1.printStackTrace();
			return "";
			}
		}
		
		try {
			startIndex = errorInfoOS.getChannel().position();
		} catch (IOException ex) {
		}
		String result = outInfoString(e.toString());
		StackTraceElement[] stack = e.getStackTrace();
		for (StackTraceElement element : stack) {
			result += outInfoString("\tat " + element + "\t, Index:"
					+ getClassIndex(element));
		}

		StackTraceElement[] parentStack = stack;
		Throwable throwable = e.getCause();
		while (throwable != null) {
			result += outInfoString("Caused by: ");
			result += outInfoString(throwable.toString());
			StackTraceElement[] currentStack = throwable.getStackTrace();
			int duplicates = countDuplicates(currentStack, parentStack);
			for (int i = 0; i < currentStack.length - duplicates; i++) {
				result += outInfoString("\tat " + currentStack[i]
						+ "\t, Index:" + getClassIndex(currentStack[i]));
			}
			if (duplicates > 0) {
				result += outInfoString("\t... " + duplicates + " more");
			}
			parentStack = currentStack;
			throwable = throwable.getCause();
		}
	
		return result;
	}
	
	/**
	 * 获得详情日志文件地址
	 * 
	 * @return 概要日志文件地址
	 */
	public static final File getInfoLogFile() {
		File dirFile = new File(KingsSystemUtils.getErrorPath());
		dirFile.mkdirs();
		File indexLogFile = new File(dirFile, INFO_NAME);
		return indexLogFile;
	}
	
	/**
	 * 获得详情日志文件地址
	 * 
	 * @return 概要日志文件地址
	 */
	public static final void deleteErrorLogFile() {
		File indexLogFile = new File(KingsSystemUtils.getErrorPath(), INFO_NAME);
		if(indexLogFile.exists() && !MyLog.isDebug()){
			indexLogFile.delete();
		}
	}
	
	/**
	 * Counts the number of duplicate stack frames, starting from the end of the
	 * stack.
	 * 
	 * @param currentStack
	 *            a stack to compare
	 * @param parentStack
	 *            a stack to compare
	 * 
	 * @return the number of duplicate stack frames.
	 */
	private static int countDuplicates(StackTraceElement[] currentStack,
			StackTraceElement[] parentStack) {
		// if (!ENABLE) {
		// return -1;
		// }
		int duplicates = 0;
		int parentIndex = parentStack.length;
		for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
			StackTraceElement parentFrame = parentStack[parentIndex];
			if (parentFrame.equals(currentStack[i])) {
				duplicates++;
			} else {
				break;
			}
		}
		return duplicates;
	}

	
	private static String outInfoString(String outStr) {
		// if (!ENABLE) {
		// return;
		// }
		try {
			outStr += "\r\n";
			errorInfoOS.write(outStr.getBytes());
			errorInfoOS.flush();
		} catch (IOException e) {
		}
		return outStr;
	}
	
	
	private static String getClassIndex(StackTraceElement element) {
		// if (!ENABLE) {
		// return null;
		// }
		Class<?> clazz = null;
		try {
			clazz = Class.forName(element.getClassName());
		} catch (ClassNotFoundException ex) {
			return "UnKnowClassIndex";
		}
		while (true) {
			Class<?> declaringClass = clazz.getDeclaringClass();
			if (declaringClass == null) {
				break;
			} else {
				clazz = declaringClass;
			}
		}
		Field indexField = null;
		try {
			indexField = clazz.getField(CLASS_INDEX_FIELD_NAME);
		} catch (Exception e) {
			return "UnKnowClassIndex";
		}
		String classIndex = null;
		try {
			classIndex = (String) indexField.get(null);
		} catch (Exception e) {
			classIndex = "UnKnowClassIndex";
		}
		return classIndex;
	}

	/**
     * Make the exception "OutOfMemoryError" for test purpose.
     */
    public static void makeOutOfMemoryError() {
        final int BLOCK_SIZE = 1024 * 1024; // 1M
        final int BLOCK_COUNT = 1000;
        class TestBuffer {
            Byte[] innerBuffer = new Byte[BLOCK_SIZE];
        }
        TestBuffer[] buffers = new TestBuffer[BLOCK_COUNT];
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = new TestBuffer();
        }
    }

    /**
     * Make the normal exception for test purpose
     */
    public static void makeCrash() {
        try {
            throwCrashNested();
        } catch (Exception e) {
            throw new RuntimeException("exception found", e);
        }
    }

    /**
     * for test only
     */
    private static void throwCrashNested() {
        throw new RuntimeException("this is a test exception");
    }
	
}
