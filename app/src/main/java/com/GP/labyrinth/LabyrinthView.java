package com.GP.labyrinth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.GP.coordinates.Grid;
import com.GP.crazylabyrinth.R;
import com.GP.labyrinth.LabyrinthModel;

public class LabyrinthView extends View {
    private Bitmap _labyrinthBitmap;
    private Bitmap keyBitmap;
    private Bitmap ballBitmap;
    private Bitmap shadowBitmap;
    private Bitmap cover;
    private Bitmap _ballBitmap;
    private float scaleFactor;
    private LabyrinthModel labyrinth;
    /**
     * Constructors
     */
    public LabyrinthView(Context context) {
        super(context);

    }

    public LabyrinthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Renders the labyrinth
     * @param labyrinthModelParam labyrinth to be rendered
     */
    public void DrawLabyrinth(@NonNull LabyrinthModel labyrinthModelParam, int WidthParam, int HeightParam) {
        this.labyrinth = labyrinthModelParam;

        float _scaleFactorWidth = (float) WidthParam / (labyrinthModelParam.Width * 100);
        float _scaleFactorHeight = (float) HeightParam / (labyrinthModelParam.Height * 100);

        if(_scaleFactorHeight > _scaleFactorWidth) {
            scaleFactor = _scaleFactorWidth;
        } else {
            scaleFactor = _scaleFactorHeight;
        }

        Canvas _canvas;
        Paint _paint = new Paint();

        this._labyrinthBitmap = Bitmap.createBitmap(labyrinthModelParam.Width*100, labyrinthModelParam.Height*100, Bitmap.Config.ARGB_8888);

        if(labyrinthModelParam.Level == LabyrinthModel.Difficulty.MEDIUM || labyrinthModelParam.Level == LabyrinthModel.Difficulty.HARD) {
            this.keyBitmap = Bitmap.createBitmap(labyrinthModelParam.Width*100, labyrinthModelParam.Height*100, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(this.keyBitmap);
            _canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.key), 100, 100, false),  labyrinthModelParam.Key.X * 100, labyrinthModelParam.Key.Y * 100, null);
        } else {
            this.keyBitmap = null;
        }

        if(labyrinthModelParam.Level == LabyrinthModel.Difficulty.HARD) {
            this.shadowBitmap = Bitmap.createBitmap(labyrinthModelParam.Width*100, labyrinthModelParam.Height*100, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(this.shadowBitmap);
            _paint = new Paint(Color.DKGRAY);
            _canvas.drawRect(0, 0, (labyrinthModelParam.Width * 100) - 1, (labyrinthModelParam.Height * 100) - 1, _paint);
        }
        _canvas = new Canvas(_labyrinthBitmap);
        _paint.setColor(Color.rgb(255,255,0));
        _canvas.drawRect(0,0, (labyrinthModelParam.Width*100 - 1), (labyrinthModelParam.Height*100 - 1), _paint);
       // _paint.setColor(Color.LTGRAY);
        //_canvas.drawRect(5, 5, (labyrinthModelParam.Width*100) - 5, (labyrinthModelParam.Height*100) - 5, _paint);
        _paint.setColor(Color.rgb(0, 0, 128));
        for(int i = 0; i < labyrinthModelParam.Height; i++) {

            for(int j = 0; j < labyrinthModelParam.Width; j++) {
                _canvas.drawRect(j * 100 + 4, i * 100 + 4, j * 100 + 94, i * 100 + 94, _paint);
                if(labyrinthModelParam.Cells[j][i].WayRight) {
                    _canvas.drawRect(j * 100 + 50, i * 100 + 4, j * 100 + 149, i * 100 + 94, _paint);

                }
                if(labyrinthModelParam.Cells[j][i].WayDown) {
                    _canvas.drawRect(j * 100 + 4, i * 100 + 50, j * 100 + 94, i * 100 + 149, _paint);
                }

                if(labyrinthModelParam.Start.Equals(new Grid(j, i))) {
                    _paint.setColor(Color.rgb(0, 255, 0));
                    _canvas.drawRect(j * 100 + 4, i * 100 + 4, j * 100 + 94, i * 100 + 94, _paint);
                    _paint.setColor(Color.rgb(0, 0, 128));

                } else if(labyrinthModelParam.End.Equals(new Grid(j, i))) {
                    _paint.setColor(Color.RED);
                    _canvas.drawRect(j * 100 + 4, i * 100 + 4, j * 100 + 94, i * 100 + 94, _paint);
                    _paint.setColor(Color.rgb(0, 0 , 128));

                }
            }
        }
    }

    public void RemoveKey() {
        this.keyBitmap = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap _bitmap = Bitmap.createBitmap(this.labyrinth.Width * 100, this.labyrinth.Height * 100, Bitmap.Config.ARGB_8888);
        Canvas _canvas = new Canvas(_bitmap);

        if(_labyrinthBitmap != null) {
            _canvas.drawBitmap(_labyrinthBitmap, 0, 0, null);
        }

        if(keyBitmap != null) {
            _canvas.drawBitmap(this.keyBitmap, 0, 0, null);
        }
        _canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ball), (int)(100), (int)(100), false), (labyrinth.Ball.Position.X * 100) - 50, (labyrinth.Ball.Position.Y * 100) - 50, null);

        if(this.labyrinth.Level == LabyrinthModel.Difficulty.HARD) {
            Canvas _shadow = new Canvas(this.shadowBitmap);
            Paint _paint = new Paint();
            _paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            _shadow.drawCircle((this.labyrinth.Ball.Position.X * 100), (this.labyrinth.Ball.Position.Y * 100), 150, _paint);
            _canvas.drawBitmap(this.shadowBitmap, 0, 0, null);
        }
        canvas.drawBitmap(Bitmap.createScaledBitmap(_bitmap, (int)(labyrinth.Width * 100 * this.scaleFactor), (int)(this.labyrinth.Height * 100 * this.scaleFactor), true), 0, 0, null);

    }
}
