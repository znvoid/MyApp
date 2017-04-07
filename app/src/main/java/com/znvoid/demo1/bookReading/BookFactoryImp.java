package com.znvoid.demo1.bookReading;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * Created by zn on 2016/12/13.
 */

public class BookFactoryImp implements BookFactory, BookMangerListener {

    private final Bitmap mBitmap;
    private final Canvas mPageCanvas;
    private final Bitmap mNextBitmap;
    private final Canvas mNextPageCanvas;


    private MappedByteBuffer m_mbBuf = null;
    private int m_mbBufLen = 0;
    private int m_mbBufBegin = 0;
    private int m_mbBufEnd = 0;
    private String m_strCharsetName = "GBK";
    private Bitmap m_book_bg = null;
    private int mWidth;
    private int mHeight;

    private Vector<String> m_lines = new Vector<String>();

    private int m_fontSize = 24;
    private int m_textColor = Color.BLACK;
  //  private int m_backColor = 0xffff9e85; // 背景颜色
    private int m_backColor = Color.WHITE; // 背景颜色
    private int marginWidth = 15; // 左右与边缘的距离
    private int marginHeight = 20; // 上下与边缘的距离

    private int mLineCount; // 每页可以显示的行数
    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private boolean m_isfirstPage, m_islastPage;

    // private int m_nLineSpaceing = 5;

    private Paint mPaint;
    private BookView mTarge;
    private BookManger mBookManager;
	private BookFactoryListener bookFactoryListener;

    public BookFactoryImp(int w, int h) {

        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(m_textColor);
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / m_fontSize-1); // 可显示的行数
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mPageCanvas = new Canvas(mBitmap);
        mNextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mNextPageCanvas = new Canvas(mNextBitmap);



    }

    protected byte[] readParagraphBack(int nFromPos) {
        int nEnd = nFromPos;
        int i;
        byte b0, b1;
        if (m_strCharsetName.equals("UTF-16LE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }

        } else if (m_strCharsetName.equals("UTF-16BE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = nEnd - 1;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;
        int nParaSize = nEnd - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = m_mbBuf.get(i + j);
        }
        return buf;
    }

    // 读取上一段落
    protected byte[] readParagraphForward(int nFromPos) {
        int nStart = nFromPos;
        int i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < m_mbBufLen) {
                b0 = m_mbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int nParaSize = i - nStart;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = m_mbBuf.get(nFromPos + i);
        }
        return buf;
    }
    protected Vector<String> pageDown() {
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 读取一个段落
            m_mbBufEnd += paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String strReturn = "";
            if (strParagraph.indexOf("\r\n") != -1) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            if (strParagraph.length() != 0) {
                try {
                    m_mbBufEnd -= (strParagraph + strReturn).getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }
    protected void pageUp() {
        if (m_mbBufBegin < 0)
            m_mbBufBegin = 0;
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && m_mbBufBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack(m_mbBufBegin);
            m_mbBufBegin -= paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");

            if (strParagraph.length() == 0) {
                paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
            }
            lines.addAll(0, paraLines);
        }
        while (lines.size() > mLineCount) {
            try {
                m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        m_mbBufEnd = m_mbBufBegin;
        return;
    }

    public void draw(Canvas canvas) {
        if (m_lines.size() == 0)
            m_lines = pageDown();
        if (m_lines.size() > 0) {
          
			if (m_book_bg == null)
                canvas.drawColor(m_backColor);
            else{
              //  canvas.drawBitmap(m_book_bg, 0, 0, null);
//            Matrix	 cMatrix=	 new Matrix();
//            float sx=mWidth/m_book_bg.getWidth();
//            float sy=mHeight/m_book_bg.getHeight();
//            cMatrix.setScale(sx, sy);
//            canvas.drawBitmap(m_book_bg, cMatrix, null);
            	 canvas.drawBitmap(m_book_bg,new Rect(0,0,m_book_bg.getWidth(),m_book_bg.getHeight()),new Rect(0,0,mWidth,mHeight),null);
            }
            int y = marginHeight;
            for (String strLine : m_lines) {
                y += (m_fontSize);
                canvas.drawText(strLine, marginWidth, y, mPaint);
            }
        }
        if (bookFactoryListener!=null) {
			bookFactoryListener.onBookProgressChange(m_mbBufBegin);
		}
        
        float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
        DecimalFormat df = new DecimalFormat("#0.0");
        String strPercent = df.format(fPercent * 100) + "%";
        int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
        canvas.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
    }


    public void prePage() {
        if (m_mbBufBegin <= 0) {
            m_mbBufBegin = 0;
            m_isfirstPage = true;
            return;
        } else
            m_isfirstPage = false;
        m_lines.clear();
        pageUp();
        m_lines = pageDown();



    }

    @Override
    public boolean isfirstPage() {
        return m_isfirstPage;
    }


    public void nextPage() {
        if (m_mbBufEnd >= m_mbBufLen) {
            m_islastPage = true;
            return;
        } else
            m_islastPage = false;
        m_lines.clear();
        m_mbBufBegin = m_mbBufEnd;
        m_lines = pageDown();
    }

    @Override
    public boolean islastPage() {
        return m_islastPage;
    }

    @Override
    public Bitmap getCurPage() {
        draw(mPageCanvas);
        return mBitmap;
    }

    @Override
    public Bitmap getPrePage() {
        prePage();
        draw(mNextPageCanvas);

        return mNextBitmap;
    }

    @Override
    public Bitmap getNextPage() {
        nextPage();
        draw(mNextPageCanvas);
        return mNextBitmap;
    }

    @Override
    public void setTarge(BookView bookView) {
    this.mTarge=bookView;
    }

    @Override
    public void setBookManage(BookManger bookManger) {
        mBookManager=bookManger;
        bookManger.addListener(this);
        m_mbBufLen=bookManger.getBookLength();
        m_mbBuf=bookManger.getBookBuffer();
        
    }



    @Override
    public void onChange(Paint paint, int progress, int color, Bitmap bitmap) {
        if (paint!=null){
            m_textColor = paint.getColor();
            mPaint.setColor(m_textColor);
            m_fontSize= (int) paint.getTextSize();
            mPaint.setTextSize(m_fontSize);
            mLineCount = (int) (mVisibleHeight / m_fontSize);
            m_mbBufEnd=m_mbBufBegin;
            m_lines.clear();
        }
        if (0<progress&&progress<m_mbBufLen){
            m_mbBufBegin = progress;
            m_mbBufEnd = progress;
            m_lines.clear();
        }
        if (bitmap!=null){

            m_book_bg=bitmap;

        }
        if (color!=-1){
            m_backColor=color;
            if (m_book_bg!=null){

                m_book_bg.recycle();
                m_book_bg=null;

            }

        }
        if (mTarge!=null) {
			 mTarge.handleChange(getCurPage());
		}
       

    }

    @Override
    public int getProgress() {
        return m_mbBufBegin;
    }

	@Override
	public void setBookFactoryListener(BookFactoryListener bookFactoryListener) {
		this.bookFactoryListener=bookFactoryListener;
		
	}
	
	
}
