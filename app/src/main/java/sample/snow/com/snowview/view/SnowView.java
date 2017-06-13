package sample.snow.com.snowview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import sample.snow.com.snowview.R;


public class SnowView extends View {
  private static final int NUM_SNOWFLAKES = 15;
  private static final int DELAY = 10;

  private SnowFlake[] snowflakes;

  public SnowView(Context context) {
    super(context);
    initSnowBitmap();
  }

  public SnowView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initSnowBitmap();
  }

  public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initSnowBitmap();
  }

  Bitmap snowBitmap;

  private void initSnowBitmap() {
    snowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_cpu_cooler_snow);
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

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (w != oldw || h != oldh) {
      resize(w, h);
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (SnowFlake snowFlake : snowflakes) {
      snowFlake.draw(canvas);
    }
    handler.sendEmptyMessageDelayed(123, DELAY);
  }

  private Handler handler = new Handler() {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case 123:
          handler.removeMessages(123);
          invalidate();
          break;
      }
    }
  };
}
