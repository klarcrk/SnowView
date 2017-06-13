package sample.snow.com.snowview.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import sample.snow.com.snowview.R;


/**
 * Created on 2016/12/5 18:28.
 * Project android_zzkko
 * Copyright (c) 2016 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #              hello         world                  #
 * #                                                   #
 */

public class SnowSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
  public SnowSurfaceView(Context context) {
    super(context);
    initView();
  }

  public SnowSurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public SnowSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  private final int NUM_SNOWFLAKES = 20;
  private final int DELAY = 5;
  private SnowFlake[] snowflakes;
  Bitmap snowBitmap;
  private DrawClock drawClock;

  private void initView() {
    snowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_cpu_cooler_snow);
    SurfaceHolder holder = getHolder();
    setZOrderOnTop(true);    // necessary
    holder.setFormat(PixelFormat.TRANSLUCENT);
    holder.addCallback(this);
  }

  protected void resize(int width, int height) {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    snowflakes = new SnowFlake[NUM_SNOWFLAKES];
    for (int i = 0; i < NUM_SNOWFLAKES; i++) {
      snowflakes[i] = SnowFlake.create(snowBitmap, width, height, paint);
    }
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    resize(width, height);
  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    drawClock = new DrawClock(getHolder(), getResources());
    drawClock.setRunning(true);
    drawClock.start();
  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {
    boolean retry = true;
    drawClock.setRunning(false);
    while (retry) {
      try {
        drawClock.join();
        retry = false;
      } catch (InterruptedException e) {
      }
    }
  }

  class DrawClock extends Thread {
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Paint painter;

    public DrawClock(SurfaceHolder surfaceHolder, Resources resources) {
      this.surfaceHolder = surfaceHolder;
      this.painter = new Paint();
      this.painter.setStyle(Paint.Style.FILL);
      this.painter.setAntiAlias(true);
      this.painter.setFilterBitmap(true);
    }

    public void setRunning(boolean run) {
      runFlag = run;
    }

    @Override public void run() {
      Canvas canvas;
      while (runFlag) {
        canvas = null;
        try {
          canvas = surfaceHolder.lockCanvas(null);
          synchronized (surfaceHolder) {
            canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
            for (SnowFlake snowFlake : snowflakes) {
              snowFlake.draw(canvas);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (canvas != null) {
            try {
              surfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
            }
          }
        }
        try {
          Thread.sleep(DELAY);
        } catch (Exception e) {
        }
      }
    }
  }
}
