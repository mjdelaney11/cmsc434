package com.example.mike.cmsc434doodler;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Mike on 11/7/2016.
 */

public class myPath {

    private Path path;
    private Paint paint;

    public myPath(Path path, Paint paint){
        this.path = path;
        this.paint = paint;
    }

    public void setPaint(Paint newPaint){
        this.paint = newPaint;
    }

    public Path getPath(){
        return this.path;
    }

    public Paint getPaint(){
        return this.paint;
    }
}
