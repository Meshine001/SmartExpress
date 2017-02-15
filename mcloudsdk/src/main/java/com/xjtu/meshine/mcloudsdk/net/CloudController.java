package com.xjtu.meshine.mcloudsdk.net;

import com.xjtu.meshine.mcloudsdk.NetOptions;

import java.lang.reflect.Method;
import java.util.Vector;

public class CloudController {

	private int port;
	private String ip;
	private int timeout;
	
	Object result;
	Object state;
	final Object waitObj = new Object();
	Vector results = new Vector();
	
	private ClientProxy clientProxy;
	public CloudController(int port, String ip,int timeout) {
		super();
		this.port = port;
		this.ip = ip;
		this.timeout = timeout;
	}
	
	private static CloudController instance;
	
	public static CloudController getInstance(){
		if(instance == null){
			synchronized (CloudController.class) {
				if(instance == null){
					System.out.println("Instanizing CloudController");
					instance = new CloudController(NetInfo.getOptions().getPort(), NetInfo.getOptions().getIp(),NetInfo.getOptions().getTimeOut());
				}
			}
		}
		return instance;
	}
	
	public Vector execute(Method toExecute, Object[] paramValues, Object state, Class stateDataType) {
		synchronized (waitObj) {
			this.result = null;
			this.state = null;
			if(clientProxy == null){
				System.out.println("Start a new Client proxy");
				clientProxy = new ClientProxy(port, ip,timeout);
				clientProxy.setCloudController(this);
			}
			
			System.out.println("Start network...");
			new Thread(new Network(toExecute, toExecute.getParameterTypes(), paramValues, state, stateDataType)).start();
			
			try {
				waitObj.wait(NetInfo.getOptions().getTimeOut());
				
				if(this.state != null){
					results.removeAllElements();
					results.add(this.result);
					results.add(this.state);
					System.out.println(result);
					return results;
				}else{
					return null;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
    public void setResult(Object result, Object cloudModel){
        synchronized (waitObj){
            this.result = result;
            this.state = cloudModel;
            waitObj.notify();
        }
    }
	class Network implements Runnable{
		Method toExecute;
        Class[] paramTypes;
        Object[] paramValues;
        Object state = null;
        Class stateDataType = null;
        
        
		public Network(Method toExecute, Class[] paramTypes, Object[] paramValues, Object state, Class stateDataType) {
			super();
			this.toExecute = toExecute;
			this.paramTypes = paramTypes;
			this.paramValues = paramValues;
			this.state = state;
			this.stateDataType = stateDataType;
		}


		@Override
		public void run() {
			boolean isConnected = clientProxy.connect();
			if(isConnected){
				clientProxy.send(toExecute.getName(), paramTypes, paramValues, state, stateDataType);
			}
			
		}
		
	}
	
	
}
