package data.mild.mynu.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

/**
 * Created by Administrator on 2014/12/18.
 */
public class NetClass {

    public static final int HTTP_ERROR_URL_NULL = 0;
    public static final int HTTP_ERROR_URL_MALFORMED = 1;
    public static final int HTTP_ERROR_IO_EXCEPTION = 2;
    public static final int HTTP_ERROR_CAN_NOT_WRITE_POST = 3;

    private static final int CONNECTION_TIME_OUT = 15000;
    private static final int READ_TIME_OUT = 30000;

    private static final int BUFFER_SIZE = 1024;
    
    private static final boolean DEBUG_NETCLASS = false;
    private static final String TAG = "NetClass";

    private void writeErrorCode(OutputStream out, int errorCode) {
        try {
            out.write(errorCode);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Map<String, String> header;
    public void setRequestHeader(Map<String, String> header) {
    	this.header = header;
    }

    private void doRequest(String url, String postParam, OutputStream out, OnNetListener listener) {
    	if(DEBUG_NETCLASS) {
    		Log.d(TAG, "doRequest ----- url: " + url);
    	}
        if ((url == null) || (out == null)) {
            return;
        }
        URL realUrl;
        try {
            realUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.toString());
            }
            writeErrorCode(out, HTTP_ERROR_URL_MALFORMED);
            return;
        }

        HttpURLConnection conn = null;
        OutputStream post = null;
        InputStream in = null;
        try {
        	if(DEBUG_NETCLASS) {
        		Log.d(TAG, "start time: " + SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
        	}
            conn = (HttpURLConnection) realUrl.openConnection();
            if (conn == null) {
                if (listener != null) {
                    listener.onError("unable to open " + url);
                }
                return;
            }

            conn.setConnectTimeout(CONNECTION_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            
            if(header != null) {
            	for(Entry<String, String> item: header.entrySet()) {
            		conn.setRequestProperty(item.getKey(), item.getValue());
            	}
            }
            
            if (postParam != null) {
                conn.setDoOutput(true);
                post = conn.getOutputStream();
                if (post == null) {
                    if (listener != null) {
                        listener.onError("unable to open post: " + url);
                    }
                    writeErrorCode(out, HTTP_ERROR_CAN_NOT_WRITE_POST);
                    return;
                } else {
                    post.write(postParam.getBytes());
                    post.close();
                }
            } else {
                int responseCode = conn.getResponseCode();
                if(DEBUG_NETCLASS) {
                	Log.d(TAG, " -----------------responseCode: " + responseCode);
                }
                if (responseCode != 200) {
                    if (listener != null) {
                        listener.onError("response code is: " + responseCode);
                    }
                    writeErrorCode(out, responseCode);
                    return;
                }
            }
            int progress = 0;
            in = conn.getInputStream();
            int length = conn.getContentLength();
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
            	if(DEBUG_NETCLASS) {
            		Log.d(TAG, "read time: " + SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis()) + "  ;progress: " + progress);
            	}
                out.write(buffer, 0, read);
                progress += read;
                int l = conn.getContentLength();
                if(DEBUG_NETCLASS) {
                	Log.d(TAG, " ----22---- after get length ------: " + l);
                }
                if (listener != null) {
                    listener.onProgress(progress, length);
                }
            }
            if(listener != null) {
                if(out instanceof ByteArrayOutputStream) {
                    listener.onSuccess(out.toString());
                } else {
                    listener.onSuccess(null);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "ioexception: " + e.toString());
            if(DEBUG_NETCLASS) {
            	Log.d(TAG, "io exception time: " + SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
            }
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.toString());
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (post != null) {
                    post.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                if (listener != null) {
                    listener.onError("IOException fail to close");
                }
            }
        } finally {
        	if(DEBUG_NETCLASS) {
        		Log.d(TAG, " -------- finally ------: ");
        	}
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (post != null) {
                    post.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onError("finally fail to close");
                }
            }
        }
    }

    /**
     * @param url
     * @param postParam
     * @param destination
     * @param listener
     * @return
     * 1. 一个请求的关键要素包括：参数(post请求)，返回值(状态码，响应头、响应体)，异步监听
     * 2. 对于返回值：
     *    1) 如果请求处理成功，调用方想要得到的一般是响应体，即正文。对正文的处理无非两个要求： a) 直接写入文件 b)原样返回
     *    2) 如果请求失败， 调用方一般想得到失败的原因，即响应码。
     *    总结：
     *    1） return的结果为： 如果请求成功，则返回响应体内容； 如果请求失败，则返回错误码。
     *    2） doRequest中负责处理回调listener中的onSuccess/onError/onProgress
     *
     */
    private String request(String url, String postParam, File destination, OnNetListener listener) {

        if (url == null) {
            if(listener != null) {
                listener.onError("url can not be null");
            }
            return String.valueOf(HTTP_ERROR_URL_NULL);
        }

        OutputStream out;
        try {
            if(destination != null) {
                //write to file
                out = new FileOutputStream(destination);
            } else {
                //return the result
                out = new ByteArrayOutputStream();
            }
            doRequest(url, postParam, out, listener);
            out.close();

            if (destination == null) {
                return new String( out.toString() );
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.toString());
            }
            return String.valueOf(HTTP_ERROR_IO_EXCEPTION);
        }

        return null;
    }

    public String getRequest(String url) {
        return postRequest(url, null);
    }
    public String postRequest(String url, String postParam) {
        return postRequestWriteResponseToFile(url, postParam, null);
    }

    public void getRequest(String url, OnNetListener listener) {
        postRequest(url, null, listener);
    }

    public void postRequest(String url, String postParam, OnNetListener listener) {
        postRequestWriteResponseToFile(url, postParam, null, listener);
    }

    public String getRequestWriteResponseToFile(String url, File destination) {
        return postRequestWriteResponseToFile(url, null, destination);
    }

    public String postRequestWriteResponseToFile(String url, String postParam, File destination) {
        String result = request(url, postParam, destination, null);
        return result;
    }

    public void getRequestWriteResponseToFile(String url, File destination, OnNetListener listener) {
        postRequestWriteResponseToFile(url, null, destination, listener);
    }

    public void postRequestWriteResponseToFile(String url, String postParam, File destination, OnNetListener listener) {
        request(url, postParam, destination, listener);
    }

    public static abstract interface OnNetListener {
        public abstract void onSuccess(String paramString);

        public abstract void onError(String paramString);

        public abstract void onProgress(int paramInt1, int paramInt2);
    }
}