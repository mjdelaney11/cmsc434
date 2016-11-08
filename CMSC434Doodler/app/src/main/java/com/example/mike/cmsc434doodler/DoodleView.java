package com.example.mike.cmsc434doodler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Mike on 11/2/2016.
 */

public class DoodleView extends View {

    private ArrayList<myPath> _paths = new ArrayList<myPath>();
    private Path _path = new Path();
    private Paint _paint = new Paint();
    private boolean clear = false;
    private boolean undo = false;
    private float size = 5.0f;
    private int color = Color.WHITE;

    public DoodleView(Context context) {
        super(context);
        init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(Object o, int i) {
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(this.undo || this.clear){
            this.undo = false;
            this.clear = false;
            canvas.drawARGB(255,255,255,255);
        }

        for(myPath p : _paths){
            canvas.drawPath(p.getPath(), p.getPaint());
        }
    }

    public void clear(){
        this.clear = true;
        _paths.clear();

        invalidate();
    }

    public void undo() {
        this.undo = true;
       if(_paths.size() > 0){
           _paths.remove(_paths.size()-1);
           invalidate();
       }
    }

    public void setSize(float size){
        this.size = size;
    }

    public void setPaintColor(int color){
        this.color = color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _path.reset();
                _path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                _path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        _paint.setStrokeWidth(size);
        _paint.setColor(color);
        Path path = new Path(_path);
        Paint paint = new Paint(_paint);
        _paths.add(new myPath(path, paint));

        invalidate();

        return true;
    }
}