package com.xjtu.meshine.mcloudsdk.net;

import java.io.Serializable;

public class ResultPack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private Object result;
	private Object state;

	public ResultPack(Object result, Object state) {
		super();
		this.result = result;
		this.state = state;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ResultPack [result=" + result + ", state=" + state + "]";
	}

}
