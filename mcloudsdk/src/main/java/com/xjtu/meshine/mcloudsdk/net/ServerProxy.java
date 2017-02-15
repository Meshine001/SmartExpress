package com.xjtu.meshine.mcloudsdk.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 云端处理事物的代理
 * 
 * @author Meshine
 *
 */
public class ServerProxy {
	private int port;
	ServerSocket serverSocket = null;
	Socket socket = null;
	InputStream inputStream = null;
	OutputStream outputStream = null;
	ObjectInputStream objectInputStream = null;
	ObjectOutputStream objectOutputStream = null;

	public ServerProxy() {
		// TODO Auto-generated constructor stub
	}

	public ServerProxy(int port) {
		super();
		this.port = port;
	}

	/**
	 * 
	 */
	public void startServer() {
		System.out.println("Starting server proxy...");
		if (serverSocket == null || serverSocket.isClosed()) {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				System.out.println("Port already in use");
				return;
			}
		}

		try {
			System.out.println("Sever proxy started");
			System.out.println("Waiting for pack...");
			socket = serverSocket.accept();
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			objectInputStream = new ObjectInputStream(inputStream);
			objectOutputStream = new ObjectOutputStream(outputStream);	
			waitForData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopServer(){

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 等待数据传入
	 */
	private void waitForData() {
		new Receiving().listen();
	}

	class Receiving implements Runnable {
		private String funtionName;
		private Class stateType;
		private Object state;
		private Object[] paramValues;
		private Class[] praramTypes;
		private Pack pack;

		public void listen() {
			Thread t = new Thread(this);
			t.start();
		}

		@Override
		public void run() {

			try {
				pack = (Pack) objectInputStream.readObject();
				System.out.println("Get Pack ===============>");
				System.out.println(pack);
				
				funtionName = pack.getFuntionName();
				stateType = pack.getStateType();
				state = pack.getState();
				paramValues = pack.getParamValues();
				praramTypes = pack.getPraramTypes();
				if (funtionName != null && funtionName.length() > 0) {
					Class cls = Class.forName(stateType.getName());
					Method method = cls.getDeclaredMethod(funtionName, praramTypes);
					Object result = method.invoke(state, paramValues);
					ResultPack resultPack = new ResultPack(result, state);
					objectOutputStream.writeObject(resultPack);
					objectOutputStream.flush();
					
					System.out.println("Send result ===============>");
					System.out.println(resultPack);
				}
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				returnnull(objectOutputStream);
			} finally {
				try {
					objectInputStream.close();
					objectOutputStream.close();
					inputStream.close();
					outputStream.close();
					socket.close();

					objectInputStream = null;
					objectOutputStream = null;
					inputStream = null;
					outputStream = null;
					socket = null;

					//重启服务
					startServer();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
	
    void returnnull(ObjectOutputStream oos){
        if(oos != null)
            try {
                oos.writeObject(null);
                oos.flush();
                System.out.println("Send result ===============>");
				System.out.println("null");
            } catch (IOException ex1) {

            }
    }
    
    public static void main(String[] args) {
		ServerProxy serverProxy = new ServerProxy(NetInfo.getOptions().getPort());
		serverProxy.startServer();
	}
}
