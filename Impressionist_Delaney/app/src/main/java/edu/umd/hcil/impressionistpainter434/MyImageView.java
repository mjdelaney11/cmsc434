package edu.umd.hcil.impressionistpainter434;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.content.Context;

/**
 * Created by Mike on 11/20/2016.
 */

public class MyImageView extends ImageView {

    private Bitmap _initialState;
    private Paint _paint = new Paint();
    private boolean _dropTarget = false;

    public MyImageView(Context context){
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int def){
        super(context, attrs, def);
    }

    public void init(){
        _paint.setColor(Color.RED);
        _paint.setStrokeWidth(2.5f);
        _paint.setAntiAlias(true);

        _initialState = this.getDrawingCache();
    }

    public void dropTarget(){
        _dropTarget = true;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int size = 15;

        if(_initialState != null && canvas != null && !(_initialState.isRecycled())) {
            canvas.drawBitmap(_initialState, 0, 0, null);
            if(_dropTarget){
                _dropTarget = false;
                return;
            }
            Point p = ImpressionistView.getLastPoint();
            if(p != null) {
                canvas.drawLine(p.x + size, p.y, p.x - size, p.y, _paint);
                canvas.drawLine(p.x, p.y + size, p.x, p.y - size, _paint);
            }
        } else {
            _initialState = this.getDrawingCache();
        }
    }

}
