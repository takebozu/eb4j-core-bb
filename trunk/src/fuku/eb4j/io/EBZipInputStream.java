package fuku.eb4j.io;

import java.io.EOFException;
import java.io.IOException;

import net.cloudhunter.bb.util.BasicLogger;
import net.cloudhunter.compat.java.io.FileNotFoundException;
import net.cloudhunter.compat.java.io.RandomAccessFile;
import net.cloudhunter.compat.java.util.zip.DataFormatException;
import net.cloudhunter.compat.java.util.zip.Inflater;
import net.rim.device.api.system.EventLogger;
import fuku.eb4j.EBException;
import fuku.eb4j.util.ByteUtil;

/**
 * EBZIP形式の書籍入力ストリームクラス。
 *
 * @author Hisaya FUKUMOTO
 */
public class EBZipInputStream
    extends BookInputStream implements EBZipConstants {

	/** 
	 * パフォーマンス改善のために追加した入力ストリーム。
	 * インデックスデータをstreamで読み、ファイル位置が大きく異なることが多いデータはこのstream2で読む。
	 * InputStream#skipが、通常のJavaにおけるseek()に比べて極端に遅いことへの対応。 
	 */
    protected RandomAccessFile stream2 = null;


    /**
     * コンストラクタ。
     *
     * @param info ファイル情報
     * @exception EBException 入出力エラーが発生した場合
     */
    protected EBZipInputStream(FileInfo info) throws EBException {
        super(info);
        open();
        // スライス単位でキャッシュする
        cache = new byte[info.getSliceSize()];
    }


    /**
     * このファイルを開きます。
     *
     * @exception EBException 入出力エラーが発生した場合
     */
    protected void open() throws EBException {
    	super.open();

        try {
            stream2 = new RandomAccessFile(info.getFile(), "r");
        } catch (FileNotFoundException e) {
            throw new EBException(EBException.FILE_NOT_FOUND, info.getPath() + ":" + e.getMessage());
        }
    }
    
    /**
     * このファイルを閉じます。
     *
     */
    public void close() {
        super.close();
        if (stream2 != null) {
            try {
                stream2.close();
                stream2 = null;
            } catch (IOException e) {
            }
        }
    }

    /**
     * このファイルからlenバイトのデータをバイト配列に読み込みます。
     *
     * @param b データの読み込み先のバッファ
     * @param off データの開始オフセット
     * @param len 読み込まれる最大バイト数
     * @exception EBException 入出力エラーが発生した場合
     */
    protected void readRawFully2(byte[] b, int off, int len) throws EBException {
        try {
            stream2.readFully(b, off, len);
        } catch (EOFException e) {
            throw new EBException(EBException.FAILED_READ_FILE, info.getPath(), e);
        } catch (IOException e) {
            throw new EBException(EBException.FAILED_READ_FILE, info.getPath(), e);
        }
    }

    /**
     * EBZIP形式のファイル情報を初期化します。
     *
     * @exception EBException 入出力エラーが発生した場合
     */
//    @Override
    protected void initFileInfo() throws EBException {
        try {
            info.setRealFileSize(stream.length());
        } catch (IOException e) {
            throw new EBException(EBException.FAILED_READ_FILE, info.getPath(), e);
        }

        // ヘッダの読み込み
        byte[] b = new byte[EBZIP_HEADER_SIZE];
        readRawFully(b, 0, b.length);

        info.setZipMode(b[5] >>> 4);
        info.setZipLevel(b[5] & 0x0f);
        info.setSliceSize(PAGE_SIZE << info.getZipLevel());
        if(info.getZipMode() == EBFile.FORMAT_EBZIP) {
        	info.setFileSize(ByteUtil.getLong4(b, 10));
        } else {
        	info.setFileSize(ByteUtil.getLong5(b, 9));
        }
        info.setZipCRC(ByteUtil.getLong4(b, 14));

        if (info.getFileSize() < (1L<<16)) {
            info.setZipIndexSize(2);
        } else if (info.getFileSize() < (1L<<24)) {
            info.setZipIndexSize(3);
        } else if (info.getFileSize() < (1L<<32)) {
            info.setZipIndexSize(4);
        } else {
            info.setZipIndexSize(5);
        }

        // 妥当性の検証
        String str = new String(b, 0, 5);
        if (!str.equals("EBZip")
            || (info.getZipMode() != 1 && info.getZipMode() != 2)	//1:4GB(4,294,967,295byte)以下、2:4GB超
            || info.getSliceSize() > (PAGE_SIZE << EBZIP_MAX_LEVEL)) {
            throw new EBException(EBException.UNEXP_FILE, info.getPath());
        }
        super.initFileInfo();
    }

    /**
     * このファイルの圧縮レベルを返します。
     *
     * @return 圧縮レベル
     */
    public int getLevel() {
        return info.getZipLevel();
    }

    /**
     * このファイルのCRCを返します。
     *
     * @return CRC
     */
    public long getCRC() {
        return info.getZipCRC();
    }

    /**
     * EBZIP形式のファイルから最大lenバイトのデータをバイト配列に読み込みます。
     *
     * @param b データの読み込み先のバッファ
     * @param off データの開始オフセット
     * @param len 読み込まれる最大バイト数
     * @return バッファに読み込まれたバイトの合計数
     *         (ストリームの終わりに達してデータがない場合は-1)
     * @exception EBException 入出力エラーが発生した場合
     */
//    @Override
    public int read(byte[] b, int off, int len) throws EBException {
        int rlen = 0;
        while (rlen < len) {
            if (info.getFileSize() <= filePos) {
                // ストリームの終わり
                if (rlen == 0) {
                    // データを読み込んでいなければ-1
                    return -1;
                } else {
                    // データを読み込んでいればバイト数を返す
                    return rlen;
                }
            }
            // キャッシュの作成
            if (cachePos < 0
                || filePos < cachePos
                || cachePos + info.getSliceSize() <= filePos) {
                // キャッシュのデータ位置
                // filePosの位置が含まれるスライスの先頭位置
                cachePos = filePos - (filePos % info.getSliceSize());

                // 圧縮されたスライスのインデックスデータの位置
                // (スライスオフセット * インデックスサイズ) + ヘッダサイズ
                long pos = filePos / info.getSliceSize() * info.getZipIndexSize()
                    + EBZIP_HEADER_SIZE;
                try {
                    stream.seek(pos);
                } catch (IOException e) {
                	BasicLogger.log("Failed to seek file", EventLogger.ERROR);
                    throw new EBException(EBException.FAILED_SEEK_FILE, info.getPath(), e);
                }
                byte[] buf = new byte[info.getZipIndexSize()*2];
                readRawFully(buf, 0, buf.length);
                
                // スライス位置の取得
                long slicePos = 0L;
                long nextSlicePos = 0L;
                switch (info.getZipIndexSize()) {
                    case 2:
                        slicePos = ByteUtil.getInt2(buf, 0);
                        nextSlicePos = ByteUtil.getInt2(buf, 2);
                        break;
                    case 3:
                        slicePos = ByteUtil.getInt3(buf, 0);
                        nextSlicePos = ByteUtil.getInt3(buf, 3);
                        break;
                    case 4:
                        slicePos = ByteUtil.getLong4(buf, 0);
                        nextSlicePos = ByteUtil.getLong4(buf, 4);
                        break;
                    case 5:
                        slicePos = ByteUtil.getLong5(buf, 0);
                        nextSlicePos = ByteUtil.getLong5(buf, 5);
                        break;
                    default:
                        break;
                }

                // 圧縮されたスライスのサイズ
                int sliceSize = (int)(nextSlicePos - slicePos);
                if (sliceSize <= 0 || info.getSliceSize() < sliceSize) {
                    return -1;
                }

                // 圧縮スライスをデコードしてキャッシュに読み込む
                try {
                    stream2.seek(slicePos);
                } catch (IOException e) {
                	BasicLogger.log("Failed to seek file", EventLogger.ERROR);
                    throw new EBException(EBException.FAILED_SEEK_FILE, info.getPath(), e);
                }
                _decode(sliceSize);
            }

            // キャッシュからデータの取得
            int n = (int)(info.getSliceSize() - (filePos % info.getSliceSize()));
            if (len - rlen < n) {
                n = len - rlen;
            }
            if (info.getFileSize() - filePos < n) {
                n = (int)(info.getFileSize() - filePos);
            }
            int p = (int)(filePos % info.getSliceSize());
            System.arraycopy(cache, p, b, off+rlen, n);
            rlen += n;
            filePos += n;
        }
        return rlen;
    }

    /**
     * 復号化します。
     *
     * @param size 圧縮スライスサイズ
     * @exception EBException 入出力エラーが発生した場合
     */
    private void _decode(int size) throws EBException {
        if (size == info.getSliceSize()) {
            // 圧縮されていないのでそのままキャッシュに読み込む
            readRawFully2(cache, 0, size);
        } else {
            byte[] b = new byte[size];
            Inflater inf = new Inflater();
            try {
                // 圧縮されたスライスをキャッシュに展開する
                readRawFully2(b, 0, size);
                inf.setInput(b, 0, size);
                inf.inflate(cache, 0, info.getSliceSize());
            } catch (DataFormatException e) {
                throw new EBException(EBException.UNEXP_FILE, info.getPath(), e);
            } finally {
                inf.end();
            }
        }
    }
}

// end of EBZipInputStream.java
