package com.GP.crazylabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class LabyrinthView extends View {

    private Labyrinth _labyrinth;

    /**
     * Constructor
     */
    public LabyrinthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    //@Override
    protected void OnDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    public void CreateRandomLabyrinth() {

    }
}
