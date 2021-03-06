package edu.umd.hcil.impressionistpainter434;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;

import java.text.MessageFormat;
import java.util.Random;

/**
 * Created by jon on 3/20/2016.
 */
public class ImpressionistView extends View {

    private MyImageView _imageView;
    private Canvas _offScreenCanvas = null;
    private Bitmap _offScreenBitmap = null;
    private Paint _paint = new Paint();
    Random _rand = new Random();
    private VelocityTracker _speed;
    private int _alpha = 150;
    private int _defaultRadius = 25;
    private int _minRadius = 5;
    private static Point _lastPoint = null;
    private long _lastPointTime = -1;
    private boolean _useMotionSpeedForBrushStrokeSize = true;
    private Paint _paintBorder = new Paint();
    private BrushType _brushType = BrushType.Square;

    public ImpressionistView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Because we have more than one constructor (i.e., overloaded constructors), we use
     * a separate initialization method
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle){

        // Set setDrawingCacheEnabled to true to support generating a bitmap copy of the view (for saving)
        // See: http://developer.android.com/reference/android/view/View.html#setDrawingCacheEnabled(boolean)
        //      http://developer.android.com/reference/android/view/View.html#getDrawingCache()
        this.setDrawingCacheEnabled(true);
        _rand.setSeed(65536);
        _paint.setColor(Color.RED);
        _paint.setAlpha(_alpha);
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.FILL);
        _paint.setStrokeWidth(4);

        _paintBorder.setColor(Color.BLACK);
        _paintBorder.setStrokeWidth(3);
        _paintBorder.setStyle(Paint.Style.STROKE);
        _paintBorder.setAlpha(50);


        //_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    }

    public Bitmap getBitmap(){
        return _offScreenBitmap;
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){

        Bitmap bitmap = getDrawingCache();
        Log.v("onSizeChanged", MessageFormat.format("bitmap={0}, w={1}, h={2}, oldw={3}, oldh={4}", bitmap, w, h, oldw, oldh));
        if(bitmap != null) {
            _offScreenBitmap = getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
            _offScreenCanvas = new Canvas(_offScreenBitmap);
        }
    }

    /**
     * Sets the ImageView, which hosts the image that we will paint in this view
     * @param imageView
     */
    public void setImageView(MyImageView imageView){
        _imageView = imageView;
        _imageView.init();
    }

    /**
     * Sets the brush type. Feel free to make your own and completely change my BrushType enum
     * @param brushType
     */
    public void setBrushType(BrushType brushType){
        _brushType = brushType;
    }

    /**
     * Clears the painting
     */
    public void clearPainting(){
        if(_offScreenCanvas != null) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            _offScreenCanvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(_offScreenBitmap != null) {
            canvas.drawBitmap(_offScreenBitmap, 0, 0, _paint);
        }

        // Draw the border. Helpful to see the size of the bitmap in the ImageView
        canvas.drawRect(getBitmapPositionInsideImageView(_imageView), _paintBorder);

    }

    // https://github.com/m-furman/Impressionist-Painter/blob/master/app/src/main/java/edu/umd/hcil/impressionistpainter434/ImpressionistView.java
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int historySize = motionEvent.getHistorySize();
                int range = 40;

                if (_speed == null){
                    _speed = VelocityTracker.obtain();
                } else {
                    _speed.clear();
                }

                _speed.addMovement(motionEvent);
                _speed.computeCurrentVelocity(1000);
                float xVelocity = _speed.getXVelocity();
                float yVelocity = _speed.getYVelocity();
                int radius = (int)Math.sqrt((int)xVelocity^2 + (int)yVelocity^2);

                for (int i = 0; i < historySize; i++) {
                    float touchX = motionEvent.getHistoricalX(i);
                    float touchY = motionEvent.getHistoricalY(i);
                    _defaultRadius = (radius <= _minRadius) ? _defaultRadius : radius;
                    Bitmap bitmap = _imageView.getDrawingCache();
                     if(touchX >=0 && touchY >=0 && touchX < bitmap.getWidth() && touchY < bitmap.getHeight() && bitmap != null) {
                        int pixel = bitmap.getPixel((int)touchX, (int)touchY);
                        _paint.setColor(pixel);
                         _paint.setAlpha(_alpha);
                    }
                    switch(_brushType.toString()){
                        case("Circle"):
                            _offScreenCanvas.drawCircle(touchX, touchY, _defaultRadius, _paint);
                            break;
                        case("Square"):
                             _offScreenCanvas.drawRect(touchX - _defaultRadius, touchY - _defaultRadius,
                                   touchX + _defaultRadius, touchY + _defaultRadius, _paint);
                            break;
                        case("Line"):
                            _offScreenCanvas.drawLine(touchX, touchY + 20, touchX, touchY-20, _paint);
                            break;
                        case("CircleSplatter"):
                            for(int j = 0; j < 11; j++){
                                int deltaX = _rand.nextInt()%range;
                                int deltaY = _rand.nextInt()%range;
                                int deltaR = (_rand.nextInt()%range)/2;
                                if(j%2==0){
                                    _offScreenCanvas.drawCircle(touchX + deltaX, touchY + deltaY, deltaR, _paint);
                                 }else {
                                    _offScreenCanvas.drawCircle(touchX - deltaX, touchY - deltaY, deltaR, _paint);
                                }
                            }
                            break;
                        case("LineSplatter"):
                            for(int j = 0; j < 11; j++){
                                int deltaX = _rand.nextInt()%range;
                                int deltaY = _rand.nextInt()%range;
                                if(j%2==0){
                                    _offScreenCanvas.drawLine(touchX + deltaX, touchY + 20 + deltaY,
                                            touchX - deltaX, touchY-20 - deltaY, _paint);
                                }else {
                                    _offScreenCanvas.drawLine(touchX - deltaX, touchY + 20 - deltaY,
                                            touchX + deltaX, touchY - 20 + deltaY, _paint);
                                }
                            }
                            break;
                        default:
                            _offScreenCanvas.drawCircle(touchX, touchY, _defaultRadius, _paint);
                    }
                    _lastPoint = new Point((int)touchX, (int)touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                _imageView.dropTarget();
                break;
        }

        invalidate();
        _imageView.invalidate();

        return true;
    }

    public static Point getLastPoint(){
        return _lastPoint;
    }

    /**
     * This method is useful to determine the bitmap position within the Image View. It's not needed for anything else
     * Modified from:
     *  - http://stackoverflow.com/a/15538856
     *  - http://stackoverflow.com/a/26930938
     * @param imageView
     * @return
     */
    private static Rect getBitmapPositionInsideImageView(ImageView imageView){
        Rect rect = new Rect();

        if (imageView == null || imageView.getDrawable() == null) {
            return rect;
        }

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int widthActual = Math.round(origW * scaleX);
        final int heightActual = Math.round(origH * scaleY);

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - heightActual)/2;
        int left = (int) (imgViewW - widthActual)/2;

        rect.set(left, top, left + widthActual, top + heightActual);

        return rect;
    }
}

