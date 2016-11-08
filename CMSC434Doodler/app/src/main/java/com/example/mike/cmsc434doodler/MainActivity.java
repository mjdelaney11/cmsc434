package com.example.mike.cmsc434doodler;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    private SeekBar sizeBar;
    private SeekBar redBar;
    private SeekBar greenBar;
    private SeekBar blueBar;
    private SeekBar alphaBar;
    private TextView sizeText;
    private DoodleView doodle;
    private ToggleButton drawOrErase;
    public static boolean erase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);

        sizeBar = (SeekBar)findViewById(R.id.size);
        redBar = (SeekBar)findViewById(R.id.red);
        greenBar = (SeekBar)findViewById(R.id.green);
        blueBar = (SeekBar)findViewById(R.id.blue);
        alphaBar = (SeekBar)findViewById(R.id.alpha);
        sizeText = (TextView)findViewById(R.id.textSize);
        doodle = (DoodleView)findViewById(R.id.doodle);
        drawOrErase = (ToggleButton)findViewById(R.id.drawOrErase);

        drawOrErase.setChecked(true);
        drawOrErase.setBackgroundColor(getColor());
        int prog = alphaBar.getProgress();
        alphaBar.setBackgroundColor(rgb(255-prog, 255-prog, 255-prog));


        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int prog = 50;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prog = progress;
                sizeText.setTextSize((float)progress/4.0f);

            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                doodle.setSize((float)prog);
            }
        });
        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double d = progress/255.0;
                int i = 255-progress;
                redBar.setBackgroundColor(rgb((int)(d*204) + i, (int)(d*24) + i, (int)(d*30)+ i));
                if(!erase){
                    drawOrErase.setBackgroundColor(getColor());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                doodle.setPaintColor(getColor());
            }
        });
        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double d = progress/255.0;
                int i = 255-progress;
                blueBar.setBackgroundColor(rgb((int)(d*59) + i, (int)(d*89) + i, (int)(d*152)+ i));
                if(!erase){
                    drawOrErase.setBackgroundColor(getColor());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                doodle.setPaintColor(getColor());
            }
        });
        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double d = progress/255.0;
                int i = 255-progress;
                greenBar.setBackgroundColor(rgb((int)(d*110) + i, (int)(d*180) + i, (int)(d*10)+ i));
                if(!erase){
                    drawOrErase.setBackgroundColor(getColor());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                doodle.setPaintColor(getColor());
            }
        });
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alphaBar.setBackgroundColor(rgb(255-progress, 255-progress, 255-progress));
                if(!erase){
                    drawOrErase.setBackgroundColor(getColor());
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                doodle.setPaintColor(getColor());
            }
        });
    }

    public void onClickClearScreen(View v){
        if(doodle != null){
            doodle.clear();
        }

    }

    public void onClickToggleDrawAndErase(View v){
        if(drawOrErase == null){
            return;
        }

        String dOrE = drawOrErase.getText().toString();

        if(dOrE.equals("Erase")){
            erase = true;
            drawOrErase.setBackgroundColor(rgb(246, 162, 185));
            doodle.setPaintColor(Color.WHITE);
        } else {
            erase = false;
            drawOrErase.setBackgroundColor(getColor());
            doodle.setPaintColor(getColor());
        }
    }

    public void onClickUndo(View v){
        doodle.undo();
    }

    public int getColor(){
        if(erase){
            return Color.argb(255, 255, 255, 255);
        }

        int red = redBar.getProgress();
        int green = greenBar.getProgress();
        int blue = blueBar.getProgress();
        int alpha = alphaBar.getProgress();

        return Color.argb(alpha, red, green, blue);
    }
}
