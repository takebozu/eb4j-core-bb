package fuku.eb4j;

import net.cloudhunter.bb.EBLogger;
import net.cloudhunter.compat.java.io.File;
import net.cloudhunter.compat.java.util.ArrayList;
import net.cloudhunter.compat.java.util.List;
import net.rim.device.api.system.EventLogger;
import fuku.eb4j.io.BookInputStream;
import fuku.eb4j.io.EBFile;
import fuku.eb4j.util.ByteUtil;

/**
 * 書籍クラス。
 *
 * @author Hisaya FUKUMOTO
 */
public class Book {

    /** 電子ブック(EB/EBG/EBXA/EBXA-C/S-EBXA)を示す定数 */
    public static final int DISC_EB = 0;
    /** EPWING形式の書籍を示す定数 */
    public static final int DISC_EPWING = 1;

    /** ISO 8859-1文字セットを示す定数 */
    public static final int CHARCODE_ISO8859_1 = 1;
    /** JIS X 0208文字セットを示す定数 */
    public static final int CHARCODE_JISX0208 = 2;
    /** JIS X 0208/GB 2312文字セットを示す定数 */
    public static final int CHARCODE_JISX0208_GB2312 = 3;

    /** CATALOG(S)ファイル内のデータサイズ */
    static final int[] SIZE_CATALOG = {40, 164};
    /** タイトルのデータサイズ */
    static final int[] SIZE_TITLE = {30, 80};
    /** ディレクトリ名のデータサイズ */
    static final int SIZE_DIRNAME = 8;

    /** 文字コードを正しく判断できない書籍の最初の副本のタイトル */
    private static final String[] MISLEADED = {
        // SONY DataDiskMan (DD-DR1) accessories.
        // (センチュリ＋ビジネス＋クラウン)
        "%;%s%A%e%j!\\%S%8%M%9!\\%/%i%&%s",
        // Shin Eiwa Waei Chujiten (earliest edition)
        // (研究社　新英和中辞典)
        "8&5f<R!!?71QOBCf<-E5",
        // EB Kagakugijutsu Yougo Daijiten (YRRS-048)
        // (ＥＢ科学技術用語大辞典)
        "#E#B2J3X5;=QMQ8lBg<-E5",
        // Nichi-Ei-Futsu Jiten (YRRS-059)
        // (ＥＮＧ／ＪＡＮ（＋ＦＲＥ）)
        "#E#N#G!?#J#A#N!J!\\#F#R#E!K",
        // Japanese-English-Spanish Jiten (YRRS-060)
        // (ＥＮＧ／ＪＡＮ（＋ＳＰＡ）)
        "#E#N#G!?#J#A#N!J!\\#S#P#A!K"
    };


    /** 書籍のディレクトリ */
    private String _bookPath = null;

    /** 書籍の種類 */
    private int _bookType = -1;
    /** 文字セットの種類 */
    private int _charCode = -1;
    /** EPWINGフォーマットのバージョン */
    private int _version = -1;

    /** 副本 */
    private SubBook[] _sub = null;


    /**
     * コンストラクタ。
     *
     * @param bookPath 書籍のパス
     * @exception EBException 初期化中にエラーが発生した場合
     */
    public Book(String bookPath) throws EBException {
        this(bookPath, null);
    }

    /**
     * コンストラクタ。
     *
     * @param bookDir 書籍のパス
     * @exception EBException 初期化中にエラーが発生した場合
     */
    public Book(File bookDir) throws EBException {
        this(bookDir, null);
    }

    /**
     * コンストラクタ。
     *
     * @param bookPath 書籍のパス
     * @param appendixPath 付録パッケージのパス
     * @exception EBException 初期化中にエラーが発生した場合
     */
    public Book(String bookPath, String appendixPath) throws EBException {
        this(new File(bookPath),
             appendixPath == null ? null : new File(appendixPath));
    }

    /**
     * コンストラクタ。
     *
     * @param bookDir 書籍のパス
     * @param appendixDir 付録パッケージのパス
     * @exception EBException 初期化中にエラーが発生した場合
     */
    public Book(File bookDir, File appendixDir) throws EBException {
        super();

        _bookPath = bookDir.getPath();
        if (!bookDir.isDirectory()) {
            throw new EBException(EBException.DIR_NOT_FOUND, _bookPath);
        }
        if (!bookDir.canRead()) {
            throw new EBException(EBException.CANT_READ_DIR, _bookPath);
        }
        _loadLanguage(bookDir);
        _loadCatalog(bookDir);

        // 付録パッケージの設定
        if (appendixDir != null) {
            Appendix appendix = new Appendix(appendixDir);
            SubAppendix[] sa = appendix.getSubAppendixes();
            int len = sa.length;
            if (len > _sub.length) {
                len = _sub.length;
            }
            for (int i=0; i<len; i++) {
                _sub[i].setAppendix(sa[i]);
            }
        }
    }


    /**
     * この書籍のパスを文字列で返します。
     *
     * @return 書籍のパスの文字列形式
     */
    public String getPath() {
        return _bookPath;
    }

    /**
     * この書籍の種類を返します。
     *
     * @return 書籍の種類を示すフラグ
     * @see Book#DISC_EB
     * @see Book#DISC_EPWING
     */
    public int getBookType() {
        return _bookType;
    }

    /**
     * この書籍の文字セットを返します。
     *
     * @return 書籍の文字セットを示すフラグ
     * @see Book#CHARCODE_ISO8859_1
     * @see Book#CHARCODE_JISX0208
     * @see Book#CHARCODE_JISX0208_GB2312
     */
    public int getCharCode() {
        return _charCode;
    }

    /**
     * この書籍の副本数を返します。
     *
     * @return 副本数
     */
    public int getSubBookCount() {
        int ret = 0;
        if (_sub != null) {
            ret = _sub.length;
        }
        return ret;
    }

    /**
     * この書籍の副本リストを返します。
     *
     * @return 副本の配列
     */
    public SubBook[] getSubBooks() {
        if (_sub == null) {
            return new SubBook[0];
        }
        int len = _sub.length;
        SubBook[] list = new SubBook[len];
        System.arraycopy(_sub, 0, list, 0, len);
        return list;
    }

    /**
     * この書籍の指定したインデックスの副本を返します。
     *
     * @param index インデックス
     * @return 副本 (範囲外のインデックス時はnull)
     */
    public SubBook getSubBook(int index) {
        if (index < 0 || index >= _sub.length) {
            return null;
        }
        return _sub[index];
    }

    /**
     * EPWINGのバージョンを返します。
     *
     * @return EPWINGのバージョン
     */
    public int getVersion() {
        return _version;
    }

    /**
     * CATALOG(S)ファイルから情報を読み込みます。
     *
     * @param dir 書籍のディレクトリ
     * @exception EBException CATALOG(S)ファイルの読み込み中にエラーが発生した場合
     */
    private void _loadCatalog(File dir) throws EBException {
        // カタログファイルの検索
        EBFile file = null;
        try {
            file = new EBFile(dir, "catalog", EBFile.FORMAT_PLAIN);
            _bookType = DISC_EB;
            _loadCatalogEB(file);
        } catch (EBException e) {
            file = new EBFile(dir, "catalogs", EBFile.FORMAT_PLAIN);
            _bookType = DISC_EPWING;
            _loadCatalogEPWING(file);
        }
    }

    /**
     * CATALOGファイルから情報を読み込みます。
     *
     * @param file CATALOGファイル
     * @exception EBException CATALOGファイルの読み込み中にエラーが発生した場合
     */
    private void _loadCatalogEB(EBFile file) throws EBException {
    	List subBooks = new ArrayList();
        BookInputStream bis = file.getInputStream();
        try {
            byte[] b = new byte[16];
            bis.readFully(b, 0, b.length);

            // 副本数の取得
            int subCount = ByteUtil.getInt2(b, 0);
            if (subCount <= 0) {
                throw new EBException(EBException.UNEXP_FILE, file.getPath());
            }

            // 副本の情報を取得
            b = new byte[SIZE_CATALOG[DISC_EB]];
            for (int i=0; i<subCount; i++) {
                bis.readFully(b, 0, b.length);

                // タイトルの取得
                int off = 2;
                String title = null;
                if (_charCode == CHARCODE_ISO8859_1) {
                    title = new String(b, off, SIZE_TITLE[DISC_EB]).trim();
                    // タイトルの修正が必要かどうか調べる
                    for (int j=0; j<MISLEADED.length; j++) {
                        if (title.equals(MISLEADED[j])) {
                            // タイトルの修正
                            _charCode = CHARCODE_JISX0208;
                            byte[] tmp = title.getBytes();
                            title = ByteUtil.jisx0208ToString(tmp, 0, tmp.length);
                            break;
                        }
                    }
                } else {
                    title = ByteUtil.jisx0208ToString(b, off,
                                                      SIZE_TITLE[DISC_EB]);
                }

                // ディレクトリ名の取得
                String name = new String(b, off, SIZE_DIRNAME).trim();

                // 副本オブジェクトの作成
                String[] fname = new String[3];
                int[] format = new int[3];
                fname[0] = "start";
                format[0] = EBFile.FORMAT_PLAIN;
                try {
                	subBooks.add(new SubBook(this, title, name, 1, fname, format, null, null));
                } catch(EBException e) {
                	//ディレクトリが存在しないなどの理由で、副本の初期化に失敗した場合
                	EBLogger.log("Skipped sub book\n-dir:" + name + "\n-catalog file:" + file.getPath(), EventLogger.WARNING);
                }
            }
        } finally {
            bis.close();
        }

        if(subBooks.size() == 0) {
        	EBLogger.log("No sub book directory found.\n-catalog file:" + file.getPath(), EventLogger.ERROR);
        	throw new EBException(EBException.DIRS_IN_CATALOG_NOT_FOUND);
        }
        _sub = (SubBook[])subBooks.toArray(new SubBook[subBooks.size()]);
    }

    /**
     * CATALOGSファイルから情報を読み込みます。
     *
     * @param file CATALOGSファイル
     * @exception EBException CATALOGSファイルの読み込み中にエラーが発生した場合
     */
    private void _loadCatalogEPWING(EBFile file) throws EBException {
    	List subBooks = new ArrayList();
        BookInputStream bis = file.getInputStream();
        try {
            byte[] b = new byte[16];
            bis.readFully(b, 0, b.length);

            // 副本数の取得
            int subCount = ByteUtil.getInt2(b, 0);
            if (subCount <= 0) {
                throw new EBException(EBException.UNEXP_FILE, file.getPath());
            }

            // EPWINGのバージョン取得
            _version = ByteUtil.getInt2(b, 2);

            // 副本の情報を取得
            b = new byte[SIZE_CATALOG[DISC_EPWING]];
            for (int i=0; i<subCount; i++) {
                bis.seek(16 + i * b.length);
                bis.readFully(b, 0, b.length);

                // タイトルの取得
                int off = 2;
                String title = null;
                if (_charCode == CHARCODE_ISO8859_1) {
                    title = new String(b, off, SIZE_TITLE[DISC_EPWING]).trim();
                    // タイトルの修正が必要かどうか調べる
                    for (int j=0; j<MISLEADED.length; j++) {
                        if (title.equals(MISLEADED[j])) {
                            // タイトルの修正
                            _charCode = CHARCODE_JISX0208;
                            byte[] tmp = title.getBytes();
                            title = ByteUtil.jisx0208ToString(tmp, 0, tmp.length);
                            break;
                        }
                    }
                } else {
                    title = ByteUtil.jisx0208ToString(b, off,
                                                      SIZE_TITLE[DISC_EPWING]);
                }
                off += SIZE_TITLE[DISC_EPWING];

                // ディレクトリ名の取得
                String name = new String(b, off, SIZE_DIRNAME).trim();
                off += SIZE_DIRNAME;

                // インデックス番号の取得
                int index = ByteUtil.getInt2(b, off+4);
                off += 6;

                // 外字ファイル名の取得
                String[] narrow = new String[4];
                String[] wide = new String[4];
                off += 4;
                for (int j=0; j<4; j++) {
                    // 全角外字
                    if (b[off] != '\0' && (b[off]&0xff) < 0x80) {
                        wide[j] = new String(b, off, SIZE_DIRNAME).trim();
                    }
                    // 半角外字
                    if (b[off+32] != '\0' && (b[off+32]&0xff) < 0x80) {
                        narrow[j] = new String(b, off+32, SIZE_DIRNAME).trim();
                    }
                    off += SIZE_DIRNAME;
                }

                // 副本のファイル名の取得
                String[] fname = new String[3];
                int[] format = new int[3];
                fname[0] = "honmon";
                format[0] = EBFile.FORMAT_PLAIN;

                if (_version != 1) {
                    // 拡張情報の取得
                    bis.seek(16 + (subCount + i) * b.length);
                    bis.readFully(b, 0, b.length);
                    if ((b[4] & 0xff) != 0) {
                        // 本文テキストファイル名
                        fname[0] = new String(b, 4, SIZE_DIRNAME).trim();
                        format[0] = b[55] & 0xff;

                        int dataType = ByteUtil.getInt2(b, 41);
                        // 画像ファイル名
                        if ((dataType & 0x03) == 0x02) {
                            fname[1] = new String(b, 44, SIZE_DIRNAME).trim();
                            format[1] = b[54] & 0xff;
                        } else if (((dataType>>>8) & 0x03) == 0x02) {
                            fname[1] = new String(b, 56, SIZE_DIRNAME).trim();
                            format[1] = b[53] & 0xff;
                        }

                        // 音声ファイル名
                        if ((dataType & 0x03) == 0x01) {
                            fname[2] = new String(b, 44, SIZE_DIRNAME).trim();
                            format[2] = b[54] & 0xff;
                        } else if (((dataType>>>8) & 0x03) == 0x01) {
                            fname[2] = new String(b, 56, SIZE_DIRNAME).trim();
                            format[2] = b[53] & 0xff;
                        }

                        for (int j=0; j<3; j++) {
                            switch (format[j]) {
                                case 0x00:
                                    format[j] = EBFile.FORMAT_PLAIN;
                                    break;
                                case 0x11:
                                    format[j] = EBFile.FORMAT_EPWING;
                                    break;
                                case 0x12:
                                    format[j] = EBFile.FORMAT_EPWING6;
                                    break;
                                default:
                                    throw new EBException(EBException.UNEXP_FILE, file.getPath());
                            }
                        }
                    }
                }

                // 副本オブジェクトの作成
                try {
                	subBooks.add(new SubBook(this, title, name, index, fname, format, narrow, wide));
                } catch(EBException e) {
                	//ディレクトリが存在しないなどの理由で、副本の初期化に失敗した場合
                	//⇒その副本はスキップする
                	EBLogger.log("Skipped sub book\n-dir:" + name + "\n-catalogs file:" + file.getPath(), EventLogger.WARNING);
                }
            }
        } finally {
            bis.close();
        }
        
        if(subBooks.size() == 0) {
        	EBLogger.log("No sub book directory found.\n-catalogs file:" + file.getPath(), EventLogger.ERROR);
        	throw new EBException(EBException.DIRS_IN_CATALOG_NOT_FOUND);
        }
        _sub = (SubBook[])subBooks.toArray(new SubBook[subBooks.size()]);
    }

    /**
     * LANGUAGEファイルから情報を読み込みます。
     *
     * @param dir 書籍のディレクトリ
     * @exception EBException LANGUAGEファイルの読み込み中にエラーが発生した場合
     */
    private void _loadLanguage(File dir) throws EBException {
        _charCode = CHARCODE_JISX0208;

        EBFile file = null;
        try {
            file = new EBFile(dir, "language", EBFile.FORMAT_PLAIN);
        } catch (EBException e) {
        }
        if (file == null) {
            return;
        }

        BookInputStream bis = file.getInputStream();
        try {
            byte[] b = new byte[16];
            if (bis.read(b, 0, b.length) != b.length) {
                return;
            }
            _charCode = ByteUtil.getInt2(b, 0);
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }
}

// end of Book.java
