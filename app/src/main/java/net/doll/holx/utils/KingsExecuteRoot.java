package net.doll.holx.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;



public final class KingsExecuteRoot {

//	private static final String SU = StrHelp1.S_PREFIX+StrHelp2.U_PREFIX;
	private static final String SU = new StringBuffer(StrHelp1.S_PREFIX).append(StrHelp2.U_PREFIX).toString();

//	private static final String SH = StrHelp1.S_PREFIX+StrHelp3.H_PREFIX;
	private static final String SH = new StringBuffer(StrHelp1.S_PREFIX).append(StrHelp3.H_PREFIX).toString();


	private Process mProcess = null;

	private BufferedReader inBuffer = null;

	private OutputStream out = null;

	private Integer exitCode = null;
	
	private static KingsExecuteRoot instance = null;

	private Thread mShellDaemonThread = new Thread() {
		@Override
		public void run() {
			while (exitCode == null && mProcess != null
					&& mShellDaemonThread != null) {
				try {
					exitCode = mProcess.waitFor();
				} catch (InterruptedException e) {
				}
			}
			if (mShellDaemonThread != null) {
				closeShell();
			}
		}
	};
	
	public static KingsExecuteRoot getExecuteRoot(boolean root){
		if(instance == null || instance.getExitCode()!=null){
			instance = new KingsExecuteRoot(root);
		}
		return instance;
	}
	
	public KingsExecuteRoot(boolean root) {
		String initCommand = null;
		if (root) {
			initCommand = SU;
		} else {
			initCommand = SH;
		}
		try {
			mProcess = Runtime.getRuntime().exec(initCommand);
			try{
				Thread.sleep(200);
			}catch (InterruptedException e) {
			}
			try{
				exitCode = mProcess.exitValue();
			}catch (IllegalThreadStateException e) {
			}
			if (exitCode == null){
				mShellDaemonThread.start();
				inBuffer = getBufferedReader(mProcess.getInputStream());
				out = mProcess.getOutputStream();
			}else{
				throw new NotFindShellProcess();
			}
		} catch (Exception e) {
			closeShell();
		}
	}

	public Integer getExitCode() {
		return exitCode;
	}
	
	public Process getProcess(){
		return mProcess;
	}

	public void closeShell() {
		if (mProcess != null){
			mProcess.destroy();
			mProcess = null;
		}
		
		if (inBuffer != null){
			try {
				inBuffer.close();
			} catch (IOException e) {
			}
			inBuffer = null;
		}
		
		if (out != null){
			try {
				out.close();
			} catch (IOException e) {
			}
			out = null;
		}
		
		exitCode = Integer.MIN_VALUE;
		
		if (mShellDaemonThread != null){
			mShellDaemonThread.interrupt();
			mShellDaemonThread = null;
		}
	}

	public String runCommandOrScript(String command) {
		String[] script = command.split("\n");
		if (script.length > 1) {
			return runScript(script);
		} else {
			return runCommand(command);
		}
	}

	public String runCommand(String command) {
		if (exitCode != null) {
			closeShell();
			throw new NotFindShellProcess();
		}
		String request = null;
//		synchronized (mProcess) {
			try {
				writeCommand(out, command);
				request = readRequset(inBuffer);
			} catch (IOException e) {
				closeShell();
				throw new RuntimeException("Exec command: " + command
						+ ",error!", e);
			}
//		}
		return request;
	}

	public String runScript(String[] script) {
		StringBuffer request = new StringBuffer();
		for (String command : script) {
			request.append(runCommand(command));
		}
		return request.toString();
	}

	private static BufferedReader getBufferedReader(InputStream in) {
		return new BufferedReader(new InputStreamReader(in));
	}

	private static void writeCommand(OutputStream out, String command)
			throws IOException {
		out.write(command.getBytes());
		out.write("\n".getBytes());
		out.write("echo EOF:\n".getBytes());
		out.flush();
	}

	private static String readRequset(BufferedReader inBuffer)
			throws IOException {
		StringBuffer request = new StringBuffer();
		while (true) {
			String newLine = inBuffer.readLine();
			if ("EOF:".equals(newLine)||newLine==null) {
				return request.toString();
			} else {
				request.append(newLine);
				request.append('\n');
			}
		}

	}

	public class NotFindShellProcess extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}