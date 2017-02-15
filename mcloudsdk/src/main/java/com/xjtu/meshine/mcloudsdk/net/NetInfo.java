package com.xjtu.meshine.mcloudsdk.net;

import com.xjtu.meshine.mcloudsdk.NetOptions;

/**
 * 用于rmi的配置
 *
 */
public class NetInfo {
	public static final int DEFAULT_PORT = 3345;
	public static final String DEFAULT_IP = "localhost";
	public static int DEFAULT_TIME_OUT = Integer.MAX_VALUE;
	public static boolean DEFAULT_IS_SERVER = false;

	private static NetOptions options ;


	public static NetOptions getOptions() {
		return options;
	}

	public static void setOptions(NetOptions options) {
		NetInfo.options = options;
	}

	

}
