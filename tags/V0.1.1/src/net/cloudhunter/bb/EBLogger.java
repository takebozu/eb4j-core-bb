package net.cloudhunter.bb;

import net.rim.device.api.system.EventLogger;

/**
 * BlackBerryデバイスのイベントログ出力ユーティリティ。
 * 
 * @author CloudHunter
 *
 */
public class EBLogger {
	private static long GUID = 0xa7ede97914aa6edL;
	
	/**
	 * インスタンス化不可
	 */
	private EBLogger() {
	}
	
	/**
	 * ロガーを初期化する。
	 * 出力レベルWARNING以上。
	 * @param appName アプリケーション名
	 */
	public static void init(String appName) {
		EventLogger.register(GUID, appName, EventLogger.VIEWER_STRING);
	}

	/**
	 * ロガーを初期化する。
	 * @param appName アプリケーション名
	 * @param level 出力レベル
	 */
	public static void init(String appName, int level) {
		EventLogger.register(GUID, appName, EventLogger.VIEWER_STRING);
		EventLogger.setMinimumLevel(level);
	}

	/**
	 * ALWAYS_LOGレベルでログを出力する。
	 * 
	 * @param msg メッセージ
	 */
	public static void log(String msg) {
		EventLogger.logEvent(GUID, msg.getBytes());
	}
	
	/**
	 * 指定したレベルでログを出力する。
	 * 
	 * @param msg メッセージ
	 * @param level ログレベル(EventLogger.WARNINGなど)
	 */
	public static void log(String msg, int level) {
		EventLogger.logEvent(GUID, msg.getBytes(), level);
	}
}
