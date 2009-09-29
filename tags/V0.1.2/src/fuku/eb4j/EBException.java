package fuku.eb4j;

/**
 * 書籍例外クラス。
 *
 * @author Hisaya FUKUMOTO & CloudHunter
 */
public class EBException extends Exception {
    /** エラーコード (ディレクトリが見つからない) */
    public static final int DIR_NOT_FOUND = 0;
    /** エラーコード (ディレクトリが読めない) */
    public static final int CANT_READ_DIR = 1;
    /** エラーコード (ファイルが見つからない) */
    public static final int FILE_NOT_FOUND = 2;
    /** エラーコード (ファイルが読めない) */
    public static final int CANT_READ_FILE = 3;
    /** エラーコード (ファイル読み込みエラー) */
    public static final int FAILED_READ_FILE = 4;
    /** エラーコード (ファイルフォーマットエラー) */
    public static final int UNEXP_FILE = 5;
    /** エラーコード (ファイルシークエラー) */
    public static final int FAILED_SEEK_FILE = 6;

    /** エラーコード (CATALOGファイルに記載されたディレクトリが１つも見つからない) */
    public static final int DIRS_IN_CATALOG_NOT_FOUND = 1001;
    
    /** エラーコードの保持用 */
    private int code = -1;

    /**
     * 標準で用意されたメッセージを返します。
     * @param code エラーコード
     * @return 標準エラーメッセージ
     */
    private static String getStandardMessage(int code) {
    	final String[] standardMsgs = {
    	        "directory not found",
    	        "can't read directory",
    	        "file not found",
    	        "can't read a file",
    	        "failed to read a file",
    	        "unexpected format in a file",
    	        "failed to seek a file"
    	    };
    	
    	if(code >=0 && code < standardMsgs.length) {
    		return standardMsgs[code];
    	} else {
    		return "code=" + String.valueOf(code);
    	}
    }
    
    /**
	 * エラーコードを取得する。
	 * @return エラーコード
	 */
    public int getCode(){
    	return code;
    }
    
    /**
     * 指定されたエラーコードを持つEBExceptionを構築します。<BR>
     * メッセージ: "ERR_MSG"
     *
     * @param code エラーコード
     */
    public EBException(int code) {
        super(getStandardMessage(code));
    	this.code = code;
    }

    /**
     * 指定されたエラーコード、原因を持つEBExceptionを構築します。<BR>
     * メッセージ: "ERR_MSG (cause)"
     *
     * @param code エラーコード
     * @param cause 原因
     */
    public EBException(int code, Throwable cause) {
        super(getStandardMessage(code) + " (" + cause.getMessage() + ")");
    	this.code = code;
    }

    /**
     * 指定されたエラーコード、追加メッセージを持つEBExceptionを構築します。<BR>
     * メッセージ: "ERR_MSG (msg)"
     *
     * @param code エラーコード
     * @param msg 追加メッセージ
     */
    public EBException(int code, String msg) {
        super(getStandardMessage(code) + " (" + msg + ")");
    	this.code = code;
    }

    /**
     * 指定されたエラーコード、追加メッセージ、原因を持つEBExceptionを構築します。<BR>
     * メッセージ: "ERR_MSG (msg: cause)"
     *
     * @param code エラーコード
     * @param msg 追加メッセージ
     * @param cause 原因
     */
    public EBException(int code, String msg, Throwable cause) {
        super(getStandardMessage(code) + " (" + msg + ": " + cause.getMessage() + ")");
    	this.code = code;
    }

    /**
     * 指定されたエラーコード、追加メッセージを持つEBExceptionを構築します。<BR>
     * メッセージ: "ERR_MSG (msg1: msg2)"
     *
     * @param code エラーコード
     * @param msg1 追加メッセージ1
     * @param msg2 追加メッセージ2
     */
    public EBException(int code, String msg1, String msg2) {
        super(getStandardMessage(code) + " (" + msg1 + ": " + msg2 + ")");
    	this.code = code;
    }
}

// end of EBException.java
