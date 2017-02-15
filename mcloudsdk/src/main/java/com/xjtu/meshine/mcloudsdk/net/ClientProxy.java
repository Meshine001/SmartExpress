package com.xjtu.meshine.mcloudsdk.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientProxy {
	private int port;
	private String ip;
	private int timeout;
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private CloudController cloudController;
	long startTime = 0;

	public ClientProxy(int port, String ip, int timeout) {
		super();
		this.port = port;
		this.ip = ip;
		this.timeout = timeout;
	}

	public void setCloudController(CloudController cloudController) {
		this.cloudController = cloudController;
	}

	public boolean connect() {
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(ip, port), timeout);

			startTime = System.nanoTime();

			in = socket.getInputStream();
			out = socket.getOutputStream();
			oos = new ObjectOutputStream(out);
			ois = new ObjectInputStream(in);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cloudController.setResult(null, null);
			return false;
		}
	}

	 public void send(String functionName, Class[] paramTypes, Object[] funcArgValues, Object state, Class stateDType){
	        try{
	            new Sending(new Pack(functionName, stateDType, state, funcArgValues, paramTypes)).send();
	        }catch(Exception ex){
	            cloudController.setResult(null, null);
	        }
	    }
	class Sending implements Runnable {
		private Pack pack;
		private ResultPack result;

		public Sending(Pack pack) {
			super();
			this.pack = pack;
		}

		public void send() {
			Thread t = new Thread(this);
			t.start();
		}

		@Override
		public void run() {
			try {
				oos.writeObject(pack);
				oos.flush();
				System.out.println("Send pack ===============>");
				System.out.println(pack);
				System.out.println("Waiting for result...");

				result = (ResultPack) ois.readObject();
				System.out.println("Get result ===============>");
				System.out.println(result);

				if ((System.nanoTime() - startTime) / 1000000 < timeout) {
					if (result == null) {
						cloudController.setResult(null, null);
					} else {
						cloudController.setResult(result.getResult(), result.getState());
					}
				}
				
				oos.close();
				ois.close();

				in.close();
				out.close();
				
				socket.close();
				
				oos = null;
				ois = null;

				in = null;
				out = null;
				
				socket = null;
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cloudController.setResult(null, null);
			}
		}

	}

}
