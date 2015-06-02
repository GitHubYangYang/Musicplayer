package com.example.musicplayer.view;

import com.example.musicplayer.service.MusicPlayService.MyBinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class VisualizerView extends View{

	private byte[] bytes;
	private float[] points;
	private Rect myRect = new Rect();
	private Paint myPaint = new Paint();	
	public VisualizerView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		bytes = null;
		myPaint.setStrokeWidth(5f);
		myPaint.setAntiAlias(true);
		myPaint.setColor(Color.WHITE);
	}
	
	public void updateVisualizer(byte[] mbyte){
//		byte[] model = new byte[mbyte.length / 2 + 1];
//		model[0] = (byte)Math.abs(mbyte[0]);
//		for(int i = 1,j = 1;j < 52 ;){
//			model[j] = (byte)Math.hypot(mbyte[i], mbyte[i+1]);
//			i += 2;
//			j++;
//		}
		bytes = mbyte;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bytes == null)
			return;
		if(points == null || points.length < bytes.length*4)
			points = new float[bytes.length*4];
		myRect.set(0, 0, getWidth(), getHeight());
//		for(int i = 0;i < bytes.length-1;i++){
//			points[i*4] = myRect.width()*i/(bytes.length - 1);
//			points[i*4+1] = myRect.height()/2 + ((byte)(bytes[i]+128))*(myRect.height()/2)/128;
//			points[i*4+2] = myRect.width()*(i+1)/(bytes.length-1);
//			points[i*4+3] = myRect.height()/2 + ((byte)(bytes[i+1]+128))*(myRect.height()/2)/128;
//		}
        final int baseX = myRect.width()/52;  
        final int height = myRect.height() - 31;  

        for (int i = 0; i < 52 ; i++)  
        {  
            if (bytes[i*3 +i] < 0)  
            {  
            	bytes[i*3 + i] = 127;
            }  
              
            final int xi = baseX*i + baseX/2;  
              
            points[i * 4] = xi + 2;  
            points[i * 4 + 1] = height;  
              
            points[i * 4 + 2] = xi + 2;  
            points[i * 4 + 3] = bytes[i*3 + i];
            Log.i("mylog", "height:" + height + "  byte i:" + bytes[i*3 + i]);
        }  
		canvas.drawLines(points, myPaint);
	}
}
