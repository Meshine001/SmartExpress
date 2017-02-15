package com.xjtu.meshine.mcloudsdk.net;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 通讯传输的pack类，用于记录rmi所需要的类，方法和参数
 * @author Meshine
 *
 */
public class Pack implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String funtionName;
	/**
	 * 发起RMI的类
	 */
	private Class stateType;
	/**
	 * 发起RMI的类的实例
	 */
	private Object state;
	private Object[] paramValues;
	private Class[] praramTypes;
	
	public Pack(String funtionName, Class stateType, Object state, Object[] paramValues, Class[] praramTypes) {
		super();
		this.funtionName = funtionName;
		this.stateType = stateType;
		this.state = state;
		this.paramValues = paramValues;
		this.praramTypes = praramTypes;
	}

	public String getFuntionName() {
		return funtionName;
	}

	public void setFuntionName(String funtionName) {
		this.funtionName = funtionName;
	}

	public Class getStateType() {
		return stateType;
	}

	public void setStateType(Class stateType) {
		this.stateType = stateType;
	}

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}

	public Object[] getParamValues() {
		return paramValues;
	}

	public void setParamValues(Object[] paramValues) {
		this.paramValues = paramValues;
	}

	public Class[] getPraramTypes() {
		return praramTypes;
	}

	public void setPraramTypes(Class[] praramTypes) {
		this.praramTypes = praramTypes;
	}

	@Override
	public String toString() {
		return "Pack [funtionName=" + funtionName + ", stateType=" + stateType + ", state=" + state + ", paramValues="
				+ Arrays.toString(paramValues) + ", praramTypes=" + Arrays.toString(praramTypes) + "]";
	}
	
	
}
