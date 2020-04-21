package com.tb.pcmrecord;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO: document your custom view class.
 */
public class AudioView extends View {

    private Paint lumpPaint;

    private static final int LUMP_MAX_HEIGHT = 300;



    private ArrayList<Short> inBuf = new ArrayList<Short>();



    public AudioView(Context context) {
        super(context);
        init();
    }

    public AudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /**
         * 数转换策略： 将数据拆分成N段，每段数据大小为DEFAULT_FFT_THRUPUT，对每段数据进行FFT转换，提取特征频谱的相对振幅，再用于数据的可视化
         */

        lumpPaint = new Paint();
        lumpPaint.setAntiAlias(true);
        lumpPaint.setColor(Color.RED);
        lumpPaint.setStrokeWidth(1);
        lumpPaint.setStyle(Paint.Style.STROKE);

    }


    public void showPcmFileWave(File file) {

        inBuf.clear();

        try (FileInputStream fis = new FileInputStream(file)) {

            byte[] b = new byte[1024 * 4];

            while ((fis.read(b)) != -1) {

                short[] wbs = Bytes2Shorts(b);
                for(int i = 0; i<wbs.length;i++){
                    inBuf.add(wbs[i]);
                }
            }

            postInvalidate();
        } catch (Exception e) {
            Log.d("",e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        if (modeW == MeasureSpec.AT_MOST) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (modeW == MeasureSpec.EXACTLY) {
            width = widthMeasureSpec;
        }
        if (modeW == MeasureSpec.UNSPECIFIED) {
            width = 96000;
        }
        int modeH = MeasureSpec.getMode(height);
        if (modeH == MeasureSpec.AT_MOST) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        if (modeH == MeasureSpec.EXACTLY) {
            height = heightMeasureSpec;
        }
        if (modeH == MeasureSpec.UNSPECIFIED) {
            height = LUMP_MAX_HEIGHT *2;
        }
        setMeasuredDimension(width, height);
    }


    public short getShort(byte[] buf) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;

        for (int i = buf.length - 1; i >= 0; i--) {
            r <<= 8;
            r |= (buf[i] & 0x00ff);
        }


        return r;
    }

    public short[] Bytes2Shorts(byte[] buf) {
        byte bLength = 2;
        short[] s = new short[buf.length / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = new byte[bLength];
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                temp[jLoop] = buf[iLoop * bLength + jLoop];
            }
            s[iLoop] = getShort(temp);
        }
        return s;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.LTGRAY);

        int rateY = (65535 /2/ LUMP_MAX_HEIGHT);
        int rateX = inBuf.size()/canvas.getWidth();

        int line = 0;
        for (int i = 0; i < inBuf.size(); i=i+rateX) {

            int value = inBuf.get(i);


            int xs = value/rateY;

            canvas.drawLine(line,LUMP_MAX_HEIGHT-xs,line,LUMP_MAX_HEIGHT + xs ,lumpPaint);
            line++;
        }
    }
}
